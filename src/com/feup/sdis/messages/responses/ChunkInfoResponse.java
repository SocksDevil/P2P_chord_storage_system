package com.feup.sdis.messages.responses;

import com.feup.sdis.messages.Status;


public class ChunkInfoResponse extends Response {
    private final String fileID;
    private final int chunkNo;
    private final int replDegree;
    private final int nChunks;
    private final String originalFilename;

    public ChunkInfoResponse(String fileID, int chunkNo, int replDegree, int nChunks, String originalFilename) {
        super(Status.SUCCESS);
        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.replDegree = replDegree;
        this.nChunks = nChunks;
        this.originalFilename = originalFilename;
    }

    public ChunkInfoResponse(Status status, String fileID, int chunkNo) {
        super(status);
        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.originalFilename = null;
        this.replDegree = -1;
        this.nChunks = -1;
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
