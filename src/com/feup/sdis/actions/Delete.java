package com.feup.sdis.actions;

public class Delete extends Action {
    private final String message;

    public Delete(String[] args) {
        this.message = String.join("", args);
    }

    @Override
    public String process() {
        //TODO: Implement delete
        this.sendMessage(message);
        return "Deleted file";
    }
}
