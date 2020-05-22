package com.feup.sdis.peer;

import com.feup.sdis.actions.BSDispatcher;
import com.feup.sdis.actions.Dispatcher;
import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.model.Store;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.lang.Runtime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Peer {

    public static SocketAddress addressInfo;
    public static Thread messageReceiver;

    /**
     * Peer hostAddress peerID accessPoint [chordEntryAddress]
     */
    public static void main(String[] args) {

        if (args.length < 4 || args.length > 5) {
            System.out.println("Invalid number of arguments");
            return;
        }

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
        createPeerFolders();

        try {
            addressInfo = new SocketAddress(InetAddress.getLocalHost().getHostAddress(), port, peerID);
        } catch (NumberFormatException | UnknownHostException e1) {
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


        startMessageReceiver(port);

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
                System.out.println(e.getMessage());
                return;
            }
        } else {

            Chord.chordInstance = new Chord(addressInfo);
        }

        Chord.chordInstance.initThreads();

        final ScheduledExecutorService periodicExecutor = Executors.newSingleThreadScheduledExecutor();
        Runnable t1 = () -> Store.instance().retryRequest();
        periodicExecutor.scheduleAtFixedRate(t1, 0, Constants.REQUEST_RETRY_INTERVAL_MS, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            periodicExecutor.shutdown();
            ShutdownHandler.execute();
        }));
    }

    private static void startMessageReceiver(int port) {
        messageReceiver = new Thread(() -> {
            MessageHandler messageListener = new MessageHandler(port);
            messageListener.receive();
        });
        messageReceiver.start();
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
