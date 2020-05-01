package com.feup.sdis.messages;

import com.feup.sdis.chord.Connection;

public class BackupMessage extends Message {

    private String file;
    private int repDegree;
    private Connection connection;

    public BackupMessage(String[] args, Connection connection){
        // TODO: if args != X throw IllegalArgumentException/MessageError...
        this.file = args[1];
        this.repDegree = Integer.parseInt(args[2]);
        this.connection = connection;
    }

    @Override
    public void handle() {
        Server.chord.get
    }

    
}