package com.feup.sdis.messages.responses;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;


public class ChunkResponse extends Response {
    private final byte[] data;
    private final String fileID;
    private final int chunkNo;
    private final int replDegree;
    private final int nChunks;
    private final String originalFilename;
    private final SocketAddress initiatorPeer;

    public ChunkResponse(byte[] data, String fileID, int chunkNo, int replDegree, int nChunks, String originalFilename, SocketAddress initiatorPeer) {
        super(Status.SUCCESS);
        this.data = data;
        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.replDegree = replDegree;
        this.nChunks = nChunks;
        this.originalFilename = originalFilename;
        this.initiatorPeer = initiatorPeer;
    }

    public ChunkResponse(Status status, String fileID, int chunkNo) {
        super(status);
        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.originalFilename = null;
        this.data = null;
        this.replDegree = -1;
        this.nChunks = -1;
        this.initiatorPeer = null;
    }

    public byte[] getData() {
        return data;
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

    @Override
    public String toString() {
        return "ChunkResponse{" +
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

    public SocketAddress getInitiatorPeer() {
        return initiatorPeer;
    }
}
