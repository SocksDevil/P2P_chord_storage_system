package com.feup.sdis.actions;

import com.feup.sdis.exceptions.MessageError;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BSDispatcher implements Dispatcher {

    public static final ExecutorService servicePool = Executors.newCachedThreadPool();

    public String processMsg(String msg) throws MessageError {
        final String[] args = msg.split(",");
        Action action;
        System.out.println("\n" + new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(new Date())
                + " Received client request: "
                + msg);
        switch (args[0]){
            case "BACKUP":
                action = new Backup(args);
                break;
            case "DELETE":
                action = new Delete(args);
                break;
            case "RESTORE":
                action = new Restore(args);
                break;
            case "RECLAIM":
                action = new Reclaim(args);
                break;
            case "STATE":
                action = new State();
                break;
            case "SHUTDOWN":
                System.exit(0);
            default:
                throw new MessageError("Received unknown RMI message");
        }
        return action.process();
    }
}
