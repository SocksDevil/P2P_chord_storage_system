package com.feup.sdis.actions;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.requests.InitRequest;
import com.feup.sdis.peer.Constants;

public class Init extends Action{
    private final InitRequest message;

    public Init(InitRequest message) {
        this.message = message;
    }

    @Override
    public String process() {
        this.sendMessage(message, new SocketAddress(Constants.SERVER_IP, Constants.SERVER_PORT));
        return "Sent Connection to Server";
    }
}
