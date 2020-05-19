package com.feup.sdis.peer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.feup.sdis.actions.BSDispatcher;
import com.feup.sdis.actions.ChunkBackup;
import com.feup.sdis.chord.Chord;
import com.feup.sdis.model.SerializableHashMap;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;

public class ShutdownHandler extends Thread {

    @Override
    public void run() {
        System.out.println("> SHUTDOWN: Terminating sequence initiated.");

        // Shutdown chord (stop periodic threads)
        Chord.chordInstance.shutdown();
        System.out.println("> SHUTDOWN: Stopped chord periodic threads.");

        // Shutdown message receiving
        Peer.messageReceiver.interrupt();
        System.out.println("> SHUTDOWN: Stopped message listener.");

        // Send stored chunks to successor (chunk backup protocol with overrided destination)
        SerializableHashMap<StoredChunkInfo> chunks = Store.instance().getStoredFiles();
        System.out.println("> SHUTDOWN: Retrieved chunks from storage(" + chunks.size() + " chunks)");
        System.out.println("> SHUTDOWN: Sending chunks to successor...");

        List<Future<String>> backupCalls = new ArrayList<>();

        for (Map.Entry<String, StoredChunkInfo> chunkEntry : chunks.entrySet()) {

            StoredChunkInfo chunk = chunkEntry.getValue();
            try {
                backupCalls.add(BSDispatcher.servicePool.submit(new ChunkBackup(chunk.getFileID(), chunk.getChunkNo(),
                        0, chunk.getBody(), chunk.getnChunks(), chunk.getDesiredReplicationDegree(),
                        chunk.getOriginalFilename(), Chord.chordInstance.getSuccessor())));
            } catch (IOException e) {
                System.out.println("> SHUTDOWN: Failed to get chunks.");
            }
        }

        // Get status of chunk transfer (null means successful, String means error)
        List<String> backupReturnCodes = backupCalls.stream().map(backupCall -> {
            try {
                return backupCall.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            return "Unknown error!";
        }).collect(Collectors.toList());

        int i = 0;
        int unsucessfulDeletions = 0;

        // For each successful transfer, delete stored chunk
        for (Map.Entry<String, StoredChunkInfo> chunkEntry : chunks.entrySet()) {

            if (backupReturnCodes.get(i) !=  null){
                unsucessfulDeletions++;
                System.out.println("> SHUTDOWN: Failed to transfer chunk " + chunkEntry.getKey());
            }
            else{
                System.out.println("> SHUTDOWN: Successfully transfered chunk " + chunkEntry.getKey());
            }

            i++;
            
        }

        System.out.println("> SHUTDOWN: Transfer complete, " + unsucessfulDeletions + " chunks could not be transfered.");

        final File peerFolder = new File(Constants.peerRootFolder);
        this.deleteDirectory(peerFolder);
        System.out.println("> SHUTDOWN: Deleted file system storage");

    }

    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

}