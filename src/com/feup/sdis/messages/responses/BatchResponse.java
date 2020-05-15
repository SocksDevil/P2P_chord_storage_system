package com.feup.sdis.messages.responses;

import java.util.List;

import com.feup.sdis.messages.Status;

public class BatchResponse extends Response {

    Response[] responses;

    public BatchResponse(Status status, Response[] responses) {
        super(status);
        this.responses = responses;
    }

    public Response[] getResponses(){

        return this.responses;
    }

}
