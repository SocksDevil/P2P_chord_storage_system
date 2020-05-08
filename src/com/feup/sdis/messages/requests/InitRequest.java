package com.feup.sdis.messages.requests;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.peer.Server;

public class InitRequest extends Request {

    private SocketAddress connection;

    public InitRequest(SocketAddress connection) {
        this.connection = connection;
    }

    @Override
    public Response handle() {
        // Server.chord.addConnection(connection);
        // System.out.println( "FingerTable:\n" +  Server.chord.toString());
        return null;
    }

    @Override
    public String toString(){
        return "INIT " + this.connection;
    }

    @Override
    public SocketAddress getConnection() {
        // TODO Auto-generated method stub
        return this.connection;
    }


}