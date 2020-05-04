package com.feup.sdis.actions;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.BackupRequest;
import com.feup.sdis.messages.requests.LookupRequest;
import com.feup.sdis.messages.requests.Request;
import com.feup.sdis.messages.responses.BackupResponse;
import com.feup.sdis.messages.responses.LookupResponse;
import com.feup.sdis.model.Store;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.Peer;
import com.feup.sdis.peer.MessageListener;

public class ChunkBackup extends Action implements Runnable {
    int MAX_TRIES = 3;

    String fileID;
    int repID;
    int chunkNo;
    byte[] chunkData;

    public ChunkBackup(String fileID, int chunkNo, int repID, byte[] chunkData) {

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
            LookupRequest lookupRequest = new LookupRequest(this.fileID + "#" + this.chunkNo, this.repID, Peer.addressInfo);
            LookupResponse lookupRequestAnswer = MessageListener.sendMessage(lookupRequest,
                    new SocketAddress(Constants.SERVER_IP, Constants.SERVER_PORT,Constants.peerID));
            // --
            if (lookupRequest == null) {
                continue;
            }

            BackupRequest backupRequest = new BackupRequest(this.fileID, chunkNo, this.repID, this.chunkData,
                    lookupRequestAnswer.getAddress());

            BackupResponse backupRequestAnswer = MessageListener.sendMessage(backupRequest, backupRequest.getConnection());
            if (backupRequestAnswer != null && backupRequestAnswer.getStatus() == Status.SUCCESS) {
                Store.instance().getReplCount().addNewID(this.fileID + "#" + this.chunkNo, Peer.addressInfo, this.repID);
                break;
            }
        }
        return null;
    }

}