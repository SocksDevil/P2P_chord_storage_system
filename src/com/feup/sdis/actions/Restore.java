package com.feup.sdis.actions;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.ChunkLookupRequest;
import com.feup.sdis.messages.requests.GetChunkInfoRequest;
import com.feup.sdis.messages.requests.GetChunkRequest;
import com.feup.sdis.messages.responses.ChunkInfoResponse;
import com.feup.sdis.messages.responses.ChunkLookupResponse;
import com.feup.sdis.messages.responses.ChunkResponse;
import com.feup.sdis.messages.responses.BackupLookupResponse;
import com.feup.sdis.model.RestoredFileInfo;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.MessageListener;
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

        final ChunkInfoResponse response = getChunkInfo(fileID, 0, Constants.MAX_REPL_DEGREE);
        if (response == null) {
            final String error = "File " + fileID + " not found";
            System.out.println(error);
            return error;
        }
        final RestoredFileInfo file = new RestoredFileInfo(fileID, response.getReplDegree(), response.getnChunks());
        System.out.println("Found file  " + response.getOriginalFilename() +
                " with replication degree " + response.getReplDegree() + " and " + response.getnChunks() + " chunks");

        for (int i = 0; i < response.getnChunks(); i++) {
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

    public static ChunkResponse getChunk(String fileID, int chunkNo, int replDegree) {
        for (int replicator = 0; replicator < replDegree; replicator++) {
            String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNo);

            // find peer that has chunk
            final SocketAddress addressInfo = Chord.chordInstance.lookup(chunkID, replicator); // get assigned peer
            ChunkLookupRequest lookupRequest = new ChunkLookupRequest(fileID, chunkNo, replicator, Peer.addressInfo); // resolve redirects
            ChunkLookupResponse lookupResponse = MessageListener.sendMessage(lookupRequest, addressInfo);

            if (lookupResponse == null) {
                System.out.println("Could not read response for ChunkLookupRequest on chunk " + chunkID);
                continue;
            }
            else if (lookupResponse.getStatus() != Status.SUCCESS) {
                System.out.println("Could not find peer with chunk " + chunkID + ", got error " + lookupResponse.getStatus());
                continue;
            }

            GetChunkRequest getChunkRequest = new GetChunkRequest(fileID, chunkNo);
            ChunkResponse chunkResponse = MessageListener.sendMessage(getChunkRequest, lookupResponse.getAddress());

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

    public static ChunkInfoResponse getChunkInfo(String fileID, int chunkNo, int replDegree) {
        for (int replicator = 0; replicator < replDegree; replicator++) {
            final String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNo);

            // find peer that has chunk
            final SocketAddress addressInfo = Chord.chordInstance.lookup(chunkID, replicator); // get assigned peer
            final ChunkLookupRequest lookupRequest = new ChunkLookupRequest(fileID, chunkNo, replicator, Peer.addressInfo); // resolve redirects
            final ChunkLookupResponse lookupResponse = MessageListener.sendMessage(lookupRequest, addressInfo);

            if (lookupResponse == null) {
                System.out.println("Could not read response for ChunkLookupRequest on chunk " + chunkID);
                continue;
            }
            else if (lookupResponse.getStatus() != Status.SUCCESS) {
                System.out.println("Could not find peer with chunk " + chunkID + ", got error " + lookupResponse.getStatus());
                continue;
            }

            final GetChunkInfoRequest getChunkRequest = new GetChunkInfoRequest(fileID, chunkNo);
            final ChunkInfoResponse chunkResponse = MessageListener.sendMessage(getChunkRequest, lookupResponse.getAddress());

            if (chunkResponse == null) {
                System.out.println("Could not read response for chunk " + chunkNo);
                continue;
            } else if (chunkResponse.getStatus() != Status.SUCCESS) {
                System.out.println("Could not retrieve info of chunk " + chunkNo + ", got error " + chunkResponse.getStatus());
                continue;
            }

            System.out.println("Retrieved info of chunk " + chunkNo + " successfully");
            return chunkResponse;
        }
        return null;
    }
}
