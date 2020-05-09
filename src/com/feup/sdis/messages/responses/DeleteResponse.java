package com.feup.sdis.messages.responses;

import com.feup.sdis.messages.Status;


public class DeleteResponse extends Response {
    private final String fileID;
    private final int chunkNo;

    public DeleteResponse(Status status, String fileID, int chunkNo) {
        super(status);
        this.fileID = fileID;
        this.chunkNo = chunkNo;
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
                '}';
    }
}
