package com.feup.sdis.peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.feup.sdis.messages.Message;

public class MessageListener {
    
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static int port;

    public MessageListener(int port){

        this.port = port;
    }

    public void receive(){

        final ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Failed to initialize server on port " + port);
            return;
        }

        while (true) {
            final Socket socket;
            try {
                socket = serverSocket.accept();
                pool.execute(() -> {
                    try {

                        final ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        final ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        Object messageObj = in.readObject();
                        if (messageObj == null) {
                            System.out.println("Message not received properly");
                        } else {

                            System.out.println("Received: " + messageObj);
                            if (messageObj instanceof Message) {

                                Message message = (Message) messageObj;
                                Message answer = message.handle();
                                out.writeObject(answer);

                            }
                        }

                        socket.shutdownOutput();
                        // TODO: why is this here??
                        // while (in.readObject() != null) {}
                        // socket.shutdownInput();

                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}