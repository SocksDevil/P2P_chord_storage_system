package com.feup.sdis.chord;

import java.util.ArrayList;

import com.feup.sdis.model.Store;

public class Chord {
    private ArrayList<SocketAddress> fingerTable;
    
    public Chord(){
        fingerTable = new ArrayList<>();
    }

    public void addConnection(SocketAddress connection){
        this.fingerTable.add(connection);
    }

    public SocketAddress getDest(SocketAddress connection, String chunkID, int currRepDegree){
        if(fingerTable.size() == 1)
            return null;

        SocketAddress addr = null;
        if(Store.instance().getReplCount().containsRepDegree(chunkID, currRepDegree))
            addr = Store.instance().getReplCount().removeRepDegree(chunkID, currRepDegree);

        SocketAddress ret = null;
        // TODO: ver ocupação tbm. depois terá de ser sequencial, é só pelos loles para já

        for (int i = 0; i < fingerTable.size(); i++) {
            
            ret = fingerTable.get(i);
            if(!ret.equals(connection) && !Store.instance().getReplCount().containsPeer(chunkID, ret)){
                System.out.println("Addr: " + addr + " Connection: " + connection + " Ret: " + ret);
                if((addr != null  && !addr.equals(ret)) || addr == null){
                    Store.instance().getReplCount().addNewID(chunkID, ret, currRepDegree);
                    return ret;
                }
            }
        }

        return null;
    }

    @Override
    public String toString(){
        String ret = "";
        for(int i = 0; i < fingerTable.size(); i++)
            ret += fingerTable.get(i).toString() + "\n";

        return ret;
    }
}