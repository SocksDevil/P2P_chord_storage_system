package com.feup.sdis.messages.requests.chord;


import java.util.UUID;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.Request;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.messages.responses.chord.FindSuccessorResponse;


public class FindSuccessorRequest extends Request{
    private final UUID key;

    public FindSuccessorRequest(UUID key){

        this.key = key;
    }

    @Override
    public Response handle() {
        
        SocketAddress succ = Chord.chordInstance.findSuccessor(key);

        return new FindSuccessorResponse(Status.SUCCESS, succ);
    }

    @Override
    public SocketAddress getConnection() {
        
        return null;
    }

    @Override
    public String toString(){
        
        return "req: CHD_FIND_SUCC " + key;
    }
}

