package com.feup.sdis.messages.requests.chord;

import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.requests.GetChunkRequest;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;

public class TakeChunkRequest extends GetChunkRequest {

    public TakeChunkRequest(String fileID, int chunkNo) {
        super(fileID, chunkNo);
    }

    public static TakeChunkRequest createRequestFromChunkID(String chunkID){
        String[] id = chunkID.split("#");
        final String fileID = id[0];
        final int chunkNo = Integer.parseInt(id[1]);
        return new TakeChunkRequest(fileID, chunkNo);
    }

    @Override
    public Response handle() {
        final Response response = super.handle();

        if (response.getStatus() == Status.SUCCESS) {
            Store.instance().getStoredFiles().remove(StoredChunkInfo.getChunkID(fileID, chunkNo));
            Store.instance().getReplCount().removeRepDegree(fileID, chunkNo);
        }

        return response;
    }
}
