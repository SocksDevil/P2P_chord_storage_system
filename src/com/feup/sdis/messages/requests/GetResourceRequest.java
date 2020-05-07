package com.feup.sdis.messages.requests;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.GetResourceResponse;
import com.feup.sdis.messages.responses.LookupResponse;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.peer.Server;

public class GetResourceRequest extends Request {

    private final String chunkID;
    private final int currRepDegree;

    public GetResourceRequest(String chunkID, int currRepDegree) {
        this.chunkID = chunkID;
        this.currRepDegree = currRepDegree;
    }

    @Override
    public Response handle() {
        final SocketAddress addressInfo = Server.chord.getResource(this.chunkID, this.currRepDegree);

        if (addressInfo == null) {
            System.out.println("TODO: An error occurred on LookupMessage.handle");
            return new GetResourceResponse(Status.ERROR, null);
        }

        return new GetResourceResponse(Status.SUCCESS, addressInfo);
    }

    @Override
    public SocketAddress getConnection() {
        return null;
    }
}
