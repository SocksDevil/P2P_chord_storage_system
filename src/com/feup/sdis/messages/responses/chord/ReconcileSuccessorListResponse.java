package com.feup.sdis.messages.responses.chord;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.Response;

public class ReconcileSuccessorListResponse extends Response {

    final SocketAddress[] successorList;

    public ReconcileSuccessorListResponse(Status status, SocketAddress[] successorList) {
        super(status);
        this.successorList = successorList; 
    }

    public SocketAddress[] getSuccessorList(){

        return this.successorList;
    }


}
