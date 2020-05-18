package com.feup.sdis.peer;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.model.SerializableHashMap;
import com.feup.sdis.model.Store;
import com.feup.sdis.model.StoredChunkInfo;

public class ShutdownHandler extends Thread{

    @Override
    public void run() {
        System.out.println("> Terminating sequence intiated.");
        Chord.chordInstance.shutdown();
        System.out.println("> Stopped chord periodic threads...");
        Peer.messageReceiver.interrupt();
        System.out.println("> Stopped message listener");
        SerializableHashMap<StoredChunkInfo> chunks = Store.instance().getStoredFiles();
        System.out.println("> Retreived stored chunks ("+ chunks.size()+ " chunks)");
    }

    
}