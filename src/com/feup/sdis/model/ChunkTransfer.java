package com.feup.sdis.model;

import com.feup.sdis.messages.requests.chord.TakeChunkRequest;

import java.io.Serializable;

public class ChunkTransfer implements Serializable {
    private final TakeChunkRequest request;
    private final int chunkSize;

    public ChunkTransfer(TakeChunkRequest request, int chunkSize) {
        this.request = request;
        this.chunkSize = chunkSize;
    }

    public TakeChunkRequest getRequest() {
        return request;
    }

    public int getChunkSize() {
        return chunkSize;
    }
}
