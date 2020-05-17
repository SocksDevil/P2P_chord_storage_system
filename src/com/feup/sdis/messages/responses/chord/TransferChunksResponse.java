package com.feup.sdis.messages.responses.chord;

import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.ChunkTransfer;

import java.util.List;

public class TransferChunksResponse extends Response {
    private final List<ChunkTransfer> chunkTransfers;

    public TransferChunksResponse(List<ChunkTransfer> chunkTransfers) {
        super(Status.SUCCESS);
        this.chunkTransfers = chunkTransfers;
    }

    public List<ChunkTransfer> getChunkTransfers() {
        return chunkTransfers;
    }
}
