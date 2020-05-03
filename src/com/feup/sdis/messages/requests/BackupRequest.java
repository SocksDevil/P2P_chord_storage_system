package com.feup.sdis.messages.requests;

import java.io.IOException;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.BackupResponse;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.Constants;

public class BackupRequest extends Request {

    private String fileID;
    private int chunkNo;
    private int desiredRepDegree;
    private SocketAddress connection;

    private byte[] chunkData;

    public BackupRequest(String fileID, int chunkNo, int desiredRepDegree, byte[] data, SocketAddress connection) {

        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.connection = connection;
        this.desiredRepDegree = desiredRepDegree;
        this.chunkData = data;
        // this.repDegree = Integer.parseInt(args[2]);
    }

    @Override
    public Response handle() {


        System.out.println("Space " + Store.instance().getUsedDiskSpace() + " - " + this.chunkData.length + " - " + Constants.MAX_OCCUPIED_DISK_SPACE_MB);
        if (!Store.instance().incrementSpace(this.chunkData.length)) {
            System.out.println("No space. TODO REDIRECT. " + Store.instance().getUsedDiskSpace() + " - " + this.chunkData.length + " - " + Constants.MAX_OCCUPIED_DISK_SPACE_MB);
            return new BackupResponse(Status.NO_SPACE);
        }

        System.out.println("Dps do if");
        StoredChunkInfo newChunk = new StoredChunkInfo(fileID, desiredRepDegree, chunkNo, chunkData.length);
        Store.instance().getStoredFiles().put(fileID + "#" + chunkNo, newChunk);
        System.out.println("Stored " + Store.instance().getUsedDiskSpace() + " - " + this.chunkData.length + " - " + Constants.MAX_OCCUPIED_DISK_SPACE_MB);

        try {
            newChunk.storeFile(chunkData);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
           return new BackupResponse(Status.ERROR);
        }

        return new BackupResponse(Status.SUCCESS);
    }

    @Override
    public SocketAddress getConnection() {
        // TODO Auto-generated method stub
        return this.connection;
    }

    @Override
    public String toString() {

        return "BACKUP: " + fileID + "#" + chunkNo;
    }

}