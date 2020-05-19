package com.feup.sdis.model;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.requests.Request;
import com.feup.sdis.peer.Constants;

public class RequestInfo {

    private final Request request;
    private final SocketAddress address;
    private int retries;

    public RequestInfo(Request request, SocketAddress address) {
        this.request = request;
        this.address = address;
        this.retries = Constants.MAX_REQUEST_RETRIES;
    }

    public Request getRequest() {
        return request;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public int getRetries() {
        return retries;
    }

    public void decrementRetries() {
        this.retries -= 1;
    }

    public boolean reachedMaxRetries() {
        return retries == 0;
    }
}
