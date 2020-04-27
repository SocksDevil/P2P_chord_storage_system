package com.feup.sdis.peer;


import com.feup.sdis.actions.BSDispatcher;
import com.feup.sdis.actions.Dispatcher;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

public class Peer {

    public static void main(String[] args) {

        if (args.length != 3) {
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
        Constants.SERVER_PORT = port;

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
    }
}
