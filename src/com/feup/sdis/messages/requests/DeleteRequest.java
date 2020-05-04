package com.feup.sdis.messages.requests;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.ChunkResponse;
import com.feup.sdis.messages.responses.DeleteResponse;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.Constants;

import java.io.File;
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
        final Store store = Store.instance();
        final String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNo);
        if (!store.getStoredFiles().containsKey(chunkID)) {
            System.out.println("Could not find chunk on Store" + chunkID);
            return new DeleteResponse(Status.FILE_NOT_FOUND, fileID, chunkNo);
        }

        final StoredChunkInfo storedChunkInfo = store.getStoredFiles().get(chunkID);
        store.incrementSpace(-1 * storedChunkInfo.getChunkSize());
        store.getStoredFiles().remove(chunkID);
        store.getBackedUpFiles().remove(chunkID);
        store.getBackedUpFiles().remove(fileID);

        // TODO may need to delete repl count in the future if it is used

        Status returnStatus = Status.SUCCESS;
        final File fileToDelete = new File(Constants.backupFolder + chunkID);
        if(!fileToDelete.exists()) {
            System.out.println("Could not find chunk " + chunkID + " on disk");
            returnStatus = Status.FILE_NOT_FOUND;
        }
        else if(!fileToDelete.delete()){
            System.out.println("Failed to delete chunk " + chunkID);
            returnStatus = Status.ERROR;
        }

        return new DeleteResponse(returnStatus, fileID, chunkNo);
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
