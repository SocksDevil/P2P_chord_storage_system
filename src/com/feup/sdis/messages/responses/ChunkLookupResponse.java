package com.feup.sdis.messages.responses;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;

public class ChunkLookupResponse extends Response {

    private final SocketAddress address;

    public ChunkLookupResponse(Status status, SocketAddress address) {
        super(status);
        this.address = address;
    }

    public SocketAddress getAddress() {
        return address;
    }

    @Override
    public String toString(){
        return "CHUNK LOOKUP: " + this.address + " STATUS: " + this.getStatus();
    }

}