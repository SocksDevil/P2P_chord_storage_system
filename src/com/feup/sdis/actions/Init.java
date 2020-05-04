package com.feup.sdis.actions;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.requests.InitRequest;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.MessageListener;

public class Init extends Action{
    private final InitRequest message;

    public Init(InitRequest message) {
        this.message = message;
    }

    @Override
    public String process() {
        MessageListener.sendMessage(message, new SocketAddress(Constants.SERVER_IP, Constants.SERVER_PORT, Constants.peerID));
        return "Sent Connection to Server";
    }
}
