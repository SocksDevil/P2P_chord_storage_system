package com.feup.sdis.actions;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.requests.DeleteRequest;
import com.feup.sdis.messages.responses.ChunkResponse;
import com.feup.sdis.messages.responses.DeleteResponse;
import com.feup.sdis.model.RestoredFileInfo;
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
        final ChunkResponse response = Restore.getChunk(fileID, 0, Constants.MAX_REPL_DEGREE);
        if (response == null) {
            final String error = "File " + fileID + " not found";
            System.out.println(error);
            return error;
        }
        final RestoredFileInfo file = new RestoredFileInfo(fileID, response.getReplDegree(), response.getnChunks());
        Store.instance().getBackedUpFiles().remove(fileID);
        //TODO delete BackupFileInfo from the peer that initiated the BACKUP. need chord stuff to figure it out

        for(int chunkNo = 0; chunkNo < file.getNChunks(); chunkNo++) {
            for (int replDegree = 0; replDegree < file.getDesiredReplicationDegree(); replDegree++) {
                int chunkNumber = chunkNo;
                int replicationDegree = replDegree;
                BSDispatcher.servicePool.execute(() -> {

                    String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNumber);
                    final SocketAddress addressInfo = Chord.chordInstance.lookup(chunkID,replicationDegree);

                    DeleteRequest deleteRequest = new DeleteRequest(fileID, chunkNumber);
                    DeleteResponse deleteResponse = MessageListener.sendMessage(deleteRequest,addressInfo);

                    if (deleteResponse == null) {
                        System.out.println("Could not read DELETE response for chunk " + chunkNumber);
                        return;
                    }

                    switch (deleteResponse.getStatus()) {
                        case SUCCESS:
                            System.out.println("Deleted chunk " + chunkNumber + " from " + addressInfo.toString());
                            break;
                        case FILE_NOT_FOUND:
                            System.out.println("Chunk " + chunkNumber + " was not present in " + addressInfo.toString());
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
