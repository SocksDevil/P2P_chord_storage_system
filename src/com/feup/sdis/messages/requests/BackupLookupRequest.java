package com.feup.sdis.messages.requests;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.BackupLookupResponse;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.PeerInfo;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.MessageListener;
import com.feup.sdis.peer.Peer;

public class BackupLookupRequest extends Request {
    private String fileID;
    private int chunkNo;
    private int currReplication;
    private int chunkLength;
    private SocketAddress connection;
    // TODO: ver se há maneira + limpa de fazer isto
    private boolean redirected;

    public BackupLookupRequest(String fileID, int chunkNo, int currReplication, SocketAddress connection, int chunkLength, boolean redirected) {
        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.currReplication = currReplication;
        this.connection = connection;
        this.chunkLength = chunkLength;
        this.redirected = redirected;
    }

    @Override
    public Response handle() {
        final String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNo);
        System.out.println("<" + chunkID + "> - " + currReplication + " rep " + redirected);

        // System.out.println("Space " + Store.instance().getUsedDiskSpace() + " - " + this.chunkLength + " - " + Constants.MAX_OCCUPIED_DISK_SPACE_MB);
        // If it already has this chunk || doesn't have enough space ->
        boolean isStored = true;
        synchronized(Store.instance().getStoredFiles()){
            if(!Store.instance().getStoredFiles().containsKey(chunkID)){
                // TODO: depois ver o que se por no 2º para se o restore chegar a meio a resposta adequada ser enviada
                // Talvez aqui sim por um either
                Store.instance().getStoredFiles().put(chunkID, new StoredChunkInfo(fileID, chunkNo, chunkLength));
                isStored = false;
            }
        }

        // TODO: make sure that if -> 1 || 2 if 1 is True; 2 is NOT evaluated
        if(Store.instance().getBackedUpFiles().containsKey(fileID) || isStored || !Store.instance().incrementSpace(this.chunkLength)){

            // Remove placeholder if no space for chunk
            if(!isStored)
                Store.instance().getStoredFiles().remove(chunkID);

            // Back to the beginning, traversed a full chord cycle
            if(Store.instance().getReplCount().containsRepDegree(chunkID, this.currReplication)){

                // If it was the responsible (final == beginning), removes the redirect entry
                System.out.println("> BACKUP LOOKUP: Failed " + this.currReplication + " of file " + chunkID);

                Store.instance().getReplCount().removeRepDegree(chunkID, this.currReplication);
                // TODO: ver depois se não era fixe, se já deu a volta ir de imediato para o inicial.
                return new BackupLookupResponse(Status.NO_SPACE, Peer.addressInfo);
            }
            System.out.println("> BACKUP LOOKUP: Redirect to " + Chord.chordInstance.getSuccessor() + " - " + chunkID + " rep " + currReplication);

            // Responsible peer save redirect
            if(!this.redirected){
                Store.instance().getReplCount().addNewID(chunkID, null, this.currReplication);
            }

            return backupChunkInSuccessor(chunkID, fileID, chunkNo, currReplication, chunkLength, redirected);
        }

        System.out.println("> BACKUP LOOKUP: Success - " + Peer.addressInfo + " - " + chunkID );
        Store.instance().getReplCount().addNewID(chunkID, new PeerInfo(Peer.addressInfo, chunkLength), this.currReplication);
        return new BackupLookupResponse(Status.SUCCESS, Peer.addressInfo);
    }

    public static BackupLookupResponse backupChunkInSuccessor(String chunkID, String fileID, int chunkNo, int currReplication, int chunkLength, boolean redirected) {
        // Get successor
        final BackupLookupRequest lookupRequest = new BackupLookupRequest(fileID, chunkNo, currReplication, Chord.chordInstance.getSuccessor(), chunkLength, true);
        final BackupLookupResponse lookupRequestAnswer = MessageListener.sendMessage(lookupRequest, lookupRequest.getConnection());

        // This should never happen
        if(lookupRequestAnswer == null ){
            System.err.println("Received null in lookup response: backing " + chunkNo + " of file " + fileID + " in peer " + Peer.addressInfo);
            return new BackupLookupResponse(Status.ERROR, Peer.addressInfo);
        }

        // System has no available space
        if (lookupRequestAnswer.getStatus() != Status.SUCCESS) {
            System.out.println("> BACKUP LOOKUP: No space available for " + chunkNo + " of file " + fileID);
            return lookupRequestAnswer;
        }

        System.out.println("> BACKUP LOOKUP: Returning " + lookupRequestAnswer.getAddress() + " for " + chunkID + " rep " + currReplication);

        // Responsible peer save redirect
        if(!redirected ){
            Store.instance().getReplCount().removeRepDegree(chunkID, currReplication);
        }
        Store.instance().getReplCount().addNewID(chunkID, new PeerInfo(lookupRequestAnswer.getAddress(), chunkLength), currReplication);

        // Successfully found
        return lookupRequestAnswer;
    }

    @Override
    public SocketAddress getConnection() {
        return this.connection;
    }
    
    @Override
    public String toString() {
        return "BackupLookupRequest{" +
                "fileID='" + fileID + '\'' +
                ", chunkNo=" + chunkNo +
                ", currReplication=" + currReplication +
                ", chunkLength=" + chunkLength +
                ", connection=" + connection +
                '}';
    }
}