package com.feup.sdis.messages.responses;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;

public class ChordClosestPreceedingResponse extends Response{
    private final SocketAddress address;

    public ChordClosestPreceedingResponse(Status status, SocketAddress address) {
        super(status);
        this.address = address;
    }

    public SocketAddress getAddress() {
        return address;
    }
}
