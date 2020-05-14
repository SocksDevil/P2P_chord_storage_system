package com.feup.sdis.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.feup.sdis.chord.SocketAddress;

public class ReplicationCounter extends SerializableHashMap<Map<Integer,SocketAddress>>{

    ReplicationCounter(String filename) {
        super(filename);
    }

    public synchronized int getSize(String key){
        return this.getOrDefault(key, new HashMap<>()).size();
    }

    public synchronized void removeChunkInfo(String key){
        this.remove(key);
    }

    public synchronized void addNewID(String key, SocketAddress peer, Integer repDegree){
        Map<Integer,SocketAddress> peers = this.getOrDefault(key, new HashMap<>());
        peers.put(repDegree, peer);
        this.files.put(key, peers);
        this.updateObject();
    }

    public synchronized Integer removePeerID(String key, SocketAddress peerId){
        Map<Integer,SocketAddress> peers = this.getOrDefault(key, new HashMap<>());
        Integer repDegree = null;
        
        for (Map.Entry<Integer, SocketAddress> entry : peers.entrySet())
            if (peerId.equals(entry.getValue())) {
                repDegree = entry.getKey();
                break;
            }

        if (repDegree != null)
            peers.remove(repDegree);

        this.files.put(key, peers);
        this.updateObject();

        return repDegree;
    }

    public synchronized SocketAddress removeRepDegree(String key, Integer repDegree){
        Map<Integer,SocketAddress> peers = this.getOrDefault(key, new HashMap<>());
        SocketAddress addr = peers.remove(repDegree);
        this.files.put(key, peers);
        this.updateObject();

        return addr;
    }


    public synchronized boolean contains(String key){
        return this.files.containsKey(key);
    }

    public synchronized boolean containsPeer(String key, SocketAddress peerId){

        System.out.println("Key: " + key + "SocketAddr: " + peerId);
        System.out.println(this.getOrDefault(key, new HashMap<>()).containsValue(peerId));

        return this.getOrDefault(key, new HashMap<>()).containsValue(peerId);
    }

    public synchronized boolean containsRepDegree(String key, Integer repDegree){
        return this.getOrDefault(key, new HashMap<>()).containsKey(repDegree);
    }

    public synchronized SocketAddress getPeerAddress(String key, Integer repDegree){
        return this.getOrDefault(key, new HashMap<>()).get(repDegree);
    }

    @Override
    public synchronized Map<Integer, SocketAddress> getOrDefault(String s, Map<Integer, SocketAddress> map) {
        return super.getOrDefault(s, Collections.synchronizedMap(map));
    }
}

