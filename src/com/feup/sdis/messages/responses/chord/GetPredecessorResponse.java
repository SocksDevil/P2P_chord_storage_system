package com.feup.sdis.messages.responses.chord;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.Response;

public class GetPredecessorResponse extends Response {
    private final SocketAddress address;

    public GetPredecessorResponse(Status status, SocketAddress address) {
        super(status);
        this.address = address;
    }

    public SocketAddress getAddress() {
        return address;
    }

    @Override
    public String toString(){
        
        return "res: CHD_GET_PRED " + (this.address == null ? "*No address given*" : this.address  ) + " STATUS: " + this.getStatus();
    }
}
