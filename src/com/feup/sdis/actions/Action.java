package com.feup.sdis.actions;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.requests.Request;
import com.feup.sdis.messages.responses.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class Action {
    
    public <T extends Response> T sendMessage(Request request, SocketAddress destination) {
        try {
            final Socket socket = new Socket(destination.getIp(), destination.getPort());
            final ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            final ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(request);
            System.out.println("Message sent : " + request);
            socket.shutdownOutput();
            T receivedMessage = (T) in.readObject();
            System.out.println("Message received : " + receivedMessage);

            // while (in.readLine() != null) {}
            socket.shutdownInput();
            socket.close();

            return receivedMessage;
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    abstract String process();
}
