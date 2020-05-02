package com.feup.sdis.messages;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.peer.Server;

public class LookupMessage extends Message {

    private String chunkID;
    private SocketAddress addressInfo;

    // chunkID -> hash(fileName#chunkNo#repID)
    public LookupMessage(String chunkID, SocketAddress addressInfo){

        // TODO: if args != X throw IllegalArgumentException/MessageError...
        this.chunkID = chunkID;
        this.addressInfo = addressInfo;
    }

    @Override
    public Message handle() {
        this.addressInfo = Server.chord.getDest(this.addressInfo, this.chunkID);
     
        if(this.addressInfo == null){
            System.out.println("TODO: An error occured on LookupMessage.handle");
            return null;
        }
        
        return  this;
    }

	@Override
	public SocketAddress getConnection() {
		
		return this.addressInfo;
	}

    @Override
    public String toString(){

        return "LOOKUP: " + this.chunkID + "-" + this.addressInfo; 
    }
    
}