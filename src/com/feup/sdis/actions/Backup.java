package com.feup.sdis.actions;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.BackupMessage;
import com.feup.sdis.messages.LookupMessage;
import com.feup.sdis.messages.Message;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.Peer;
import static com.feup.sdis.peer.Constants.BLOCK_SIZE;
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

    private String filepath;
    private int repDegree;
    private ArrayList<byte[]> chunks;
    private File file;
    private String fileID;
    private int nChunks;

    public Backup(String[] args) {

        this.filepath = args[1];
        this.repDegree = Integer.parseInt(args[2]);
        try {
            this.readFile();
        } catch (InvalidAttributeValueException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public String process() {
        // -- TODO: This will change because of Chord
        LookupMessage lookupRequest = new LookupMessage(this.fileID + '#' + this.nChunks, this.repDegree,
                Peer.addressInfo);
        Message lookupMessageAnswer = this.sendMessage(lookupRequest,
                new SocketAddress(Constants.SERVER_IP, Constants.SERVER_PORT));
        // --

        // TODO: execute for each chunk
        BackupMessage backupRequest = new BackupMessage(this.fileID, this.nChunks, this.repDegree, this.chunks.get(0),lookupMessageAnswer.getConnection());
        Message backupMessageAnswer = this.sendMessage(backupRequest, backupRequest.getConnection());

        return "Backed up file";
    }

    public void readFile() throws InvalidAttributeValueException {

        if (filepath == null)
            throw new InvalidAttributeValueException("Filepath can't be null");
        if (repDegree < 1)
            throw new InvalidAttributeValueException("Replication Degree must be at least 1");

        this.file = new File(filepath);

        if (!this.file.exists()) {
            throw new InvalidAttributeValueException("File does not exist");
        }

        // Get file info
        Path path = Paths.get(filepath);
        FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(path, FileOwnerAttributeView.class);
        UserPrincipal owner = null;
        String ownerName = "default";

        try {
            owner = ownerAttributeView.getOwner();
            ownerName = owner.getName();
        } catch (IOException e1) {

            e1.printStackTrace();
        }

        // Generate UUID for file
        String metaFileName = this.file.getName() + String.valueOf(this.file.lastModified()) + ownerName;
        this.fileID = UUID.nameUUIDFromBytes(metaFileName.getBytes()).toString();
        this.chunks = new ArrayList<>();


        try {
            this.splitChunks();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            return;
        }
        

    }

    // TODO: Este split está errado pq está com os tamanhos hardcoded
    public void splitChunks() throws IOException {
        byte[] fileData = Files.readAllBytes(file.toPath());
        this.nChunks = (int) (Math.floor(this.file.length() / 64000) + 1);
        for (int i = 0; i < this.nChunks; i++) {
            byte[] chunk;

            if (i == this.nChunks - 1)
                chunk = Arrays.copyOfRange(fileData, i * 64000 , fileData.length);
            else
                chunk = Arrays.copyOfRange(fileData, i * 64000 , (i + 1) * 64000 );

            this.chunks.add(chunk);
        }

    }

}
