package com.feup.sdis.messages.responses.chord;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.Response;

import java.util.concurrent.atomic.AtomicReferenceArray;

public class ReconcileSuccessorListResponse extends Response {

    final AtomicReferenceArray<SocketAddress> successorList;

    public ReconcileSuccessorListResponse(Status status, AtomicReferenceArray<SocketAddress> successorList) {
        super(status);
        this.successorList = successorList; 
    }

    public AtomicReferenceArray<SocketAddress> getSuccessorList(){

        return this.successorList;
    }


}
