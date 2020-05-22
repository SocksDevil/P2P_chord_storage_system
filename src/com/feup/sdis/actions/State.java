package com.feup.sdis.actions;

import com.feup.sdis.chord.Chord;
import com.feup.sdis.model.*;
import com.feup.sdis.peer.Constants;
import com.feup.sdis.peer.Peer;

import java.util.Map;

public class State extends Action {
    @Override
    public String process() {
        final SerializableHashMap<BackupFileInfo> backedFiles = Store.instance().getBackedUpFiles();
        String message = "Peer " + Constants.SENDER_ID + "\n";
        message += "Used disk space: " + Store.instance().getUsedDiskSpace() + " bytes\n";
        message += "Backed up files: " + (backedFiles.size() == 0 ? "NONE" : backedFiles.size()) + "\n";
        for (Map.Entry<String, BackupFileInfo> entry : backedFiles.entrySet()) {
            final BackupFileInfo file = entry.getValue();
            message += "  - " + file.getfileID() + "\n"
                    + "    > " + "original filename: " + file.getOriginalFilename() + "\n"
                    + "    > " + "original path: " + file.getOriginalPath() + "\n"
                    + "    > " + "saved replication degree: " + file.getDesiredReplicationDegree() + "\n"
                    + "    > " + "number of chunks: " + file.getNChunks() + "\n";
            for (int i = 0; i < file.getNChunks(); i++) {
                String chunkID = file.getfileID() + Constants.idSeparation + i;
                message += "      # " + chunkID + "\n";
                message += "        * " + "chunk number: " + i + "\n";
                message += "        * " + "perceived replication degree: " + Store.instance().getReplCount().getSize(chunkID) + "\n";
            }
        }

        final SerializableHashMap<StoredChunkInfo> storedFiles = Store.instance().getStoredFiles();
        message += "Stored chunks: " + (storedFiles.size() == 0 ? "NONE" : storedFiles.size()) + "\n";
        for (Map.Entry<String, StoredChunkInfo> entry : storedFiles.entrySet()) {
            message += "  - " + entry.getKey() + "\n";
            message += "    > file ID: " + entry.getValue().getFileID() + "\n";
            message += "    > chunk number: " + entry.getValue().getChunkNo() + "\n";
            message += "    > chunk size (KBytes): " + entry.getValue().getChunkSize() / 1000 + "\n";
            message += "    > desired replication degree: " + entry.getValue().getDesiredReplicationDegree() + "\n";
            message += "    > saved replication degree: " + Store.instance().getReplCount().getSize(entry.getKey()) + "\n";
        }


        final ReplicationCounter reCounter = Store.instance().getReplCount();
        message += "Redirects chunks: " + (reCounter.size() == 0 ? "NONE" : reCounter.size()) + "\n";
        for (Map.Entry<String, Map<Integer, PeerInfo>> entry : reCounter.entrySet()) {
            message += "  - " + entry.getKey() + "\n";
            for(Map.Entry<Integer, PeerInfo> entry2 : entry.getValue().entrySet()){
                message += "       > rep No: " + entry2.getKey() + "\n";
                message += "       > redirect: " + (entry2.getValue().getAddress().equals(Peer.addressInfo) ? "no" : "yes")  + "\n";
                message += "       > socket address: " + entry2.getValue().getAddress() + "\n";
            }
        }

        message += Chord.chordInstance.state();

        int maxDiskSpace = Constants.MAX_OCCUPIED_DISK_SPACE_MB;
        message += "Disk space limit: " + (maxDiskSpace/1000 + " KBytes") + "\n";
        message += "Used disk space: " + Store.instance().getUsedDiskSpace()/1000 + " KBytes\n";
        return message;
    }
}
