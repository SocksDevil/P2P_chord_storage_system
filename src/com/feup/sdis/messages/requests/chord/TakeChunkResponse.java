package com.feup.sdis.messages.requests.chord;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.Response;

public class TakeChunkResponse extends Response {
    private final String fileID;
    private final int chunkNo;
    private final int replNo;
    private final byte[] data;
    private final int nChunks;
    private final String originalFileName;
    private final SocketAddress initiatorPeer;

    public TakeChunkResponse(Status returnStatus, String fileID, int chunkNo, int replNo, byte[] data, int nChunks, String originalFileName, SocketAddress initiatorPeer) {
        super(returnStatus);
        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.replNo = replNo;
        this.data = data;
        this.nChunks = nChunks;
        this.originalFileName = originalFileName;
        this.initiatorPeer = initiatorPeer;
    }

    public String getFileID() {
        return fileID;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public int getReplNo() {
        return replNo;
    }

    public byte[] getData() {
        return data;
    }

    public int getnChunks() {
        return nChunks;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public SocketAddress getInitiatorPeer() {
        return initiatorPeer;
    }
}
