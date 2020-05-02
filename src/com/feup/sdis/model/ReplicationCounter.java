package com.feup.sdis.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ReplicationCounter extends SerializableHashMap<String>{

    ReplicationCounter(String filename) {
        super(filename);
    }

    // public synchronized int getSize(String key){
    //     return this.getOrDefault(key, new HashSet<>()).size();
    // }

    public synchronized void removeChunkInfo(String key){
        this.remove(key);
    }

    public synchronized void addNewID(String key, String peerId){
        // Set<String> peers = this.getOrDefault(key, new HashSet<>());
        // peers.add(peerId);
        this.files.put(key, peerId);
        this.updateObject();
    }

    public synchronized void removeID(String key, String peerId){
        // Set<String> peers = this.getOrDefault(key, new HashSet<>());
        // peers.remove(peerId);
        this.files.put(key, peerId);
        this.updateObject();
    }

    public synchronized boolean contains(String key){
        return this.files.containsKey(key);
    }

    public synchronized boolean containsPeer(String key, String peerId){
        return this.getOrDefault(key, "").equals(peerId);
    }

    @Override
    public synchronized String getOrDefault(String s, String string) {
        return super.getOrDefault(s, string);
    }
}

