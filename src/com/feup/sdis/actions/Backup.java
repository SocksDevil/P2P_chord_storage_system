package com.feup.sdis.actions;

import com.feup.sdis.model.BackupFileInfo;
import com.feup.sdis.model.Store;
import com.feup.sdis.peer.Constants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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

        List<Future<String>> backupCalls = new ArrayList<>();
        for (int i = 0; i < this.repDegree; i++) {
            for (int j = 0; j < this.file.getNChunks(); j++) {

                backupCalls.add(BSDispatcher.servicePool.submit(new ChunkBackup(file.getfileID(), j, i,
                        this.chunks.get(j), this.file.getNChunks(), this.repDegree, file.getOriginalFilename())));
            }
        }


        List<String> backupReturnCodes = backupCalls.stream()
                .map(backupCall -> {
                    try {
                        return backupCall.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return "Unknown error!";
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (backupReturnCodes.size() != 0) {
            Store.instance().getBackedUpFiles().remove(this.file.getfileID());
            for (int i = 0; i < this.repDegree; i++) {
                for (int j = 0; j < this.file.getNChunks(); j++) {
                    Delete.deleteChunk(j, i, file.getfileID());
                }
            }
            StringBuilder error = new StringBuilder("Failed to backup file with the following errors: \n");
            for(String returnCode: backupReturnCodes)
                error.append("\t - ").append(returnCode).append("\n");
            System.out.println(error);
            return error.toString();
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
