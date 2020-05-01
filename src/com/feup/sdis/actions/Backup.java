package com.feup.sdis.actions;

import com.feup.sdis.chord.Connection;
import com.feup.sdis.messages.BackupMessage;
import com.feup.sdis.messages.LookupMessage;
import com.feup.sdis.messages.Message;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.Peer;

public class Backup extends Action {
    private final Message message;
    private String file;
    private int repDegree;

    public Backup(String[] args) {
        this.message = new LookupMessage(args, Peer.connection);
    }

    @Override
    public String process() {
        //TODO: Implement backup
        Message lookupMessageAnswer = this.sendMessage(message, new Connection(Constants.SERVER_IP, Constants.SERVER_PORT));
        BackupMessage backupRequest = new BackupMessage(file, lookupMessageAnswer.getConnection());
        Message backupMessageAnswer = this.sendMessage(backupRequest, backupRequest.getConnection());

        return "Backed up file";
    }
}
