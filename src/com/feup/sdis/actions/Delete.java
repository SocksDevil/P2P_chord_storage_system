package com.feup.sdis.actions;

import java.util.concurrent.Future;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.DeleteFileInfo;
import com.feup.sdis.messages.requests.DeleteRequest;
import com.feup.sdis.messages.responses.ChunkInfoResponse;
import com.feup.sdis.messages.responses.DeleteFileInfoResponse;
import com.feup.sdis.messages.responses.DeleteResponse;
import com.feup.sdis.model.RequestRetryInfo;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.MessageListener;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

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

        // remove BackupFileInfo from the peer that initiated the backup
        Callable<Boolean> deleteFileInfoReq = () -> {
            final SocketAddress backupInitiatorPeer = response.getInitiatorPeer();
            final DeleteFileInfo req = new DeleteFileInfo(fileID);
            final DeleteFileInfoResponse res = MessageListener.sendMessage(req, backupInitiatorPeer);

            if (res == null) {
                System.out.println("Error removing file " + fileID + " from initiator peer " + backupInitiatorPeer);
                return false;
            }

            return true;
        };
        sendRequest(deleteFileInfoReq);

        for (int chunkNo = 0; chunkNo < nChunks; chunkNo++) {
            for (int replDegree = 0; replDegree < desiredRepl; replDegree++) {
                deleteChunk(chunkNo, replDegree, fileID);
            }
        }

        return "Successfully requested file deletion";
    }

    public static Future<Boolean> deleteChunk(int chunkNumber, int replNo, String fileID) {

        Callable<Boolean> r = () -> {
            final String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNumber);

            final SocketAddress addressInfo = Chord.chordInstance.lookup(chunkID, replNo);
            final DeleteRequest deleteRequest = new DeleteRequest(fileID, chunkNumber, replNo);
            System.out.println(
                    "Requesting DELETE (" + fileID + "," + chunkNumber + "," + replNo + ") to peer " + addressInfo);
            final DeleteResponse deleteResponse = MessageListener.sendMessage(deleteRequest, addressInfo);

            if (deleteResponse == null) {
                System.out.println("Could not read DELETE response for chunk " + chunkNumber + ", added to retry queue");
                return false;
            }

            if (deleteResponse.getStatus() == Status.SUCCESS) {
                System.out.println("Deleted chunk " + chunkNumber + " from " + addressInfo.toString() + ", replNo=" + replNo);
                return true;
            }

            switch (deleteResponse.getStatus()) {
                case FILE_NOT_FOUND:
                    System.out.println("Chunk " + chunkNumber + " was not present in " + addressInfo.toString());
                    break;
                case CONNECTION_ERROR:
                    System.out.println("Connection error for chunk " + chunkID + ", replNo=" + replNo);
                    break;
                default:
                    System.out.println("Could not delete chunk " + chunkNumber + " from " + addressInfo + ", got error "
                            + deleteResponse.getStatus());
            }

            System.out.println("Adding request " + deleteRequest.toString() + " to retry queue");
            return false;
        };

        return sendRequest(r);
    }

    private static Future<Boolean> sendRequest(Callable<Boolean> r) {
        Future<Boolean> receivedAnswer = BSDispatcher.servicePool.submit(r);
        BSDispatcher.servicePool.execute(() -> {
            try {
                Boolean answer = receivedAnswer.get();
                if (!answer) {
                    System.out.println("> DELETE: adding request to retry queue");
                    Store.instance().addRequestToRetryQueue(new RequestRetryInfo(r));
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return receivedAnswer;
    }
}
