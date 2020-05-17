package com.feup.sdis.model;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.requests.chord.TakeChunkRequest;

public class ChunkTransfer {
    private final SocketAddress address;
    private final TakeChunkRequest request;

    public ChunkTransfer(SocketAddress address, TakeChunkRequest request) {
        this.address = address;
        this.request = request;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public TakeChunkRequest getRequest() {
        return request;
    }
}
