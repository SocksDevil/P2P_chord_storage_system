package com.feup.sdis.model;

import com.feup.sdis.messages.responses.Response;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.MessageListener;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Store {
    private static Store storeInstance;
    final private ReplicationCounter replCount = new ReplicationCounter(Constants.peerRootFolder + "repl.ser");
    final private SerializableHashMap<BackupFileInfo> backedUpFiles = new SerializableHashMap<>(
            Constants.peerRootFolder + "backed.ser");
    final private SerializableHashMap<StoredChunkInfo> storedFiles = new SerializableHashMap<>(
            Constants.peerRootFolder + "stored.ser");
    final private Set<String> chunksSent = Collections.synchronizedSet(new HashSet<>());
    final private Queue<RequestInfo> retryQueue = new ConcurrentLinkedQueue<>();
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

        if(this.usedSpace + length <= Constants.MAX_OCCUPIED_DISK_SPACE_MB){
            this.usedSpace += length;
            return true;
        }

        return false;
    }

    public synchronized StoredChunkInfo popChunkFromStored(){
        if(this.storedFiles.size() == 0)
            return null;

        int maxFound = -1;
        String chunkToPop = null;
        for (Map.Entry<String,StoredChunkInfo> entry : this.storedFiles.entrySet()) {
            int chunkSize = entry.getValue().chunkSize;
            if (chunkSize == Constants.BLOCK_SIZE) { // there wont be bigger chunks
                chunkToPop = entry.getKey();
                break;
            }

            if (chunkSize > maxFound) {
                maxFound = chunkSize;
                chunkToPop = entry.getKey();
            }
        }
        
        return this.storedFiles.remove(chunkToPop);
    }

    public synchronized void retryRequest() {
        if (retryQueue.isEmpty()) return;

        RequestInfo nextRequest = retryQueue.poll();
        System.out.println("> RETRY: Retrying request " + nextRequest.getRequest() + " to peer " + nextRequest.getAddress());
        final Response response = MessageListener.sendMessage(nextRequest.getRequest(), nextRequest.getAddress());
        nextRequest.decrementRetries();

        // request was not received
        if (response == null) {
            if (nextRequest.reachedMaxRetries()) {
                System.out.println("> RETRY: Reached max tries for request " + nextRequest.getRequest() + " to peer " + nextRequest.getAddress());
            }
            else {
                retryQueue.add(nextRequest);
            }
        }
    }

}