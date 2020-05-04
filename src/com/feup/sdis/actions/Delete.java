package com.feup.sdis.actions;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.requests.DeleteRequest;
import com.feup.sdis.messages.requests.GetResourceRequest;
import com.feup.sdis.messages.responses.ChunkResponse;
import com.feup.sdis.messages.responses.DeleteResponse;
import com.feup.sdis.messages.responses.GetResourceResponse;
import com.feup.sdis.model.RestoredFileInfo;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.Constants;

public class Delete extends Action {
    private final String fileID;

    public Delete(String[] args) {
        fileID = args[1];
    }

    @Override
    public String process() {
        final ChunkResponse response = Restore.getChunk(fileID, 0, Constants.MAX_REPL_DEGREE);
        if (response == null) {
            final String error = "File " + fileID + " not found";
            System.out.println(error);
            return error;
        }
        final RestoredFileInfo file = new RestoredFileInfo(fileID, response.getReplDegree(), response.getnChunks());

        for(int chunkNo = 0; chunkNo < file.getNChunks(); chunkNo++) {
            for (int replDegree = 0; replDegree < file.getDesiredReplicationDegree(); replDegree++) {
                int chunkNumber = chunkNo;
                int replicationDegree = replDegree;
                BSDispatcher.servicePool.execute(() -> {

                    GetResourceRequest lookupRequest = new GetResourceRequest(StoredChunkInfo.getChunkID(fileID, chunkNumber)
                            , replicationDegree);

                    GetResourceResponse lookupResponse = sendMessage(lookupRequest,
                            new SocketAddress(Constants.SERVER_IP, Constants.SERVER_PORT));

                    if (lookupResponse == null || lookupResponse.getAddress() == null) {
                        System.out.println("Null lookup response");
                        return;
                    }
                    DeleteRequest deleteRequest = new DeleteRequest(fileID, chunkNumber);
                    DeleteResponse deleteResponse = sendMessage(deleteRequest, lookupResponse.getAddress());

                    if (deleteResponse == null) {
                        System.out.println("Could not read DELETE response for chunk " + chunkNumber);
                        return;
                    }

                    switch (deleteResponse.getStatus()) {
                        case SUCCESS:
                            System.out.println("Deleted chunk " + chunkNumber + " from " + lookupResponse.getAddress().toString());
                            break;
                        case FILE_NOT_FOUND:
                            System.out.println("Chunk " + chunkNumber + " was not present in " + lookupResponse.getAddress().toString());
                            break;
                        default:
                            System.out.println("Could not retrieve chunk " + chunkNumber + ", got error " + deleteResponse.getStatus());
                    }
                });
            }
        }

        return "Successfully requested file deletion";
    }
}
