package com.feup.sdis.actions;

import com.feup.sdis.messages.Message;

public class Restore extends Action {
    private final Message message;

    public Restore(String[] args) {
        message = null;
        // this.message = String.join("", args);
    }

    @Override
    public String process() {
        //TODO: Implement restore
        this.sendMessage(message, null);
        return "Restored file";
    }
}
