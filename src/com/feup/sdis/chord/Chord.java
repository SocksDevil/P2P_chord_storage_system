package com.feup.sdis.chord;

import java.util.ArrayList;
import java.util.Random;

import com.feup.sdis.model.Store;

public class Chord {
    private ArrayList<SocketAddress> fingerTable;
    
    public Chord(){
        fingerTable = new ArrayList<>();
    }

    public void addConnection(SocketAddress connection){
        this.fingerTable.add(connection);
    }

    public SocketAddress getDest(SocketAddress connection, String chunkID){
        if(fingerTable.size() == 1)
            return null;
        if(Store.instance().getReplCount().contains(chunkID))
            Store.instance().getReplCount().removeChunkInfo(chunkID);
        SocketAddress ret = null;
        // TODO: ver ocupação tbm. depois terá de ser sequencial, é só pelos loles para já
        do{
            ret = fingerTable.get(new Random().nextInt(fingerTable.size()));
        } while(ret.equals(connection));
        Store.instance().getReplCount().addNewID(chunkID, connection.toString());
        return ret;
    }

    @Override
    public String toString(){
        String ret = "";
        for(int i = 0; i < fingerTable.size(); i++)
            ret += fingerTable.get(i).toString() + "\n";

        return ret;
    }
}