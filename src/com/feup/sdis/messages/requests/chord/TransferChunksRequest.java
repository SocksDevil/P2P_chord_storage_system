package com.feup.sdis.messages.requests.chord;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.requests.GetChunkRequest;
import com.feup.sdis.messages.requests.Request;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.messages.responses.chord.TransferChunksResponse;
import com.feup.sdis.model.ChunkTransfer;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TransferChunksRequest extends Request {
    private final UUID peerKey;

    public TransferChunksRequest(UUID peerKey) {
        this.peerKey = peerKey;
    }

    @Override
    public Response handle() {
        List<ChunkTransfer> chunkTransfers = new ArrayList<>();

        for (Map.Entry<String, Map<Integer, SocketAddress>> storedChunk : Store.instance().getReplCount().entrySet()) {
            for (Map.Entry<Integer, SocketAddress> addressEntry : storedChunk.getValue().entrySet()) {
                if (Chord.chordInstance.betweenTwoKeys(peerKey, Chord.chordInstance.getSelf().getPeerID(),
                        Chord.chordInstance.generateKey(storedChunk.getKey(),
                                addressEntry.getKey()),
                        false, true))
                    chunkTransfers.add(new ChunkTransfer(addressEntry.getValue(),
                            TakeChunkRequest.createRequestFromChunkID(storedChunk.getKey(), addressEntry.getKey())));
            }

        }

        return new TransferChunksResponse(chunkTransfers);
    }

    @Override
    public SocketAddress getConnection() {
        return null;
    }
}
