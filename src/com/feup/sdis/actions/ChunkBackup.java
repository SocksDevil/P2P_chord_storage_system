package com.feup.sdis.actions;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.BackupRequest;
import com.feup.sdis.messages.requests.BackupLookupRequest;
import com.feup.sdis.messages.responses.BackupResponse;
import com.feup.sdis.messages.responses.LookupResponse;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.MessageListener;
import com.feup.sdis.peer.Peer;

public class ChunkBackup extends Action implements Runnable {

    String fileID;
    int repID;
    int chunkNo;
    byte[] chunkData;
    private final int nChunks;
    private final int replDegree;
    private final String originalFilename;

    public ChunkBackup(String fileID, int chunkNo, int repID, byte[] chunkData, int nChunks, int replDegree, String originalFilename) {

        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.chunkData = chunkData;
        this.repID = repID;
        this.nChunks = nChunks;
        this.replDegree = replDegree;
        this.originalFilename = originalFilename;
    }

    @Override
    public void run() {

        this.process();
    }

    @Override
    String process() {

        String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNo);
        final SocketAddress addressInfo = Chord.chordInstance.lookup(chunkID, repID);
        
        BackupLookupRequest lookupRequest = new BackupLookupRequest(fileID, chunkNo, repID, addressInfo, this.chunkData.length, false);
        LookupResponse lookupRequestAnswer = MessageListener.sendMessage(lookupRequest, lookupRequest.getConnection());

        if(lookupRequestAnswer == null || lookupRequestAnswer.getStatus() != Status.SUCCESS) {
            System.out.println("Failed to lookup peer for " + chunkNo + " of file " + fileID + " with rep " + repID);
            // TODO: ver return
            return null;
        }

        // System.out.println("Backing up " + chunkNo + " of file " + fileID + " with rep " + repID + " to " + lookupRequestAnswer.getAddress());

        BackupRequest backupRequest = new BackupRequest(this.fileID, chunkNo, this.replDegree, this.chunkData, lookupRequestAnswer.getAddress(), nChunks, originalFilename);

        BackupResponse backupRequestAnswer = MessageListener.sendMessage(backupRequest, backupRequest.getConnection());
        if (backupRequestAnswer != null && backupRequestAnswer.getStatus() == Status.SUCCESS) {
            // System.out.println("Successfully stored chunk " + chunkNo + " of file " + fileID + " with rep " + repID + " in " + lookupRequestAnswer.getAddress());
            System.out.println("Successfully stored chunk " + chunkNo + " with rep " + repID + " in " + lookupRequestAnswer.getAddress());

            Store.instance().getReplCount().addNewID(chunkID, Peer.addressInfo, this.repID);
        }
        else{
            System.out.println("Failed to stored chunk " + chunkNo + " with rep " + repID);
        }

        return null;
    }

}