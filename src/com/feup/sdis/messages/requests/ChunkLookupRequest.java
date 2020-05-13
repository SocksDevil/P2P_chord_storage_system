package com.feup.sdis.messages.requests;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.ChunkLookupResponse;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;
import com.feup.sdis.peer.MessageListener;
import com.feup.sdis.peer.Peer;

public class ChunkLookupRequest extends Request {
    private String fileID;
    private int chunkNo;
    private int replNo;
    private SocketAddress connection;

    public ChunkLookupRequest(String fileID, int chunkNo, int replNo, SocketAddress connection) {
        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.replNo = replNo;
        this.connection = connection;
    }

    @Override
    public Response handle() {
        String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNo);
        Store store = Store.instance();

        SocketAddress peerWithChunk = Peer.addressInfo;
        Status status = Status.SUCCESS;
        if (!store.getStoredFiles().containsKey(chunkID)) {
            SocketAddress redirectAddress = store.getReplCount().getRepDegree(chunkID, replNo);
            System.out.println("> LOOKUP: Redirect to " + redirectAddress + " - " + chunkID + " rep " + replNo);

            ChunkLookupRequest lookupRedirect = new ChunkLookupRequest(fileID, chunkNo, replNo, redirectAddress);
            ChunkLookupResponse redirectAnswer = MessageListener.sendMessage(lookupRedirect, redirectAddress);

            if (redirectAnswer == null || redirectAnswer.getAddress() == null) {
                System.err.println("Received null in lookup response: searching for chunk " + chunkNo + " of file " + fileID + " in peer " + Peer.addressInfo);
                return null;
            }

            peerWithChunk = redirectAnswer.getAddress();
        }

        if (status == Status.SUCCESS)
            System.out.println("> LOOKUP: Success! Found " + peerWithChunk + " for " + chunkID + " rep " + replNo);

        return new ChunkLookupResponse(status, peerWithChunk);
    }

    @Override
    public SocketAddress getConnection() {
        return this.connection;
    }

    @Override
    public String toString() {
        return "ChunkLookupRequest{" +
                "fileID='" + fileID + '\'' +
                ", chunkNo=" + chunkNo +
                ", replNo=" + replNo +
                ", connection=" + connection +
                '}';
    }
}