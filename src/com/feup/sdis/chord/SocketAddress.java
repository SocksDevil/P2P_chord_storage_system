package com.feup.sdis.chord;

import java.io.Serializable;
import java.util.UUID;

public class SocketAddress implements Serializable {

    private UUID peerID;
    private String ip;
    private int port;

    public SocketAddress(String ip, int port, String peerID) {
        this.ip = ip;
        this.port = port;
        this.peerID = UUID.nameUUIDFromBytes(peerID.getBytes());
    }

    public UUID getPeerID() {
        return peerID;
    }

    public String getIp() {
        return ip;
    }
    
    public int getPort() {
        return port;
    }
    
    @Override
    public String toString(){
        return ip + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false; 
        if (getClass() != o.getClass()) return false;
        SocketAddress c = (SocketAddress) o;    
        return this.ip.equals(c.getIp()) && this.port == c.getPort() && this.peerID.equals(c.getPeerID());
    }
}