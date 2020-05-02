package com.feup.sdis.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReplicationCounter extends SerializableHashMap<Map<Integer,String>>{

    ReplicationCounter(String filename) {
        super(filename);
    }

    public synchronized int getSize(String key){
        return this.getOrDefault(key, new HashMap<>()).size();
    }

    public synchronized void removeChunkInfo(String key){
        this.remove(key);
    }

    public synchronized void addNewID(String key, String peer, Integer repDegree){
        Map<Integer,String> peers = this.getOrDefault(key, new HashMap<>());
        peers.put(repDegree, peer);
        this.files.put(key, peers);
        this.updateObject();
    }

    // public synchronized void removeID(String key, String peerId){
    //     Map<Integer,String> peers = this.getOrDefault(key, new HashMap<>());
    //     peers.remove(peerId);
    //     this.files.put(key, peers);
    //     this.updateObject();
    // }

    public synchronized void removeRepDegree(String key, Integer repDegree){
        Map<Integer,String> peers = this.getOrDefault(key, new HashMap<>());
        peers.remove(repDegree);
        this.files.put(key, peers);
        this.updateObject();
    }


    public synchronized boolean contains(String key){
        return this.files.containsKey(key);
    }

    // public synchronized boolean containsPeer(String key, String peerId){
    //     return this.getOrDefault(key, new HashSet<>()).contains(peerId);
    // }

    public synchronized boolean containsRepDegree(String key, Integer repDegree){
        return this.getOrDefault(key, new HashMap<>()).containsKey(repDegree);
    }

    @Override
    public synchronized Map<Integer, String> getOrDefault(String s, Map<Integer, String> map) {
        return super.getOrDefault(s, Collections.synchronizedMap(map));
    }
}

