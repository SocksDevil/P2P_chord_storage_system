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
    private final int finalSpace;

    public Reclaim (int finalSpace){
        this.finalSpace = finalSpace;
    }

    public Reclaim(String[] args) {
        this(Integer.parseInt(args[1]));
    }

    @Override
    public String process() {
        // Set new peer space
        // TODO: maybe synchronize this?? (because of the increment space function)
        Constants.MAX_OCCUPIED_DISK_SPACE_MB = finalSpace * Constants.MEGABYTE;
        List<Future<?>> returnCodes = new LinkedList<>();
        while (Store.instance().getUsedDiskSpace() > Constants.MAX_OCCUPIED_DISK_SPACE_MB) {
            System.out.println("> RECLAIM: Space " + Store.instance().getUsedDiskSpace() + "/"
                    + Constants.MAX_OCCUPIED_DISK_SPACE_MB);

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
                // TODO Auto-generated catch block - think about it pharrell
                e2.printStackTrace();
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
                // TODO Auto-generated catch block delete call failed
                e1.printStackTrace();
            }
        });
    }
}
