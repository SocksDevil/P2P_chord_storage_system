package com.feup.sdis.messages.responses.chord;

import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.ChunkTransfer;
import com.feup.sdis.model.PeerInfo;

import java.util.List;
import java.util.Map;

public class TransferChunksResponse extends Response {
    private final List<ChunkTransfer> chunkTransfers;
    private final List<Map.Entry<String, Map.Entry<Integer, PeerInfo>>> redirects;

    public TransferChunksResponse(List<ChunkTransfer> chunkTransfers, List<Map.Entry<String, Map.Entry<Integer, PeerInfo>>> redirects) {
        super(Status.SUCCESS);
        this.chunkTransfers = chunkTransfers;
        this.redirects = redirects;
    }

    public List<ChunkTransfer> getChunkTransfers() {
        return chunkTransfers;
    }

    public List<Map.Entry<String, Map.Entry<Integer, PeerInfo>>> getRedirects() {
        return redirects;
    }
}
