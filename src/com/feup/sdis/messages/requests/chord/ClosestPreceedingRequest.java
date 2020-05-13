package com.feup.sdis.messages.requests.chord;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.Request;
import com.feup.sdis.messages.responses.chord.ClosestPreceedingResponse;
import com.feup.sdis.messages.responses.Response;

public class ClosestPreceedingRequest extends Request {

    private SocketAddress addressInfo;

    public ClosestPreceedingRequest(SocketAddress connection){

        this.addressInfo = connection;
    }

    @Override
    public Response handle() {
        
        SocketAddress preeceding = Chord.chordInstance.closestPreceedingNode(addressInfo.getPeerID());

        return new ClosestPreceedingResponse(Status.SUCCESS, preeceding);
    }

    @Override
    public SocketAddress getConnection() {
        
        return addressInfo;
    }

    @Override
    public String toString(){
        
        return "req: CHD_CLOSEST_PRECEEDING_NODE " + this.addressInfo;
    }
}