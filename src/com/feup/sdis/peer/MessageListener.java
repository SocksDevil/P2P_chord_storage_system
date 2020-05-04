package com.feup.sdis.peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.requests.Request;

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
                            if (messageObj instanceof Request) {

                                Request request = (Request) messageObj;
                                Response answer = request.handle();
                                out.writeObject(answer);

                            }
                        }

                        socket.shutdownOutput();
                        // TODO: why is this here??
                        // while (in.readObject() != null) {}
                        // socket.shutdownInput();

                        socket.close();
                    } catch (IOException | ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T extends Response> T sendMessage(Request request, SocketAddress destination) {
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

}