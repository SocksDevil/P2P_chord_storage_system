package com.feup.sdis.messages.requests;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.BatchResponse;
import com.feup.sdis.messages.responses.Response;

public class BatchRequest extends Request {

    Request[] requests;

    public BatchRequest(Request[] requests){

        this.requests = requests;
    }

    @Override
    public Response handle() {
        
        Response[] responses = new Response[requests.length];
        Status stat = Status.SUCCESS;
        
        for(int i = 0; i < this.requests.length; i++){

            responses[i] = this.requests[i].handle();

            // TODO may not be the best option (opposite of success may not be error ??)
            if(responses[i].getStatus() != Status.SUCCESS)
                stat = Status.ERROR;
        }

        
        Response res = new BatchResponse(stat,responses);

        return res;
    }

    @Override
    public SocketAddress getConnection() {
        return null;
    }
    
}