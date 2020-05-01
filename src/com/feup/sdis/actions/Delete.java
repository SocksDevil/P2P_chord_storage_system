package com.feup.sdis.actions;

import com.feup.sdis.messages.Message;

public class Delete extends Action {
    private final Message message;

    public Delete(String[] args) {
        message = null;
        // this.message = String.join("", args);
    }

    @Override
    public String process() {
        //TODO: Implement delete
        this.sendMessage(message);
        return "Deleted file";
    }
}
