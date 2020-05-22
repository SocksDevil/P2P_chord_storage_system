package com.feup.sdis.actions;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.Peer;
import com.feup.sdis.peer.Constants;

public class Reclaim extends Action {
    private final float finalSpace;

    public Reclaim (float finalSpace){
        this.finalSpace = finalSpace;
    }

    public Reclaim(String[] args) {
        this(Float.parseFloat(args[1]));
    }

    @Override
    public String process() {
        // Set new peer space
        Constants.MAX_OCCUPIED_DISK_SPACE = (int) finalSpace * Constants.MEGABYTE;
        List<Future<?>> returnCodes = new LinkedList<>();
        while (Store.instance().getUsedDiskSpace() > Constants.MAX_OCCUPIED_DISK_SPACE) {
            System.out.println("> RECLAIM: Space " + Store.instance().getUsedDiskSpace() + "/"
                    + Constants.MAX_OCCUPIED_DISK_SPACE);

            StoredChunkInfo chunkInfo = Store.instance().getChunkCandidate();
            Store.instance().decrementSpace(chunkInfo.getChunkSize());

            Integer currRepDegree = Store.instance().getReplCount().getRepDegree(chunkInfo.getChunkID(), Peer.addressInfo);

           returnCodes.add(this.passChunk(chunkInfo, currRepDegree));
        }

        returnCodes.forEach(backupCall -> {
            try {
                backupCall.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        return "Reclaimed space";
    }

    private Future<?> passChunk(StoredChunkInfo chunkInfo, Integer currRepDegree){
        return BSDispatcher.servicePool.submit(() -> {
            String chunkID = chunkInfo.getChunkID();
            byte[] chunkData = null;
            try {
                chunkData = chunkInfo.getBody();
            } catch (IOException e2) {
                System.out.println("Failed to get chunk body!");
                return;
            }
            System.out.println("> RECLAIM: Delete chunk " + chunkID + " rep " + currRepDegree + " and redirects");
            Future<Boolean> deleteCall = Delete.deleteChunk(chunkInfo.getChunkNo(), currRepDegree, chunkInfo.getFileID());

            try {
                deleteCall.get();
                System.out.println("> RECLAIM: Backing up chunk " + chunkID + " rep " + currRepDegree);
                var chunkBackup = new ChunkBackup(chunkInfo.getFileID(), chunkInfo.getChunkNo(),
                                                        currRepDegree, chunkData, chunkInfo.getnChunks(),
                                                        chunkInfo.getDesiredReplicationDegree(), chunkInfo.getOriginalFilename());
                BSDispatcher.servicePool.submit(chunkBackup).get();
            } catch (InterruptedException | ExecutionException e1) {
                System.out.println("Failed to delete chunk!");
            }
        });
    }
}
