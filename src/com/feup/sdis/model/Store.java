package com.feup.sdis.model;

import com.feup.sdis.actions.BSDispatcher;
import com.feup.sdis.peer.Constants;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Store {
    private static Store storeInstance;
    final private ReplicationCounter replCount = new ReplicationCounter(Constants.peerRootFolder + "repl.ser");
    final private SerializableHashMap<BackupFileInfo> backedUpFiles = new SerializableHashMap<>(
            Constants.peerRootFolder + "backed.ser");
    final private SerializableHashMap<StoredChunkInfo> storedFiles = new SerializableHashMap<>(
            Constants.peerRootFolder + "stored.ser");
    final private Set<String> chunksSent = Collections.synchronizedSet(new HashSet<>());
    final private Queue<RequestRetryInfo> retryQueue = new ConcurrentLinkedQueue<>();
    private int usedSpace = 0;

    private Store() {
    }

    public synchronized static Store instance() {
        if (storeInstance == null) {
            storeInstance = new Store();
            storeInstance.calculateUsedDiskSpace();
        }
        return storeInstance;
    }

    public synchronized SerializableHashMap<BackupFileInfo> getBackedUpFiles() {
        return backedUpFiles;
    }

    public synchronized ReplicationCounter getReplCount() {
        return replCount;
    }

    public synchronized SerializableHashMap<StoredChunkInfo> getStoredFiles() {
        return storedFiles;
    }

    public synchronized int getUsedDiskSpace() {
        return this.usedSpace;
    }

    public synchronized void calculateUsedDiskSpace() {
        int total = 0;
        for (Map.Entry<String, StoredChunkInfo> entry : storedFiles.entrySet()) {
            total += entry.getValue().chunkSize;
        }
        this.usedSpace = total;
    }

    public Set<String> getChunksSent() {
        return chunksSent;
    }

    public synchronized boolean incrementSpace(int length) {

        if(this.usedSpace + length <= Constants.MAX_OCCUPIED_DISK_SPACE){
            this.usedSpace += length;
            return true;
        }

        return false;
    }

    public synchronized boolean decrementSpace(int length) {
        if(this.usedSpace - length >= 0){
            this.usedSpace -= length;
            return true;
        }
        System.err.println("> Store: ERROR in space. Decrementing to negative number");
        return false;
    }

    // Returns null if empty
    public synchronized StoredChunkInfo getChunkCandidate(){
        if(this.storedFiles.size() == 0)
            return null;
        int maxFound = -1;
        String chunkToPop = null;
        for (Map.Entry<String, StoredChunkInfo> entry : this.storedFiles.entrySet())
        {
            int chunkSize = entry.getValue().chunkSize;
            if(entry.getValue().pendingDeletion())
                continue;
            if (chunkSize == Constants.BLOCK_SIZE) { // there wont be bigger chunks
                chunkToPop = entry.getKey();
                break;
            }

            if (chunkSize > maxFound) {
                maxFound = chunkSize;
                chunkToPop = entry.getKey();
            }
        }

        StoredChunkInfo storedChunkInfo = this.storedFiles.get(chunkToPop);
        storedChunkInfo.setPendingDeletion(true);
        return storedChunkInfo;
    }

    public synchronized void retryRequest() {
        if (retryQueue.isEmpty()) return;

        RequestRetryInfo nextRequest = retryQueue.poll();
        System.out.println("> RETRY: Retrying request ");
        nextRequest.decrementRetries();
        Future<Boolean> receivedAnswer = BSDispatcher.servicePool.submit(nextRequest.getRequest());

        BSDispatcher.servicePool.execute(() -> {
            try {
                Boolean answer = receivedAnswer.get();
                if (!answer) {
                    if (nextRequest.reachedMaxRetries()) {
                        System.out.println("> RETRY: Reached max tries for request ");
                    }
                    else {
                        retryQueue.add(nextRequest);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        });
    }

    public synchronized void addRequestToRetryQueue(RequestRetryInfo reqInfo) {
        this.retryQueue.add(reqInfo);
    }

}