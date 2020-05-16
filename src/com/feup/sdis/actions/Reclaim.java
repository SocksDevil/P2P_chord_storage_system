package com.feup.sdis.actions;

import java.io.File;
import java.io.IOException;

import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.Peer;
import com.feup.sdis.peer.Constants;

public class Reclaim extends Action {
    private final int finalSpace;

    public Reclaim(String[] args) {
        finalSpace = Integer.parseInt(args[1]);
    }

    @Override
    public String process() {
        // Set new peer space
        // TODO: maybe synchronize this?? (because of the increment space function)
        Constants.MAX_OCCUPIED_DISK_SPACE_MB = finalSpace * Constants.MEGABYTE;

        while (Store.instance().getUsedDiskSpace() > Constants.MAX_OCCUPIED_DISK_SPACE_MB) {
            StoredChunkInfo chunkInfo = Store.instance().popChunkFromStored();
            String chunkID = chunkInfo.getChunkID();
            Integer currRepDegree = Store.instance().getReplCount().removePeerID(chunkID, Peer.addressInfo);

            Store.instance().incrementSpace(-1 * chunkInfo.getChunkSize());

            System.out.println("> RECLAIM: Backing up to ensure rep degree" + chunkID);

            try {
                BSDispatcher.servicePool.execute(new ChunkBackup(chunkInfo.getFileID(), chunkInfo.getChunkNo(),
                        currRepDegree, chunkInfo.getBody(), chunkInfo.getnChunks(),
                        chunkInfo.getDesiredReplicationDegree(), chunkInfo.getOriginalFilename()));
            } catch (IOException e) {
                // TODO: ver se este for o erro se não convinha por as cenas nas dbs de novo
                e.printStackTrace();
            }

            final File fileToDelete = new File(Constants.backupFolder + chunkID);
            if(!fileToDelete.exists())
                System.out.println("> RECLAIM: Could not find chunk " + chunkID + " on disk, deleted from stored");
            // TODO: ver se este for o erro se não convinha por as cenas nas dbs de novo
            else if(!fileToDelete.delete())
                System.out.println("> RECLAIM: Failed to delete chunk " + chunkID + " from disk");
        }
        
        return "Reclaimed space";
    }
}
