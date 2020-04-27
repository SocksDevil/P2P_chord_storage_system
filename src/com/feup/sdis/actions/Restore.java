package com.feup.sdis.actions;

public class Restore extends Action {
    private final String message;

    public Restore(String[] args) {
        this.message = String.join("", args);
    }

    @Override
    public String process() {
        //TODO: Implement restore
        this.sendMessage(message);
        return "Restored file";
    }
}
