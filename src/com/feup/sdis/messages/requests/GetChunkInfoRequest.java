package com.feup.sdis.messages.requests;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.ChunkInfoResponse;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;

public class GetChunkInfoRequest extends Request {
    private final String fileID;
    private final int chunkNo;

    public GetChunkInfoRequest(String fileID, int chunkNo) {
        this.fileID = fileID;
        this.chunkNo = chunkNo;
    }

    @Override
    public Response handle() {
        final String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNo);
        if (!Store.instance().getStoredFiles().containsKey(chunkID)) {
            System.out.println("Could not find chunk " + chunkID);
            return new ChunkInfoResponse(Status.FILE_NOT_FOUND, fileID, chunkNo);
        }

        final StoredChunkInfo storedChunkInfo = Store.instance().getStoredFiles().get(chunkID);
        return new ChunkInfoResponse(storedChunkInfo);
    }

    @Override
    public SocketAddress getConnection() {
        return null;
    }

    @Override
    public String toString() {
        return "GetChunkInfoRequest{" +
                "fileID='" + fileID + '\'' +
                ", chunkNo=" + chunkNo +
                '}';
    }
}
