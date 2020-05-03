package com.feup.sdis.model;

import com.feup.sdis.peer.Constants;

import java.io.*;

public class StoredChunkInfo implements Serializable {

    final private String fileID;
    final int desiredReplicationDegree;
    final int chunkNo;
    final int chunkSize;



    public StoredChunkInfo(String fileID, int desiredReplicationDegree, int chunkNo, int chunkSize) {
        this.fileID = fileID;
        this.desiredReplicationDegree = desiredReplicationDegree;
        this.chunkNo = chunkNo;
        this.chunkSize = chunkSize;
    }

    public String getFileID() {
        return fileID;
    }

    public int getDesiredReplicationDegree() {
        return desiredReplicationDegree;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void storeFile(byte[] body) throws IOException {
        (new FileOutputStream(Constants.backupFolder + fileID + "#" + chunkNo)).write(body);
    }

    public byte[] getBody() throws IOException {
        return (new FileInputStream(Constants.backupFolder + fileID + "#" + chunkNo)).readAllBytes();
    }
}