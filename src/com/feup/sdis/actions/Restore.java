package com.feup.sdis.actions;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.GetChunkRequest;
import com.feup.sdis.messages.requests.GetResourceRequest;
import com.feup.sdis.messages.requests.LookupRequest;
import com.feup.sdis.messages.responses.ChunkResponse;
import com.feup.sdis.messages.responses.GetResourceResponse;
import com.feup.sdis.messages.responses.LookupResponse;
import com.feup.sdis.model.RestoredFileInfo;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.Peer;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Restore extends Action {
    private final String fileID;

    public Restore(String[] args) {
        fileID = args[1];
    }

    @Override
    public String process() {

        final ChunkResponse response = getChunk(fileID, 0, Constants.MAX_REPL_DEGREE);
        if (response == null) {
            final String error = "File " + fileID + " not found";
            System.out.println(error);
            return error;
        }
        final RestoredFileInfo file = new RestoredFileInfo(fileID, response.getReplDegree(), response.getnChunks());
        file.getRestoredChunks().put(0, response.getData());
        System.out.println("Found file  " + response.getOriginalFilename() +
                " with replication degree " + response.getReplDegree() + " and " + response.getnChunks() + " chunks");

        for (int i = 1; i < response.getnChunks(); i++) {
            int chunkNo = i;
            BSDispatcher.servicePool.execute(() -> {
                final ChunkResponse chunk = getChunk(fileID, chunkNo, response.getReplDegree());
                if (chunk == null) {
                    System.out.println("Couldn't retrieve chunk " + chunkNo + " of file " + fileID);
                    return;
                }

                file.getRestoredChunks().put(chunkNo, chunk.getData());
                if (file.isFullyRestored()) {
                    try {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        for (byte[] body : file.getRestoredChunks().values())
                            outputStream.write(body);
                        FileOutputStream fos = new FileOutputStream
                                (Constants.restoredFolder + response.getOriginalFilename());
                        fos.write(outputStream.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Failed to store file " + fileID);
                    }
                }
            });
        }
        return "Restored file";
    }

    public static ChunkResponse getChunk(String fileId, int chunkNo, int replDegree) {
        for (int replicator = 0; replicator < replDegree; replicator++) {
            GetResourceRequest lookupRequest = new GetResourceRequest(StoredChunkInfo.getChunkID(fileId, chunkNo)
                    , replicator);

            GetResourceResponse lookupResponse = sendMessage(lookupRequest,
                    new SocketAddress(Constants.SERVER_IP, Constants.SERVER_PORT));

            if (lookupResponse == null || lookupResponse.getAddress() == null) {
                System.out.println("Null lookup response");
                continue;
            }

            GetChunkRequest getChunkRequest = new GetChunkRequest(fileId, chunkNo);

            ChunkResponse chunkResponse = sendMessage(getChunkRequest, lookupResponse.getAddress());

            if (chunkResponse == null) {
                System.out.println("Could not read response for chunk " + chunkNo);
                continue;
            } else if (chunkResponse.getStatus() != Status.SUCCESS) {
                System.out.println("Could not retrieve chunk " + chunkNo + ", got error " + chunkResponse.getStatus());
                continue;
            }

            System.out.println("Retrieved chunk " + chunkNo + " successfully");
            return chunkResponse;
        }
        return null;
    }
}
