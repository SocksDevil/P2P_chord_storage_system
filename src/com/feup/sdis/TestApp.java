package com.feup.sdis;

import com.feup.sdis.actions.Dispatcher;
import com.feup.sdis.exceptions.MessageError;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestApp {

    public static void main(String[] args) throws IOException, NotBoundException {
        if(args.length < 3) {
            System.out.println("Usage: java AppName hostname[:port] <peerAp> <operation> <opnd1> [<opnd2>]");
            return;
        }
        String ip;
        int port;

        if(args[0].contains(":")){
            String[] arguments = args[0].split(":");
            ip =  arguments[0];
            port = Integer.parseInt(arguments[1]);
        } else {
            port = 1099;
            ip = args[0];
        }

        String msg = null;
        if (args.length == 3){
            msg = args[2];
        }
        else if(args.length == 4){
            msg = args[2] + "," + args[3];
        } else if(args.length == 5){
            msg = args[2] + "," + args[3] + "," + args[4];
        }

        final String peerAp = args[1];

        System.out.println(new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(new Date()));
        Registry registry = LocateRegistry.getRegistry(ip, port);
        Dispatcher stub = (Dispatcher) registry.lookup(peerAp);
        String answer = null;
        try {
            System.out.println("@ Sending message to peer " + peerAp + ": " + msg);
            answer = stub.processMsg(msg);
        } catch (MessageError messageError) {
            messageError.printStackTrace();
        }
        System.out.println("@ Received answer:");
        System.out.println(answer + "\n");

    }
}