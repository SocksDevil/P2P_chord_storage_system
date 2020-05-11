package com.feup.sdis.model;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class RestoredFileInfo implements Serializable {
    final private String fileID;
    final private int desiredReplicationDegree;
    final private SortedMap<Integer, byte[]> restoredChunks = new ConcurrentSkipListMap<>();
    final private int nChunks;

    public RestoredFileInfo(String fileID, int desiredReplicationDegree, int nChunks) {
        this.fileID = fileID;
        this.desiredReplicationDegree = desiredReplicationDegree;
        this.nChunks = nChunks;
    }

    public synchronized boolean isFullyRestored() {
        return restoredChunks.size() == nChunks;
    }

    public SortedMap<Integer, byte[]> getRestoredChunks() {
        return restoredChunks;
    }

    public String getFileID(){
        return this.fileID;
    }

    public int getNChunks() {
        return nChunks;
    }

    public int getDesiredReplicationDegree() {
        return desiredReplicationDegree;
    }
}
