package com.feup.sdis.messages.requests.chord;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.Request;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.messages.responses.chord.ReconcileSuccessorListResponse;

public class ReconcileSuccessorListRequest extends Request {

    @Override
    public Response handle() {
        
        return new ReconcileSuccessorListResponse(Status.SUCCESS,Chord.chordInstance.getSuccessorList());
    }

    @Override
    public SocketAddress getConnection() {
        return null;
    }
    
}