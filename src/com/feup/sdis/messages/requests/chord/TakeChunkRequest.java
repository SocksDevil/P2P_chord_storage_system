package com.feup.sdis.messages.requests.chord;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.DeleteRequest;
import com.feup.sdis.messages.responses.DeleteResponse;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TakeChunkRequest extends DeleteRequest {

    public TakeChunkRequest(String fileID, int chunkNo, int replNo) {
        super(fileID, chunkNo, replNo);
    }

    public static TakeChunkRequest createRequestFromChunkID(String chunkID, int replNo) {
        String[] id = chunkID.split("#");
        final String fileID = id[0];
        final int chunkNo = Integer.parseInt(id[1]);
        return new TakeChunkRequest(fileID, chunkNo, replNo);
    }

    @Override
    public Response handle() {
        final DeleteResponse response = super.deleteChunk();
        final StoredChunkInfo chunkInfo = Store.instance().getStoredFiles().get(fileID);
        final int nChunks = chunkInfo.getnChunks();
        final String originalFileName = chunkInfo.getOriginalFilename();
        final SocketAddress initiatorPeer = chunkInfo.getInitiatorPeer();

        if (response == null) {
            Store.instance().getStoredFiles().remove(StoredChunkInfo.getChunkID(fileID, chunkNo));
            Store.instance().getReplCount().removeRepDegree(fileID, replNo);

            Status returnStatus = Status.SUCCESS;
            final String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNo);

            final File fileToDelete = new File(Constants.backupFolder + chunkID);
            if (!fileToDelete.exists()) {
                System.out.println("> DELETE: Could not find chunk " + chunkID + " on disk");
                returnStatus = Status.FILE_NOT_FOUND;
            }

            byte[] data = null;
            try {
                data = StoredChunkInfo.getBody(new FileInputStream(fileToDelete));
            } catch (IOException e) {
                System.out.println("> DELETE: failed to read body data");
                returnStatus = Status.ERROR;
            }

            // TODO: ver se este for o erro se não convinha por as cenas nas dbs de novo
            if (!fileToDelete.delete()) {
                System.out.println("> DELETE: Failed to delete chunk " + chunkID);
                returnStatus = Status.ERROR;
            }

            return new TakeChunkResponse(returnStatus, fileID, chunkNo, replNo, data, nChunks, originalFileName, initiatorPeer);
        }

        return new TakeChunkResponse(response.getStatus(), response.getFileID(), response.getChunkNo(),
                response.getReplNo(), null, nChunks, originalFileName, initiatorPeer);
    }
}
