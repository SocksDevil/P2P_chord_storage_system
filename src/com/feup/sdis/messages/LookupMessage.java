package com.feup.sdis.messages;

import com.feup.sdis.chord.Connection;
import com.feup.sdis.peer.Server;

public class LookupMessage extends Message {

    private String file;
    private int repDegree;
    private Connection connection;

    public LookupMessage(String[] args, Connection connection){
        // TODO: if args != X throw IllegalArgumentException/MessageError...
        this.file = args[1];
        this.repDegree = Integer.parseInt(args[2]);
        this.connection = connection;
    }

    @Override
    public Message handle() {
        this.connection = Server.chord.getDest(this.connection);
        
        if(this.connection == null){
            System.out.println("TODO: An error occured on LookupMessage.handle");
            return null;
        }
        
        return  this;
    }

	@Override
	public Connection getConnection() {
		
		return this.connection;
	}

    @Override
    public String toString(){

        return "LOOKUP: " + file + "-" + this.connection; 
    }
    
}