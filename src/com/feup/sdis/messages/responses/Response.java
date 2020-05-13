package com.feup.sdis.messages.responses;

import com.feup.sdis.messages.Status;

import java.io.Serializable;

public abstract class Response implements Serializable {
    private final Status status;

    public Response(Status status){
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
