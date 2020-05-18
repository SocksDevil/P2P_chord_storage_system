package com.feup.sdis.messages.requests.chord;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.requests.Request;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.messages.responses.chord.TransferChunksResponse;
import com.feup.sdis.model.ChunkTransfer;
import com.feup.sdis.model.PeerInfo;
import com.feup.sdis.model.Store;
import com.feup.sdis.peer.Peer;

import java.util.*;

public class TransferChunksRequest extends Request {
    private final UUID peerKey;
    private final UUID peerPredecessor;

    public TransferChunksRequest(UUID peerKey, UUID peerPredecessor) {
        this.peerKey = peerKey;
        this.peerPredecessor = peerPredecessor;
    }

    @Override
    public Response handle() {
        final List<ChunkTransfer> chunkTransfers = new ArrayList<>();
        final List<Map.Entry<String, Map.Entry<Integer, PeerInfo>>> redirects = new ArrayList<>();

        for (Map.Entry<String, Map<Integer, PeerInfo>> storedChunk : Store.instance().getReplCount().entrySet()) {
            for (Map.Entry<Integer, PeerInfo> addressEntry : storedChunk.getValue().entrySet()) {
                if (isTransferable(storedChunk, addressEntry))
                    chunkTransfers.add(new ChunkTransfer(
                            TakeChunkRequest.createRequestFromChunkID(storedChunk.getKey(),
                                    addressEntry.getKey()), addressEntry.getValue().getChunkSize()));

                if (!addressEntry.getValue().getAddress().equals(Peer.addressInfo))
                    redirects.add(new AbstractMap.SimpleEntry<>(storedChunk.getKey(), addressEntry));
            }

        }

        return new TransferChunksResponse(chunkTransfers, redirects);
    }

    private boolean isTransferable(Map.Entry<String, Map<Integer, PeerInfo>> storedChunk, Map.Entry<Integer, PeerInfo> addressEntry) {
        return Chord.chordInstance.betweenTwoKeys(peerPredecessor, peerKey,
                Chord.chordInstance.generateKey(storedChunk.getKey(),
                        addressEntry.getKey()),
                false, true) && addressEntry.getValue().getAddress().equals(Peer.addressInfo);
    }

    @Override
    public SocketAddress getConnection() {
        return null;
    }
}
