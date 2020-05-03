package com.feup.sdis.messages.responses;

import com.feup.sdis.messages.Status;

public class ChunkResponse extends Response {
    private final byte[] data;
    private final String fileID;
    private final int chunkNo;

    public ChunkResponse(byte[] data, String fileID, int chunkNo) {
        super(Status.SUCCESS);
        this.data = data;
        this.fileID = fileID;
        this.chunkNo = chunkNo;
    }

    public ChunkResponse(Status status, String fileID, int chunkNo) {
        super(status);
        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.data = null;
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
}
