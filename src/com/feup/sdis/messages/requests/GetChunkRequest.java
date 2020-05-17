package com.feup.sdis.messages.requests;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.ChunkResponse;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;

import java.io.IOException;

public class GetChunkRequest extends Request {
    protected final String fileID;
    protected final int chunkNo;

    public GetChunkRequest(String fileID, int chunkNo) {
        this.fileID = fileID;
        this.chunkNo = chunkNo;
    }

    @Override
    public Response handle() {
        final String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNo);
        if (!Store.instance().getStoredFiles().containsKey(chunkID)) {
            System.out.println("Could not find chunk " + chunkID);
            return new ChunkResponse(Status.FILE_NOT_FOUND, fileID, chunkNo);
        }

        final StoredChunkInfo storedChunkInfo = Store.instance().getStoredFiles().get(chunkID);

        try {
            return new ChunkResponse(storedChunkInfo.getBody(), fileID, chunkNo,
                    storedChunkInfo.getDesiredReplicationDegree(), storedChunkInfo.getnChunks(),
                    storedChunkInfo.getOriginalFilename(), storedChunkInfo.getInitiatorPeer());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ChunkResponse(Status.ERROR, fileID, chunkNo);
    }

    @Override
    public SocketAddress getConnection() {
        return null;
    }

    @Override
    public String toString() {
        return "GetChunkRequest{" +
                "fileID='" + fileID + '\'' +
                ", chunkNo=" + chunkNo +
                '}';
    }
}
