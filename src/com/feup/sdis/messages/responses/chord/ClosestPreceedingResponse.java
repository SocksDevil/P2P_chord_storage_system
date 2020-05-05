package com.feup.sdis.messages.responses.chord;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.Response;

public class ClosestPreceedingResponse extends Response{
    private final SocketAddress address;

    public ClosestPreceedingResponse(Status status, SocketAddress address) {
        super(status);
        this.address = address;
    }

    public SocketAddress getAddress() {
        return address;
    }

    @Override
    public String toString(){
        
        return "res: CHD_CLOSEST_PRECEEDING_NODE " + this.address.getPeerID() + " STATUS: " + this.getStatus();
    }
}
