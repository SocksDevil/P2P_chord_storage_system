package com.feup.sdis.messages;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.peer.Server;

public class LookupMessage extends Message {

    private String fileID;
    private int chunkID;
    private int repDegree;
    private SocketAddress addressInfo;

    // chunkID -> hash(fileName#chunkNo#repID)
    public LookupMessage(String fileID, int chunkID, int repDegree, SocketAddress addressInfo){

        // TODO: if args != X throw IllegalArgumentException/MessageError...
        this.fileID = fileID;
        this.chunkID = chunkID;
        this.repDegree = repDegree;
        this.addressInfo = addressInfo;
    }

    @Override
    public Message handle() {
        this.addressInfo = Server.chord.getDest(this.addressInfo);
        
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