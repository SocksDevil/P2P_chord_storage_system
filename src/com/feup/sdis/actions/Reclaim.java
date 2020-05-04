package com.feup.sdis.actions;

import com.feup.sdis.messages.requests.Request;
import com.feup.sdis.peer.MessageListener;

public class Reclaim extends Action {
    private final Request request;

    public Reclaim(String[] args) {
        request = null;

        // this.message = String.join("", args);
    }

    @Override
    public String process() {
        //TODO: Implement reclaim
        MessageListener.sendMessage(request, null);
        return "Reclaimed space";
    }
}
