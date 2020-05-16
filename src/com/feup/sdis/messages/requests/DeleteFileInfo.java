package com.feup.sdis.messages.requests;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.DeleteFileInfoResponse;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.Store;

public class DeleteFileInfo extends Request {
    private final String fileID;

    public DeleteFileInfo(String fileID) {
        this.fileID = fileID;
    }

    @Override
    public Response handle() {
        Store.instance().getBackedUpFiles().remove(fileID);
        return new DeleteFileInfoResponse(Status.SUCCESS, fileID);
    }

    @Override
    public SocketAddress getConnection() {
        return null;
    }

    @Override
    public String toString() {
        return "DeleteFileInfo{" +
                "fileID='" + fileID + '\'' +
                '}';
    }
}
