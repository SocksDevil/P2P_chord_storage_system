package com.feup.sdis.messages;

import java.io.IOException;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.Constants;

public class BackupMessage extends Message {

    private String fileID;
    private int chunkNo;
    private int desiredRepDegree;
    private SocketAddress connection;
    private int status;
    private byte[] chunkData;

    public BackupMessage(String fileID, int chunkNo, int desiredRepDegree, byte[] data, SocketAddress connection) {

        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.connection = connection;
        this.desiredRepDegree = desiredRepDegree;
        this.chunkData = data;
        // this.repDegree = Integer.parseInt(args[2]);

    }

    @Override
    public Message handle() {

        if(Store.instance().getUsedDiskSpace() + this.chunkData.length > Constants.MAX_OCCUPIED_DISK_SPACE_MB){
            System.out.println("No space. TODO REDIRECT.");
            this.status = 400; // TODO: change the status, only here for debug
            return this;
        }

        StoredChunkInfo newChunk = new StoredChunkInfo(fileID, desiredRepDegree, chunkNo, chunkData.length);
        Store.instance().getStoredFiles().put(fileID + "#" + chunkNo, newChunk);
        
        try {
            newChunk.storeFile(chunkData);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.status = 400; // TODO: change the status, only here for debug
            return this;
        }

        this.status = 200;
        this.chunkData = null; // No need to tranfer the file again
        return this;
    }

    @Override
    public SocketAddress getConnection() {
        // TODO Auto-generated method stub
        return this.connection;
    }

    @Override
    public String toString(){

        return "BACKUP: " + fileID + "#" + chunkNo; 
    }
    
}