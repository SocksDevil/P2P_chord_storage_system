package com.feup.sdis.actions;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.BackupMessage;
import com.feup.sdis.messages.LookupMessage;
import com.feup.sdis.messages.Message;
import com.feup.sdis.model.BackupFileInfo;
import com.feup.sdis.model.Store;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.Peer;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import javax.naming.directory.InvalidAttributeValueException;

public class Backup extends Action {

    private BackupFileInfo file;
    private String filepath;
    private int repDegree;
    private ArrayList<byte[]> chunks;

    public Backup(String[] args) {

        this.filepath = args[1];
        this.repDegree = Integer.parseInt(args[2]);
        this.chunks = new ArrayList<>();

    }

    @Override
    public String process() {

        try {
            this.file = this.readFile();
            Store.instance().getBackedUpFiles().put(this.file.getfileID(), this.file);

        } catch (InvalidAttributeValueException | IOException e) {
            return e.getMessage();
        }

        for (int i = 0; i < this.file.getNChunks(); i++) {

            final int chunkNo = i;
            Runnable task = () -> {
                // -- TODO: This will change because of Chord
                LookupMessage lookupRequest = new LookupMessage(this.file.getfileID(), chunkNo, this.repDegree,
                        Peer.addressInfo);
                Message lookupMessageAnswer = this.sendMessage(lookupRequest,
                        new SocketAddress(Constants.SERVER_IP, Constants.SERVER_PORT));
                // --
                BackupMessage backupRequest = new BackupMessage(this.file.getfileID(), chunkNo, this.repDegree,
                        this.chunks.get(chunkNo), lookupMessageAnswer.getConnection());
                Message backupMessageAnswer = this.sendMessage(backupRequest, backupRequest.getConnection());
            };

            BSDispatcher.servicePool.execute(task);

        }

        return "Backed up file";
    }

    public BackupFileInfo readFile() throws InvalidAttributeValueException, IOException {

        if (filepath == null)
            throw new InvalidAttributeValueException("Filepath can't be null");
        if (repDegree < 1)
            throw new InvalidAttributeValueException("Replication Degree must be at least 1");

        File file = new File(filepath);

        if (!file.exists()) {
            throw new InvalidAttributeValueException("File does not exist");
        }

        // Get file info
        Path path = Paths.get(filepath);

        // Get file owner
        String ownerName = "default";

        try {
            FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(path, FileOwnerAttributeView.class);
            UserPrincipal owner = ownerAttributeView.getOwner();
            ownerName = owner.getName();
        } catch (IOException e1) {

            e1.printStackTrace();
        }

        // Generate UUID for file: filename + lastmodified + ownername TODO: change
        // this, maybe include file hash
        String metaFileName = file.getName() + String.valueOf(file.lastModified()) + ownerName;
        String fileID = UUID.nameUUIDFromBytes(metaFileName.getBytes()).toString();

        // Split chunks
        int nChunks = this.splitChunks(file);

        return new BackupFileInfo(fileID, file.getName(), filepath, nChunks, repDegree);
    }

    public int splitChunks(File file) throws IOException {

        byte[] fileData = Files.readAllBytes(file.toPath());
        int nChunks = (int) Math.ceil(((double) file.length()) / Constants.BLOCK_SIZE);

        for (int i = 0; i < nChunks; i++) {
            byte[] chunk;

            if (i == nChunks - 1)
                chunk = Arrays.copyOfRange(fileData, i * Constants.BLOCK_SIZE, fileData.length);
            else
                chunk = Arrays.copyOfRange(fileData, i * Constants.BLOCK_SIZE, (i + 1) * Constants.BLOCK_SIZE);

            this.chunks.add(chunk);
        }

        return nChunks;
    }

}
