package com.feup.sdis.actions;

public class Backup extends Action{
    private final String message;

    public Backup(String[] args) {
        this.message = String.join("", args);
    }

    @Override
    public String process() {
        //TODO: Implement backup
        this.sendMessage(message);
        return "Backed up file";
    }
}
