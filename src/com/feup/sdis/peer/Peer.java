package com.feup.sdis.peer;

import com.feup.sdis.actions.BSDispatcher;
import com.feup.sdis.actions.Dispatcher;
import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class Peer {

    // TODO: ver isto meu puto
    public static SocketAddress addressInfo;

    /**
     * Peer hostAddress peerID accessPoint [chordEntryAddress]
     * 
     */
    public static void main(String[] args) {

        // // TODO fix argument count because of optional arguments
        // if (args.length != 5) {
        // System.out.println("Invalid number of arguments");
        // return;
        // }

        String peerPort = args[0];
        String peerID = args[1];
        String accessPoint = args[2];

        int port;
        try {
            port = Integer.parseInt(peerPort);
        } catch (NumberFormatException nfe) {
            System.out.println("Port must be a number");
            return;
        }

        Constants.peerID = peerID;
        Constants.SENDER_ID = peerID;
        Constants.MAX_OCCUPIED_DISK_SPACE_MB = Integer.parseInt(args[3]) * Constants.MEGABYTE;
        Constants.peerRootFolder = Constants.peerParentFolder + "peer-" + peerID + "/";
        Constants.backupFolder = Constants.peerRootFolder + "backups/";
        Constants.restoredFolder = Constants.peerRootFolder + "restored/";
        Server.createPeerFolders();

        // TODO: SERVER PORT IS HARDCODED
        Constants.SERVER_PORT = 25565;

        // TODO: com argumentos
        try {
            addressInfo = new SocketAddress(InetAddress.getLocalHost().getHostAddress(), port, peerID);
        } catch (NumberFormatException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (UnknownHostException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        BSDispatcher dispatcher = new BSDispatcher();
        try {
            final Dispatcher stub = (Dispatcher) UnicastRemoteObject.exportObject(dispatcher, 0);
            try {
                LocateRegistry.createRegistry(Constants.RMI_PORT);
            } catch (ExportException e) {
            } // already exists
            final Registry registry = LocateRegistry.getRegistry(Constants.RMI_PORT);
            registry.rebind(accessPoint, stub);

            System.out.println("Starting Peer " + Constants.SENDER_ID);
            System.out.println("Peer " + Constants.SENDER_ID + " ready");

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // TODO: move this to a proper place:

        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {

                // TODO: port number está hardcoded
                MessageListener messageListener = new MessageListener(port);
                messageListener.receive();
            }
        });

        t1.start();

        if (args.length == 5) {

            if (!args[4].contains(":")) {
                System.out.println("Chord entry should be provided in the form ip:number");
            }

            String[] boostrapingArguments = args[4].split(":");
            String bsIP;
            int bsPort;

            try {
                bsIP = boostrapingArguments[0];
                bsPort = Integer.parseInt(boostrapingArguments[1]);
            } catch (NumberFormatException nfe) {
                System.out.println("Port must be a number");
                return;
            }

            SocketAddress ringBoostrapping = new SocketAddress(bsIP, bsPort,
                    UUID.nameUUIDFromBytes(peerID.getBytes()).toString());
            try {
                Chord.chordInstance = new Chord(addressInfo, ringBoostrapping);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {

            Chord.chordInstance = new Chord(addressInfo);
        }

        Chord.chordInstance.initThreads();


    }
}
