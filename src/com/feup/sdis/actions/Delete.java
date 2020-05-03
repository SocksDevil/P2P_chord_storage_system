package com.feup.sdis.actions;

import com.feup.sdis.messages.requests.Request;

public class Delete extends Action {
    private final Request request;

    public Delete(String[] args) {
        request = null;
        // this.message = String.join("", args);
    }

    @Override
    public String process() {
        //TODO: Implement delete
        this.sendMessage(request, null);
        return "Deleted file";
    }
}
