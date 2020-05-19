package com.feup.sdis.actions;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.DeleteFileInfo;
import com.feup.sdis.messages.requests.DeleteRequest;
import com.feup.sdis.messages.responses.ChunkInfoResponse;
import com.feup.sdis.messages.responses.DeleteFileInfoResponse;
import com.feup.sdis.messages.responses.DeleteResponse;
import com.feup.sdis.model.RequestInfo;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.MessageListener;

public class Delete extends Action {
    private final String fileID;

    public Delete(String[] args) {
        fileID = args[1];
    }

    @Override
    public String process() {
        final ChunkInfoResponse response = Restore.getChunkInfo(fileID, 0, Constants.MAX_REPL_DEGREE);
        if (response == null) {
            final String error = "File " + fileID + " not found";
            System.out.println(error);
            return error;
        }

        final int desiredRepl = response.getReplDegree();
        final int nChunks = response.getnChunks();
        final SocketAddress backupInitiatorPeer = response.getInitiatorPeer();

        // remove BackupFileInfo from the peer that initiated the backup
        final DeleteFileInfo deleteFileInfoReq = new DeleteFileInfo(fileID);
        final DeleteFileInfoResponse deleteFileInfoRes = MessageListener.sendMessage(deleteFileInfoReq, backupInitiatorPeer);

        if (deleteFileInfoRes == null) {
            Store.instance().addRequestToRetryQueue(new RequestInfo(deleteFileInfoReq, backupInitiatorPeer));
            System.out.println("Error removing file " + fileID + " from initiator peer " + backupInitiatorPeer + ", added to retry queue");
        }

        for (int chunkNo = 0; chunkNo < nChunks; chunkNo++) {
            for (int replDegree = 0; replDegree < desiredRepl; replDegree++) {
                deleteChunk(chunkNo, replDegree, fileID);
            }
        }

        return "Successfully requested file deletion";
    }

    public static void deleteChunk(int chunkNumber, int replNo, String fileID) {
        BSDispatcher.servicePool.execute(() -> {
            final String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNumber);

            final SocketAddress addressInfo = Chord.chordInstance.lookup(chunkID, replNo);
            final DeleteRequest deleteRequest = new DeleteRequest(fileID, chunkNumber, replNo);
            System.out.println("Requesting DELETE (" + fileID + "," + chunkNumber + "," + replNo + ") to peer " + addressInfo);
            final DeleteResponse deleteResponse = MessageListener.sendMessage(deleteRequest, addressInfo);

            if (deleteResponse == null) {
                Store.instance().addRequestToRetryQueue(new RequestInfo(deleteRequest, addressInfo));
                System.out.println("Could not read DELETE response for chunk " + chunkNumber + ", added to retry queue");
                return;
            }

            switch (deleteResponse.getStatus()) {
                case SUCCESS:
                    System.out.println("Deleted chunk " + chunkNumber + " from " + addressInfo.toString() + ", replNo=" + replNo);
                    break;
                case FILE_NOT_FOUND:
                    System.out.println("Chunk " + chunkNumber + " was not present in " + addressInfo.toString());
                    break;
                default:
                    System.out.println("Could not delete chunk " + chunkNumber + " from " + addressInfo +
                            ", got error " + deleteResponse.getStatus());
            }
        });
    }
}
