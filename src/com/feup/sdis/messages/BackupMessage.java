package com.feup.sdis.messages;

import com.feup.sdis.chord.Connection;

public class BackupMessage extends Message {

    private String file;
    private int repDegree;
    private Connection connection;

    public BackupMessage(String file, Connection connection){

        this.file =file;
        this.connection = connection;
        // this.repDegree = Integer.parseInt(args[2]);

    }

    @Override
    public Message handle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Connection getConnection() {
        // TODO Auto-generated method stub
        return this.connection;
    }

    
}