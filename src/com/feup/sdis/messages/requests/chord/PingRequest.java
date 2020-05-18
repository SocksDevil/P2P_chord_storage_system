package com.feup.sdis.messages.requests.chord;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.Request;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.messages.responses.chord.PingResponse;

public class PingRequest extends Request {

    public PingRequest() {

    }

    @Override
    public Response handle() {

        return new PingResponse(Status.SUCCESS);
    }

    @Override
    public SocketAddress getConnection() {

        return null;
    }

    @Override
    public String toString(){
        
        return "req: CHD_PING ";
    }
}