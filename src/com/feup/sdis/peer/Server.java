package com.feup.sdis.peer;

import java.io.*;


public class Server {

    // TODO: depois ver isto
    // public static final Chord chord = new Chord();

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

        MessageListener receiver = new MessageListener(port);
        receiver.receive();
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
