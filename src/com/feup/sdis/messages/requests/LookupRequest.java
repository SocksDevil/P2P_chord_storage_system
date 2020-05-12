package com.feup.sdis.messages.requests;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.exceptions.MessageError;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.LookupResponse;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.MessageListener;
import com.feup.sdis.peer.Peer;

public class LookupRequest extends Request {
    private String fileID;
    private int chunkNo;
    private int currReplication;
    private int chunkLength;
    private SocketAddress connection;
    // TODO: ver se há maneira + limpa de fazer isto
    private boolean redirected;

    public LookupRequest(String fileID, int chunkNo, int currReplication, SocketAddress connection, int chunkLength, boolean redirected) {
        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.currReplication = currReplication;
        this.connection = connection;
        this.chunkLength = chunkLength;
        this.redirected = redirected;
    }

    @Override
    public Response handle() {
        System.out.println("<" + StoredChunkInfo.getChunkID(fileID, chunkNo) + "> - " + currReplication + " red " + redirected);

        // System.out.println("Space " + Store.instance().getUsedDiskSpace() + " - " + this.chunkLength + " - " + Constants.MAX_OCCUPIED_DISK_SPACE_MB);
        // If it already has this chunk || doesn't have enough space ->
        boolean isStored = true;
        synchronized(Store.instance().getStoredFiles()){
            if(!Store.instance().getStoredFiles().containsKey(StoredChunkInfo.getChunkID(fileID, chunkNo))){
                // TODO: depois ver o que se por no 2º para se o restore chegar a meio a resposta adequada ser enviada
                // Talvez aqui sim por um either
                Store.instance().getStoredFiles().put(StoredChunkInfo.getChunkID(fileID, chunkNo), new StoredChunkInfo(fileID, chunkNo, chunkLength));
                isStored = false;
            }
        }

        // TODO: make sure that if -> 1 || 2 if 1 is True; 2 is NOT evaluated
        if(isStored || !Store.instance().incrementSpace(this.chunkLength)){
            // Back to the beginning
            if(Store.instance().getReplCount().containsRepDegree(StoredChunkInfo.getChunkID(fileID, chunkNo), this.currReplication)){

                // If it was the responsible (final == beginning), removes the redirect entry
                System.out.println("> LOOKUP: Failed  " + chunkNo + " of file " + fileID);

                Store.instance().getReplCount().removeRepDegree(StoredChunkInfo.getChunkID(fileID, chunkNo), this.currReplication);
                // TODO: ver depois se não era fixe, se já deu a volta ir de imediato para o inicial.
                return new LookupResponse(Status.NO_SPACE, Peer.addressInfo);
            }
            System.out.println("> LOOKUP: Redirect to " + Chord.chordInstance.getSucessor() + " - " + StoredChunkInfo.getChunkID(fileID, chunkNo) + " rep " + currReplication);
            
            // Get successor
            LookupRequest lookupRequest = new LookupRequest(fileID, chunkNo, currReplication, Chord.chordInstance.getSucessor(), this.chunkLength, true);            
            LookupResponse lookupRequestAnswer = MessageListener.sendMessage(lookupRequest, lookupRequest.getConnection());

            // This should never happen
            if(lookupRequestAnswer == null ){
                System.err.println("Received null in lookup response: backing " + chunkNo + " of file " + fileID + " in peer " + Peer.addressInfo);
                return null;
            }

            // System has no available space
            if (lookupRequestAnswer.getStatus() == Status.NO_SPACE) {
                System.out.println("> LOOKUP: No space available for " + chunkNo + " of file " + fileID);
                return new LookupResponse(Status.NO_SPACE, Peer.addressInfo);
            }

            // Responsible peer save redirect
            if(!this.redirected){
                Store.instance().getReplCount().addNewID(StoredChunkInfo.getChunkID(fileID, chunkNo), lookupRequestAnswer.getAddress(), this.currReplication);
            }

            // Successfully found
            System.out.println("> LOOKUP: Returning " + lookupRequestAnswer.getAddress() + " for " + StoredChunkInfo.getChunkID(fileID, chunkNo) + " rep " + currReplication);
            return lookupRequestAnswer;
        }

        System.out.println("> LOOKUP: Success - " + Peer.addressInfo + " - " + StoredChunkInfo.getChunkID(fileID, chunkNo) );
        return new LookupResponse(Status.SUCCESS, Peer.addressInfo);
    }

    @Override
    public SocketAddress getConnection() {
        return this.connection;
    }
    
    @Override
    public String toString() {
        return "LookupRequest{" +
                "fileID='" + fileID + '\'' +
                ", chunkNo=" + chunkNo +
                ", currReplication=" + currReplication +
                ", chunkLength=" + chunkLength +
                ", connection=" + connection +
                '}';
    }
}