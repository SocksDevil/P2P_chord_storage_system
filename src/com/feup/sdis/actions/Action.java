package com.feup.sdis.actions;

import com.feup.sdis.messages.Message;
import com.feup.sdis.peer.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class Action {
    public void sendMessage(Message message) {
        try {
            final Socket socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
            final ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.writeObject(message);
            socket.shutdownOutput();
            String receivedMessage = in.readLine();
            if(receivedMessage == null){
                System.out.println("Message not received properly");
            } else {
                System.out.println(receivedMessage);
            }
            while (in.readLine() != null) {}
            socket.shutdownInput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract String process();
}
