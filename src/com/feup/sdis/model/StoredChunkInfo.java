package com.feup.sdis.model;

import com.feup.sdis.peer.Constants;

import java.io.*;

public class StoredChunkInfo implements Serializable {

    final private String fileID;
    final int desiredReplicationDegree;
    final int chunkNo;
    final int chunkSize;
    final private int nChunks;
    private final String originalFilename;

    public StoredChunkInfo(String fileID, int desiredReplicationDegree,
                           int chunkNo, int chunkSize, int nChunks, String originalFilename) {
        this.fileID = fileID;
        this.desiredReplicationDegree = desiredReplicationDegree;
        this.chunkNo = chunkNo;
        this.chunkSize = chunkSize;
        this.nChunks = nChunks;
        this.originalFilename = originalFilename;
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
        (new FileOutputStream(Constants.backupFolder + getChunkID())).write(body);
    }

    public byte[] getBody() throws IOException {
        return (new FileInputStream(Constants.backupFolder + getChunkID())).readAllBytes();
    }

    public int getnChunks() {
        return nChunks;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public static String getChunkID(String fileID, int chunkNo){
        return fileID + "#" + chunkNo;
    }

    public String getChunkID(){
        return StoredChunkInfo.getChunkID(fileID, chunkNo);
    }
}