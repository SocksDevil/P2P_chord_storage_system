package com.feup.sdis.messages.requests;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.ChunkLookupResponse;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.PeerInfo;
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
        final String chunkID = StoredChunkInfo.getChunkID(fileID, chunkNo);
        final Store store = Store.instance();

        SocketAddress peerWithChunk = Peer.addressInfo;
        PeerInfo peerInfo = store.getReplCount().getPeerAddress(chunkID, replNo);

        if (peerInfo == null) {
            System.out.println("> CHUNK LOOKUP: redirect address is null for chunk " + chunkNo + " of file " + fileID + ", replNo = " + replNo);
            return new ChunkLookupResponse(Status.FILE_NOT_FOUND, Peer.addressInfo);
        }
        if (!store.getStoredFiles().containsKey(chunkID) ||
                !peerInfo.getAddress().equals(Peer.addressInfo) ) { // TODO may contain placeholder, handle this somewhere
            

            final SocketAddress redirectAddress = peerInfo.getAddress();

            System.out.println("> CHUNK LOOKUP: Redirect to " + redirectAddress + " - " + chunkID + " rep " + replNo);

            final ChunkLookupRequest lookupRedirect = new ChunkLookupRequest(fileID, chunkNo, replNo, redirectAddress);
            final ChunkLookupResponse redirectAnswer = MessageListener.sendMessage(lookupRedirect, redirectAddress);

            if (redirectAnswer == null || redirectAnswer.getAddress() == null) {
                System.err.println("> CHUNK LOOKUP: Received null when searching for chunk " + chunkNo + " of file " + fileID + " in peer " + Peer.addressInfo);
                return new ChunkLookupResponse(Status.ERROR, Peer.addressInfo);
            }

            if (redirectAnswer.getStatus() == Status.ERROR || redirectAnswer.getStatus() == Status.FILE_NOT_FOUND) {
                System.out.println("> CHUNK LOOKUP: Did not find chunk " + chunkNo + " of file " + fileID);
                return new ChunkLookupResponse(Status.FILE_NOT_FOUND, Peer.addressInfo);
            }

            peerWithChunk = redirectAnswer.getAddress();
        }

        System.out.println("> CHUNK LOOKUP: Success! Found " + peerWithChunk + " for " + chunkID + " rep " + replNo);
        return new ChunkLookupResponse(Status.SUCCESS, peerWithChunk);
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