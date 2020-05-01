package com.feup.sdis.actions;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;

import static com.feup.sdis.peer.Constants.BLOCK_SIZE;

public class Backup extends Action{
    private final String message;

    public Backup(String[] args) {
        this.message = String.join("", args);
    }

    @Override
    public String process() {
        //TODO: Implement backup
        this.sendMessage(message);
        return "Backed up file";
    }

    public static void sendPutChunks(String fileId, byte[] fileContent, int replDeg) {
        final double division = fileContent.length / (double) BLOCK_SIZE;
        final int numChunks = (int) Math.ceil(division);
        for (int i = 0; i < numChunks; i++) {
            final byte[] chunk = Arrays.copyOfRange(fileContent, BLOCK_SIZE * i, Math.min(BLOCK_SIZE * (i + 1), fileContent.length));
        }
    }
}
