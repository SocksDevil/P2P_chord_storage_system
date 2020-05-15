package com.feup.sdis.messages.responses;

import com.feup.sdis.messages.Status;

public class NullResponse extends Response {

    public NullResponse() {
        super(Status.ERROR);
    }

    public NullResponse(Status stat){
        super(stat);
    }
}