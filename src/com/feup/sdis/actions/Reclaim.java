package com.feup.sdis.actions;

import com.feup.sdis.messages.Message;

public class Reclaim extends Action {
    private final Message message;

    public Reclaim(String[] args) {
        message = null;

        // this.message = String.join("", args);
    }

    @Override
    public String process() {
        //TODO: Implement reclaim
        this.sendMessage(message, null);
        return "Reclaimed space";
    }
}
