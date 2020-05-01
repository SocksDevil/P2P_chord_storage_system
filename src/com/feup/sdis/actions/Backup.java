package com.feup.sdis.actions;

import com.feup.sdis.messages.BackupMessage;
import com.feup.sdis.messages.Message;
import com.feup.sdis.peer.Peer;

public class Backup extends Action {
    private final Message message;

    public Backup(String[] args) {
        this.message = new BackupMessage(args, Peer.connection);
    }

    @Override
    public String process() {
        //TODO: Implement backup
        // this.sendMessage(message);
        return "Backed up file";
    }
}
