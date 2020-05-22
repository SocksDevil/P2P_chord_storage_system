package com.feup.sdis.actions;

import java.io.File;
import com.feup.sdis.chord.Chord;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.MessageListener;
import com.feup.sdis.peer.Peer;

public class ShutdownHandler {

    public static void execute() {

        System.out.println("> SHUTDOWN: Terminating sequence initiated.");

        Action action = new Reclaim(0);
        System.out.println(action.process());

        // Shutdown chord (stop periodic threads)
        Chord.chordInstance.shutdown();
        System.out.println("> SHUTDOWN: Stopped chord periodic threads.");

        // Shutdown message receiving
        MessageListener.shutdown();
        Peer.messageReceiver.interrupt();
        System.out.println("> SHUTDOWN: Stopped message listener.");


        final File peerFolder = new File(Constants.peerRootFolder);
        deleteDirectory(peerFolder);
        System.out.println("> SHUTDOWN: Deleted file system storage");

    }

    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

}