package com.feup.sdis.actions;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Message;
import com.feup.sdis.peer.Constants;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class Action {
    
    public Message sendMessage(Message message, SocketAddress destination) {
        try {
            final Socket socket = new Socket(destination.getIp(), destination.getPort());
            final ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            final ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(message);
            System.out.println("Message sent : " + message);
            socket.shutdownOutput();
            Message receivedMessage = (Message) in.readObject();
            System.out.println("Message received : " + receivedMessage);

            // while (in.readLine() != null) {}
            socket.shutdownInput();
            socket.close();

            return receivedMessage;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }

    abstract String process();
}
