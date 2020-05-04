package com.feup.sdis.actions;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.GetChunkRequest;
import com.feup.sdis.messages.requests.LookupRequest;
import com.feup.sdis.messages.responses.ChunkResponse;
import com.feup.sdis.messages.responses.LookupResponse;
import com.feup.sdis.model.BackupFileInfo;
import com.feup.sdis.model.Store;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.MessageListener;
import com.feup.sdis.peer.Peer;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Restore extends Action {
    private final String filepath;

    public Restore(String[] args) {
        filepath = args[1];
    }

    @Override
    public String process() {
        if (!Store.instance().getBackedUpFilesIds().containsKey(filepath))
            return "Could not find backed up file on " + filepath;

        final BackupFileInfo file = Store.instance().getBackedUpFiles()
                .get(Store.instance().getBackedUpFilesIds().get(filepath));

        for (int i = 0; i < file.getNChunks(); i++) {
            int chunkNo = i;
            BSDispatcher.servicePool.execute(() -> {
                for (int replicator = 0; replicator < file.getDesiredReplicationDegree(); replicator++) {
                    LookupRequest lookupRequest = new LookupRequest(file.getfileID() + "#" + chunkNo
                            , replicator, Peer.addressInfo);

                    LookupResponse lookupResponse = MessageListener.sendMessage(lookupRequest,
                            new SocketAddress(Constants.SERVER_IP, Constants.SERVER_PORT, Constants.peerID));

                    if(lookupResponse == null)
                        continue;

                    GetChunkRequest getChunkRequest = new GetChunkRequest(file.getfileID(), chunkNo);

                    ChunkResponse chunkResponse = MessageListener.sendMessage(getChunkRequest, lookupResponse.getAddress());

                    if (chunkResponse != null && chunkResponse.getStatus() != Status.SUCCESS)
                        continue;

                    file.getRestoredChunks().put(chunkNo, chunkResponse.getData());
                    if (file.isFullyRestored()) {
                        try {
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            for (byte[] chunk : file.getRestoredChunks().values())
                                outputStream.write(chunk);
                            FileOutputStream fos = new FileOutputStream
                                    (Constants.restoredFolder + file.getOriginalFilename());
                            fos.write(outputStream.toByteArray());
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        return "Restored file";
    }
}
