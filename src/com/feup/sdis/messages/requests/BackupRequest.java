package com.feup.sdis.messages.requests;

import java.io.IOException;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.BackupResponse;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.Constants;

public class BackupRequest extends Request {

    private final String fileID;
    private final int chunkNo;
    private final int desiredRepDegree;
    private final SocketAddress connection;
    private final int nChunks;
    private final String originalFilename;
    private final SocketAddress initiatorPeer;
    private final byte[] chunkData;

    public BackupRequest(String fileID, int chunkNo, int desiredRepDegree,
                         byte[] data, SocketAddress connection, int nChunks,
                         String originalFilename, SocketAddress initiatorPeer) {

        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.connection = connection;
        this.desiredRepDegree = desiredRepDegree;
        this.chunkData = data;
        // this.repDegree = Integer.parseInt(args[2]);
        this.nChunks = nChunks;
        this.originalFilename = originalFilename;
        this.initiatorPeer = initiatorPeer;
    }

    @Override
    public Response handle() {
        // Space is already "reserved"
        final StoredChunkInfo newChunk = new StoredChunkInfo(fileID, desiredRepDegree, chunkNo,
                chunkData.length, nChunks, originalFilename, initiatorPeer);
        // If placeholder is not there, file deleted -> don't save
        if(!Store.instance().getStoredFiles().containsKey(newChunk.getChunkID()))
            return new BackupResponse(Status.FILE_NOT_FOUND);
        Store.instance().getStoredFiles().put(newChunk.getChunkID(), newChunk);
        System.out.println("Stored " + Store.instance().getUsedDiskSpace() + " - " + this.chunkData.length + " - " + Constants.MAX_OCCUPIED_DISK_SPACE_MB);

        try {
            newChunk.storeFile(chunkData);
        } catch (IOException e) {
            System.out.println("Error storing chunk");
           return new BackupResponse(Status.ERROR);
        }

        return new BackupResponse(Status.SUCCESS);
    }

    @Override
    public SocketAddress getConnection() {
        return this.connection;
    }


    @Override
    public String toString() {
        return "BackupRequest{" +
                "fileID='" + fileID + '\'' +
                ", chunkNo=" + chunkNo +
                ", desiredRepDegree=" + desiredRepDegree +
                ", connection=" + connection +
                ", nChunks=" + nChunks +
                '}';
    }
}