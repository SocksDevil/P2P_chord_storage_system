package com.feup.sdis.actions;

import com.feup.sdis.chord.Connection;
import com.feup.sdis.messages.InitMessage;
import com.feup.sdis.peer.Constants;

public class Init extends Action{
    private final InitMessage message;

    public Init(InitMessage message) {
        this.message = message;
    }

    @Override
    public String process() {
        this.sendMessage(message, new Connection(Constants.SERVER_IP, Constants.SERVER_PORT));
        return "Sent Connection to Server";
    }
}
