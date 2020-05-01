package com.feup.sdis.peer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.messages.MessageHandler;


public class Server {
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    // TODO: depois ver isto
    public static final Chord chord = new Chord();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid number of arguments");
        }

        int port;

        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            System.out.println("Port must be a number");
            return;
        }

        final ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Failed to initialize server on port " + port);
            return;
        }
        Constants.peerRootFolder = Constants.peerParentFolder + "server/";
        Constants.backupFolder = Constants.peerRootFolder + "backups/";
        Constants.restoredFolder = Constants.peerRootFolder + "restored/";
        Server.createPeerFolders();

        while (true) {
            final Socket socket;
            try {
                socket = serverSocket.accept();
                pool.execute(() -> {
                    try {
                        final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        final ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        Object message = in.readObject();
                        if (message == null) {
                            System.out.println("Message not received properly");
                        } else {
                            MessageHandler messageHandler = new MessageHandler(message);
                            pool.execute(messageHandler);
                            // System.out.println(message);
                            // TODO: ---
                            out.println("Opah ya correu bem");
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

    public static void createPeerFolders() {
        if (!(new File(Constants.peerParentFolder)).mkdir()) {
            System.out.println("Folder already exists or failed to be created: " + Constants.peerParentFolder);
        }
        if (!(new File(Constants.peerRootFolder)).mkdir()) {
            System.out.println("Folder already exists or failed to be created: " + Constants.peerRootFolder);
        }
        if (!(new File(Constants.backupFolder)).mkdir()) {
            System.out.println("Folder already exists or failed to be created: " + Constants.backupFolder);
        }
        if (!(new File(Constants.restoredFolder)).mkdir()) {
            System.out.println("Folder already exists or failed to be created: " + Constants.restoredFolder);
        }
    }
}
