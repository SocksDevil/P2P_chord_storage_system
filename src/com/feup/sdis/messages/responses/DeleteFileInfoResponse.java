package com.feup.sdis.messages.responses;

import com.feup.sdis.messages.Status;


public class DeleteFileInfoResponse extends Response {
    private final String fileID;

    public DeleteFileInfoResponse(Status status, String fileID) {
        super(status);
        this.fileID = fileID;
    }

    public String getFileID() {
        return fileID;
    }

    @Override
    public String toString() {
        return "DeleteResponse{" +
                "status=" + getStatus() +
                ", fileID='" + fileID + '\'' +
                '}';
    }
}
