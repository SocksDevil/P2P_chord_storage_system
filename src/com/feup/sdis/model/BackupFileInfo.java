package com.feup.sdis.model;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class BackupFileInfo implements Serializable {

    final private String fileID;
    final private String originalFilename;
    final private String originalPath;
    final private int nChunks;
    final private int desiredReplicationDegree;
    final private SortedMap<Integer, byte[]> restoredChunks = new ConcurrentSkipListMap<>();

    public BackupFileInfo(String fileID, String originalFilename, String originalPath, int nChunks, int desiredReplicationDegree) {
        this.fileID = fileID;
        this.originalFilename = originalFilename;
        this.originalPath = originalPath;
        this.nChunks = nChunks;
        this.desiredReplicationDegree = desiredReplicationDegree;
    }

    public String getfileID() { return fileID; }

    public String getOriginalFilename() { return originalFilename; }

    public String getOriginalPath() { return originalPath; }

    public int getNChunks() {
        return nChunks;
    }

    public int getDesiredReplicationDegree() { return desiredReplicationDegree; }

    public SortedMap<Integer, byte[]> getRestoredChunks() {
        return restoredChunks;
    }

    public boolean isFullyRestored() {
        return restoredChunks.size() == nChunks;
    }

}
