package com.feup.sdis.actions;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.BackupRequest;
import com.feup.sdis.messages.requests.BackupLookupRequest;
import com.feup.sdis.messages.responses.BackupResponse;
import com.feup.sdis.messages.responses.BackupLookupResponse;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.MessageHandler;
import com.feup.sdis.peer.Peer;
import java.util.concurrent.Callable;

public class ChunkBackup extends Action implements Callable<String> {

    String fileID;
    int repID;
    int chunkNo;
    byte[] chunkData;
    private final int nChunks;
    private final int replDegree;
    private final String originalFilename;
    private SocketAddress destinationOverride;

    public ChunkBackup(String fileID, int chunkNo, int repID, byte[] chunkData, int nChunks, int replDegree, String originalFilename) {

        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.chunkData = chunkData;
        this.repID = repID;
        this.nChunks = nChunks;
        this.replDegree = replDegree;
        this.originalFilename = originalFilename;
        this.destinationOverride = null; /* Destination override is meant to overule chords TODO: don't forget to remove this if I end up not using it*/
    }

    public ChunkBackup(String fileID, int chunkNo, int repID, byte[] chunkData, int nChunks, int replDegree, String originalFilename, SocketAddress destinationOverride) {

        this(fileID, chunkNo, repID, chunkData, nChunks, replDegree, originalFilename);
        this.destinationOverride = destinationOverride;
    }

    @Override
    String process() {

        final String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNo);
        final SocketAddress addressInfo = this.destinationOverride == null ? Chord.chordInstance.lookup(chunkID, repID) : this.destinationOverride ;
        final BackupLookupRequest lookupRequest = new BackupLookupRequest(fileID, chunkNo, repID, addressInfo, this.chunkData.length, false);
        final BackupLookupResponse lookupRequestAnswer = MessageHandler.sendMessage(lookupRequest, lookupRequest.getConnection());

        if(lookupRequestAnswer == null || lookupRequestAnswer.getStatus() != Status.SUCCESS) {
            return "Failed to lookup peer for " + chunkNo + " of file " + fileID + " with rep " + repID + (lookupRequestAnswer == null ? "" :
                    " with status " + lookupRequestAnswer.getStatus());
        }

        final BackupRequest backupRequest = new BackupRequest(this.fileID, chunkNo, this.replDegree, this.chunkData,
                lookupRequestAnswer.getAddress(), nChunks, originalFilename, Peer.addressInfo);

        final BackupResponse backupRequestAnswer = MessageHandler.sendMessage(backupRequest, backupRequest.getConnection());
        if (backupRequestAnswer != null && backupRequestAnswer.getStatus() == Status.SUCCESS) {
            System.out.println("Successfully stored chunk " + chunkNo + " with rep " + repID + " in " + lookupRequestAnswer.getAddress());
        }
        else{
            return "Failed to stored chunk " + chunkNo + " with rep " + repID;
        }

        return null;
    }

    @Override
    public String call() {
        return this.process();
    }
}