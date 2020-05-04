package com.feup.sdis.messages.requests;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.ChunkResponse;
import com.feup.sdis.messages.responses.DeleteResponse;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;

import java.io.IOException;

public class DeleteRequest extends Request {
    private final String fileID;
    private final int chunkNo;

    public DeleteRequest(String fileID, int chunkNo) {
        this.fileID = fileID;
        this.chunkNo = chunkNo;
    }


    @Override
    public Response handle() {
        final String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNo);
        if (!Store.instance().getStoredFiles().containsKey(chunkID)) {
            System.out.println("Could not find chunk " + chunkID);
            return new DeleteResponse(Status.FILE_NOT_FOUND, fileID, chunkNo);
        }

        final StoredChunkInfo storedChunkInfo = Store.instance().getStoredFiles().get(chunkID);
        // delete file

        return new DeleteResponse(Status.SUCCESS, fileID, chunkNo);
    }

    @Override
    public SocketAddress getConnection() {
        return null;
    }

    @Override
    public String toString() {
        return "DeleteRequest{" +
                "fileID='" + fileID + '\'' +
                ", chunkNo=" + chunkNo +
                '}';
    }
}
