package com.feup.sdis.actions;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.BackupRequest;
import com.feup.sdis.messages.responses.BackupResponse;
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
    private final int MAX_TRIES = 3;

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

            final SocketAddress addressInfo = Chord.chordInstance.lookup(StoredChunkInfo.getChunkID(fileID, chunkNo),repID);
            BackupRequest backupRequest = new BackupRequest(this.fileID, chunkNo, this.replDegree, this.chunkData, addressInfo, nChunks, originalFilename);

            BackupResponse backupRequestAnswer = MessageListener.sendMessage(backupRequest, backupRequest.getConnection());
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