package com.feup.sdis.actions;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.BackupRequest;
import com.feup.sdis.messages.requests.LookupRequest;
import com.feup.sdis.messages.responses.BackupResponse;
import com.feup.sdis.messages.responses.LookupResponse;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.Constants;
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

        for (int i = 0; i < this.MAX_TRIES; i++) {

            // -- TODO: This will change because of Chord
            LookupRequest lookupRequest = new LookupRequest(StoredChunkInfo.getChunkID(this.fileID, this.chunkNo),
                    this.repID, Peer.addressInfo);
            LookupResponse lookupRequestAnswer = this.sendMessage(lookupRequest,
                    new SocketAddress(Constants.SERVER_IP, Constants.SERVER_PORT));
            // --
            if (lookupRequestAnswer == null) {
                System.out.println("Did not receive lookup answer");
                continue;
            }

            BackupRequest backupRequest = new BackupRequest(this.fileID, chunkNo, this.replDegree, this.chunkData,
                    lookupRequestAnswer.getAddress(), nChunks, originalFilename);

            BackupResponse backupRequestAnswer = this.sendMessage(backupRequest, backupRequest.getConnection());
            if (backupRequestAnswer != null && backupRequestAnswer.getStatus() == Status.SUCCESS) {
                System.out.println("Successfully stored chunk " + chunkNo + " of file " + fileID);
                Store.instance().getReplCount().addNewID(StoredChunkInfo.getChunkID(fileID, chunkNo),
                        Peer.addressInfo, this.repID);
                break;
            }

            System.out.println("Failed to store chunk " + chunkNo + " of file " + fileID + ", trying again");
        }
        return null;
    }

}