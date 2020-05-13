package com.feup.sdis.messages.responses;

import com.feup.sdis.messages.Status;


public class DeleteResponse extends Response {
    private final String fileID;
    private final int chunkNo;
    private final int replNo;

    public DeleteResponse(Status status, String fileID, int chunkNo, int replNo) {
        super(status);
        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.replNo = replNo;
    }

    public String getFileID() {
        return fileID;
    }

    @Override
    public String toString() {
        return "DeleteResponse{" +
                "status=" + getStatus() +
                ", fileID='" + fileID + '\'' +
                ", chunkNo=" + chunkNo +
                ", replNo=" + replNo +
                '}';
    }
}
