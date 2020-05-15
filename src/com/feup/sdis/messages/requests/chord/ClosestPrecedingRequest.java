package com.feup.sdis.messages.requests.chord;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.Request;
import com.feup.sdis.messages.responses.chord.ClosestPrecedingResponse;
import com.feup.sdis.messages.responses.Response;

public class ClosestPrecedingRequest extends Request {

    private SocketAddress addressInfo;

    public ClosestPrecedingRequest(SocketAddress connection){

        this.addressInfo = connection;
    }

    @Override
    public Response handle() {
        
        SocketAddress preeceding = Chord.chordInstance.closestPrecedingNode(addressInfo.getPeerID());

        return new ClosestPrecedingResponse(Status.SUCCESS, preeceding);
    }

    @Override
    public SocketAddress getConnection() {
        
        return addressInfo;
    }

    @Override
    public String toString(){
        
        return "req: CHD_CLOSEST_PRECEDING_NODE " + this.addressInfo;
    }
}