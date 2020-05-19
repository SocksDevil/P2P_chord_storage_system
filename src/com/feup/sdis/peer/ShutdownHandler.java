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
        int chunkCount = chunks.size();
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
        int successfullyDeletedChunks = 0;
        Store store = Store.instance();

        // For each successful transfer, delete stored chunk
        for (Map.Entry<String, StoredChunkInfo> chunkEntry : chunks.entrySet()) {

            if (backupReturnCodes.get(i) !=  null){
                i++;
                continue;
            }
            System.out.println("> SHUTDOWN: Successfully transfered chunk " + chunkEntry.getKey());
            
            // Update storage structures
            store.incrementSpace(-1 * chunkEntry.getValue().getChunkSize());
            store.getStoredFiles().remove(chunkEntry.getKey());
            
            // Update non-volatile memory
            final File fileToDelete = new File(Constants.backupFolder + chunkEntry.getValue().getChunkID());
            if (!fileToDelete.exists()) {
                System.out.println("> DELETE: Could not find chunk " + chunkEntry.getValue().getChunkID() + " on disk");
                
            }
            // TODO: ver se este for o erro se não convinha por as cenas nas dbs de novo
            else if (!fileToDelete.delete()) {
                System.out.println("> DELETE: Failed to delete chunk " + chunkEntry.getValue().getChunkID());
            }

            i++;
            successfullyDeletedChunks++;
        }

        System.out.println("> SHUTDOWN: Transfer complete, " + (chunkCount - successfullyDeletedChunks) + " chunks remaining.");

    }

}