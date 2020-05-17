package com.feup.sdis.peer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.requests.Request;

public class MessageListener {

    private static final boolean DEBUG_MODE = false;
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static int port;

    public MessageListener(int port) {

        MessageListener.port = port;
    }

    public void receive() {

        final AsynchronousServerSocketChannel serverSocket;
        try {
            serverSocket = AsynchronousServerSocketChannel.open(AsynchronousChannelGroup.withCachedThreadPool(pool, 1));
            serverSocket.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            System.out.println("Failed to initialize server on port " + port);
            return;
        }

        while (true) {
            serverSocket.accept(null, new CompletionHandler<>() {
                @Override
                public void completed(AsynchronousSocketChannel socket, Object attachment) {
                    if (serverSocket.isOpen())
                        serverSocket.accept(null, this);

                    if(socket != null && socket.isOpen()){
                        Request request = SerializationUtils.deserialize(socket);
                        if (request == null){
                            System.out.println("* Request as null.");
                            return;
                        }

                        Response response = request.handle();

                        socket.write(SerializationUtils.serialize(response));

                        try {
                            socket.shutdownOutput();
                            socket.close();
                        } catch (IOException e) {
                            // if(DEBUG_MODE )
                                System.out.println("* Socket shutdown/close failed on MessageListener.");
                        }
                    }
                }

                @Override
                public void failed(Throwable throwable, Object att) {
                    // if(DEBUG_MODE )
                        System.out.println("* Socket accept failed on MessageListener.");
                }
            });
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T extends Response> T sendMessage(Request request, SocketAddress destination) {
        try {

            if(destination == null)
                return null;
            AsynchronousSocketChannel socket = AsynchronousSocketChannel.open();
            Future<Void> future = socket.connect(new InetSocketAddress(destination.getIp(), destination.getPort()));
            future.get();

            Future<Integer> writeResult = socket.write(SerializationUtils.serialize(request));
            socket.shutdownOutput();
            writeResult.get();

            if (DEBUG_MODE)
                System.out.println("* OUT > " + request + " to " + destination.getIp() + ":" + destination.getPort());

            T receivedMessage = SerializationUtils.deserialize(socket);

            if (DEBUG_MODE)
                System.out.println("* IN  > " + (receivedMessage != null ? receivedMessage : "-------") + " from " + destination.getIp() + ":" + destination.getPort());

            socket.shutdownInput();
            socket.close();

            return receivedMessage;
        } catch (IOException ex) {
            if(DEBUG_MODE )
                System.out.println("* IOException on sendMessage.");
        }
        catch(ExecutionException ex){
            if(DEBUG_MODE )
                System.out.println("* ExecutionException on sendMessage.");
        }
        catch(InterruptedException ex){
            if(DEBUG_MODE)
                System.out.println("* InterruptedException on sendMessage.");
        }

        return null;
    }

}