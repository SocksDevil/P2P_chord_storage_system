package com.feup.sdis.actions;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.*;
import com.feup.sdis.model.Store;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.Peer;

public class ChunkBackup extends Action implements Runnable {
    int MAX_TRIES = 3;

    String fileID;
    int repID;
    int chunkNo;
    byte[] chunkData;

    public ChunkBackup(String fileID, int chunkNo,int repID, byte[] chunkData) {

        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.chunkData = chunkData;
        this.repID = repID;
    }

    @Override
    public void run() {

        this.process();
    }

    @Override
    String process() {

        for (int i = 0; i < this.MAX_TRIES; i++) {

            // -- TODO: This will change because of Chord
            LookupMessage lookupRequest = new LookupMessage(this.fileID + "#" + this.chunkNo, this.repID, Peer.addressInfo);
            Message lookupMessageAnswer = this.sendMessage(lookupRequest,
                    new SocketAddress(Constants.SERVER_IP, Constants.SERVER_PORT));
            // --
            BackupMessage backupRequest = new BackupMessage(this.fileID, chunkNo, this.repID, this.chunkData,
                    lookupMessageAnswer.getConnection());

            Message backupMessageAnswer = this.sendMessage(backupRequest, backupRequest.getConnection());

            if (backupMessageAnswer.getStatus() == 200) {

                Store.instance().getReplCount().addNewID(this.fileID + "#" + this.chunkNo, Peer.addressInfo, this.repID);
                break;
            } 

        }
        return null;
    }

}