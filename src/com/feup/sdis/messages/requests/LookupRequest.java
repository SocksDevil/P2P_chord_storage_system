package com.feup.sdis.messages.requests;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.LookupResponse;
import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.peer.Server;

public class LookupRequest extends Request {

    private String chunkID;
    private int currRepDegree;
    private SocketAddress addressInfo;

    // chunkID -> hash(fileName#chunkNo#repID)
    public LookupRequest(String chunkID, int currRepDegree, SocketAddress addressInfo){

        // TODO: if args != X throw IllegalArgumentException/MessageError...
        this.chunkID = chunkID;
        this.currRepDegree = currRepDegree;
        this.addressInfo = addressInfo;
    }

    @Override
    public Response handle() {
        final SocketAddress addressInfo = Server.chord.getDest(this.addressInfo, this.chunkID, this.currRepDegree);
     
        if(addressInfo == null){
            System.out.println("TODO: An error occured on LookupMessage.handle");
            return new LookupResponse(Status.ERROR, null);
        }
        
        return new LookupResponse(Status.SUCCESS, addressInfo);
    }

	@Override
	public SocketAddress getConnection() {
		
		return this.addressInfo;
	}

    @Override
    public String toString(){

        return "LOOKUP: " + this.chunkID + "-" +this.currRepDegree + "-"+ this.addressInfo; 
    }
    
}