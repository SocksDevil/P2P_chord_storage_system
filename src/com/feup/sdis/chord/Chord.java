package com.feup.sdis.chord;

import java.util.ArrayList;
import java.util.UUID;

import com.feup.sdis.messages.requests.ChordClosestPreceedingRequest;
import com.feup.sdis.messages.responses.ChordClosestPreceedingResponse;
import com.feup.sdis.peer.MessageListener;


public class Chord {

    public static Chord chordInstance;
    private ArrayList<SocketAddress> fingerTable;
    private SocketAddress self = null;
    private SocketAddress predecessor = null;
    private SocketAddress successor = null;
    
    // create
    public Chord(SocketAddress self){
        this.fingerTable = new ArrayList<>(7);
        this.self = self;
        this.successor = self;
    }

    // join
    public Chord(SocketAddress self, SocketAddress node){
        this(self);

        ChordClosestPreceedingResponse res =  MessageListener.sendMessage(new ChordClosestPreceedingRequest(self), node);
        this.successor = res.getAddress();
    }


    public SocketAddress closestPreceedingNode(UUID key){

        for (int i = fingerTable.size() - 1; i >= 0; i--) {
            
            if(this.fingerTable.get(i).getPeerID().compareTo(self.getPeerID()) > 0 && this.fingerTable.get(i).getPeerID().compareTo(key) < 0)
                return this.fingerTable.get(i);

        }

        return self;
    }

    @Override
    public String toString(){
        String ret = "";
        for(int i = 0; i < fingerTable.size(); i++)
            ret += fingerTable.get(i).toString() + "\n";

        return ret;
    }
}