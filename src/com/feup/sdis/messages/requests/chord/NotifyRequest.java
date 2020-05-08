package com.feup.sdis.messages.requests.chord;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.Request;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.messages.responses.chord.GetPredecessorResponse;
import com.feup.sdis.messages.responses.chord.NotifyResponse;


public class NotifyRequest extends Request{

    private SocketAddress newPred;

    public NotifyRequest(SocketAddress newPred){

        this.newPred = newPred; 
    }

    @Override
    public Response handle() {
        
        Status responseStatus = Chord.chordInstance.notify(newPred) ? Status.SUCCESS : Status.UNCHANGED;

        return new NotifyResponse(responseStatus);
    }

    @Override
    public SocketAddress getConnection() {
        
        return newPred;
    }

    @Override
    public String toString(){
        
        return "req: CHD_NOTIFY " + newPred;
    }
}

