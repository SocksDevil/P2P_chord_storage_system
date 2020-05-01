package com.feup.sdis.messages;

import com.feup.sdis.chord.Connection;
import com.feup.sdis.peer.Server;

public class InitMessage extends Message {

    private Connection connection;

    public InitMessage(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void handle() {
        Server.chord.addConnection(connection);
        System.out.println("FingerTable:\n" +  Server.chord.toString());
    }

    @Override
    public String toString(){
        return "INIT " + this.connection;
    }


}