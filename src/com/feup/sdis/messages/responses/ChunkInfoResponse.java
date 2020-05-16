package com.feup.sdis.messages.responses;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.model.BackupFileInfo;
import com.feup.sdis.model.StoredChunkInfo;


public class ChunkInfoResponse extends Response {
    private final String fileID;
    private final int chunkNo;
    private final int replDegree;
    private final int nChunks;
    private final String originalFilename;
    private final SocketAddress initiatorPeer;

    public ChunkInfoResponse(String fileID, int chunkNo, int replDegree, int nChunks,
                             String originalFilename, SocketAddress initiatorPeer) {
        super(Status.SUCCESS);
        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.replDegree = replDegree;
        this.nChunks = nChunks;
        this.originalFilename = originalFilename;
        this.initiatorPeer = initiatorPeer;
    }

    public ChunkInfoResponse(StoredChunkInfo fileInfo) {
        super(Status.SUCCESS);
        this.fileID = fileInfo.getFileID();
        this.chunkNo = fileInfo.getChunkNo();
        this.replDegree = fileInfo.getDesiredReplicationDegree();
        this.nChunks = fileInfo.getnChunks();
        this.originalFilename = fileInfo.getOriginalFilename();
        this.initiatorPeer = fileInfo.getInitiatorPeer();
    }

    public ChunkInfoResponse(Status status, String fileID, int chunkNo) {
        super(status);
        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.originalFilename = null;
        this.replDegree = -1;
        this.nChunks = -1;
        this.initiatorPeer = null;
    }

    public String getFileID() {
        return fileID;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public int getnChunks() {
        return nChunks;
    }

    public int getReplDegree() {
        return replDegree;
    }

    public SocketAddress getInitiatorPeer() {
        return initiatorPeer;
    }

    @Override
    public String toString() {
        return "ChunkInfoResponse{" +
                "status=" + getStatus() +
                ", fileID='" + fileID + '\'' +
                ", chunkNo=" + chunkNo +
                ", replDegree=" + replDegree +
                ", nChunks=" + nChunks +
                '}';
    }

    public String getOriginalFilename() {
        return originalFilename;
    }
}
