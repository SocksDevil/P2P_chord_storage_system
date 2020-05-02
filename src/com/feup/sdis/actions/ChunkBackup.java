package com.feup.sdis.actions;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.*;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.Peer;

public class ChunkBackup extends Action implements Runnable {
    int MAX_TRIES = 3;

    String fileID;
    int chunkNo;
    int desiredRepDeg;
    byte[] chunkData;

    public ChunkBackup(String fileID, int chunkNo, int desiredRepDeg, byte[] chunkData) {

        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.chunkData = chunkData;
        this.desiredRepDeg = desiredRepDeg;
    }

    @Override
    public void run() {

        this.process();
    }

    @Override
    String process() {

        int curr = 0;
        for (int i = 0; i < this.desiredRepDeg; i++) {

            // -- TODO: This will change because of Chord
            LookupMessage lookupRequest = new LookupMessage(this.fileID + "#" + this.chunkNo , i,  Peer.addressInfo);
            Message lookupMessageAnswer = this.sendMessage(lookupRequest, new SocketAddress(Constants.SERVER_IP, Constants.SERVER_PORT));
            // --
            BackupMessage backupRequest = new BackupMessage(this.fileID, chunkNo, i, this.chunkData, lookupMessageAnswer.getConnection());

            Message backupMessageAnswer = this.sendMessage(backupRequest, backupRequest.getConnection());

            if(backupMessageAnswer.getStatus() == 400){
                curr++;
                i--;
            }

            if(curr > MAX_TRIES)
                return null;

        }
        return null;
    }

}