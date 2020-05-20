package com.feup.sdis.model;

import com.feup.sdis.peer.Constants;

import java.util.concurrent.Callable;

public class RequestRetryInfo {

    private final Callable<Boolean> request;
    private int retries;

    public RequestRetryInfo(Callable<Boolean> r) {
        this.request = r;
        this.retries = Constants.MAX_REQUEST_RETRIES;
    }

    public Callable<Boolean> getRequest() {
        return request;
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
