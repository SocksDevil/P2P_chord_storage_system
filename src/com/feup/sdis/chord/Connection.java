package com.feup.sdis.chord;

import java.io.Serializable;

public class Connection implements Serializable {
    private String ip;
    private int port;

    public Connection(String ip, int port) {
        this.ip = ip;
        this.port = port;
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
        Connection c = (Connection) o;    
        return this.ip.equals(c.getIp()) && this.port == c.getPort();
    }
}