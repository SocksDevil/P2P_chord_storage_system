package com.feup.sdis.actions;

public class Reclaim extends Action {
    private final String message;

    public Reclaim(String[] args) {
        this.message = String.join("", args);
    }

    @Override
    public String process() {
        //TODO: Implement reclaim
        this.sendMessage(message);
        return "Reclaimed space";
    }
}
