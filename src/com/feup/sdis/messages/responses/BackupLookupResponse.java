package com.feup.sdis.messages.responses;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;

public class BackupLookupResponse extends Response {

    private final SocketAddress address;
    
    public BackupLookupResponse(Status status, SocketAddress address) {
        super(status);
        this.address = address;
    }

    public SocketAddress getAddress() {
        return address;
    }

    @Override
    public String toString(){
        return "BACKUP LOOKUP: " + this.address + " STATUS: " + this.getStatus();
    }
    
}