package com.feup.sdis.actions;

import com.feup.sdis.messages.InitMessage;

public class Init extends Action{
    private final InitMessage message;

    public Init(InitMessage message) {
        this.message = message;
    }

    @Override
    public String process() {
        this.sendMessage(message);
        return "Sent Connection to Server";
    }
}
