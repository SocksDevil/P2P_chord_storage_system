package com.feup.sdis.peer;

import com.feup.sdis.actions.BSDispatcher;
import com.feup.sdis.actions.Dispatcher;
import com.feup.sdis.actions.Init;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.InitMessage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

public class Peer {

    // TODO: ver isto meu puto
    public static SocketAddress addressInfo;

    public static void main(String[] args) {

        if (args.length != 4) {
            System.out.println("Invalid number of arguments");
            return;
        }

        String hostname = args[0];
        String peerID = args[1];
        String accessPoint = args[2];
        String ip;
        int port;
        if (!hostname.contains(":")) {
            System.out.println("Hostname should be provided in the form ip:number");
        }

        String[] arguments = hostname.split(":");
        ip = arguments[0];

        try {
            port = Integer.parseInt(arguments[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("Port must be a number");
            return;
        }

        Constants.SENDER_ID = peerID;
        Constants.SERVER_IP = ip;
        Constants.MAX_OCCUPIED_DISK_SPACE_MB = Integer.parseInt(args[3]) * Constants.MEGABYTE;
        Constants.peerRootFolder = Constants.peerParentFolder + "peer-" + peerID + "/";
        Constants.backupFolder = Constants.peerRootFolder + "backups/";
        Constants.restoredFolder = Constants.peerRootFolder + "restored/";
        Server.createPeerFolders();

        // TODO: SERVER PORT IS HARDCODED
        Constants.SERVER_PORT = 25565;

        // TODO: com argumentos
        try {
            addressInfo = new SocketAddress(InetAddress.getLocalHost().getHostAddress(), port);
        } catch (NumberFormatException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (UnknownHostException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // TODO: Initial message - this will change from Server to chord - Por num thread
        new Init(new InitMessage(addressInfo)).process();

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
        
        //TODO: port number está hardcoded
        MessageListener messageListener = new MessageListener(port);
        messageListener.receive();

    }
}
