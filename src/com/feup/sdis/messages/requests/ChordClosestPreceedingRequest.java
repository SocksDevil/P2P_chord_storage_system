package com.feup.sdis.messages.requests;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.ChordClosestPreceedingResponse;
import com.feup.sdis.messages.responses.Response;

public class ChordClosestPreceedingRequest extends Request {

    private SocketAddress addressInfo;

    public ChordClosestPreceedingRequest(SocketAddress connection){

        this.addressInfo = connection;
    }

    @Override
    public Response handle() {
        
        SocketAddress preeceding = Chord.chordInstance.closestPreceedingNode(addressInfo.getPeerID());

        return new ChordClosestPreceedingResponse(Status.SUCCESS, preeceding);
    }

    @Override
    public SocketAddress getConnection() {
        
        return addressInfo;
    }



    
}