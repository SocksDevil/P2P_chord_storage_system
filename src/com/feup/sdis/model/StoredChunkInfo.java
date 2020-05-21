package com.feup.sdis.model;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.peer.Constants;

import java.io.*;

public class StoredChunkInfo implements Serializable {

    final private String fileID;
    int desiredReplicationDegree;
    final int chunkNo;
    final int chunkSize;
    private int nChunks;
    private String originalFilename;
    private SocketAddress initiatorPeer;
    private boolean pendingDeletion;

    public StoredChunkInfo(String fileID, int desiredReplicationDegree,
                           int chunkNo, int chunkSize, int nChunks,
                           String originalFilename, SocketAddress initiatorPeer) {
        this.fileID = fileID;
        this.desiredReplicationDegree = desiredReplicationDegree;
        this.chunkNo = chunkNo;
        this.chunkSize = chunkSize;
        this.nChunks = nChunks;
        this.originalFilename = originalFilename;
        this.initiatorPeer = initiatorPeer;
        this.pendingDeletion = false;
    }

    public StoredChunkInfo(String fileID, int chunkNo, int chunkSize) {
        this.fileID = fileID;
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
        (new FileOutputStream(Constants.backupFolder + getChunkID())).write(body);
    }

    public byte[] getBody() throws IOException {
        return getBody((new FileInputStream(Constants.backupFolder + getChunkID())));
    }

    public static byte[] getBody(FileInputStream file) throws IOException {
        return file.readAllBytes();
    }

    public int getnChunks() {
        return nChunks;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public static String getChunkID(String fileID, int chunkNo) {
        return fileID + Constants.idSeparation + chunkNo;
    }

    public String getChunkID() {
        return StoredChunkInfo.getChunkID(fileID, chunkNo);
    }

    public SocketAddress getInitiatorPeer() {
        return initiatorPeer;
    }

	public boolean pendingDeletion() {
		return this.pendingDeletion;
    }
    
    public void setPendingDeletion(boolean isPending){
        this.pendingDeletion = isPending;
    }
}