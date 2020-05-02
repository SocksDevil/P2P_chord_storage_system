package com.feup.sdis.messages;

import java.io.Serializable;

import com.feup.sdis.chord.SocketAddress;

public abstract class Message implements Serializable {
    protected int status;
    public abstract Message handle();
    public abstract SocketAddress getConnection();


    public int getStatus(){
        return status;
    }
}