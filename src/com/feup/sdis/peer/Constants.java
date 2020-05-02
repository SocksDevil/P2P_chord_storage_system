package com.feup.sdis.peer;

public class Constants {
    public static final int RMI_PORT = 1099;
    public static final int  MEGABYTE = 1024 * 1024;
    public static final int  MAX_OCCUPIED_DISK_SPACE_MB = 3 * MEGABYTE; // SIZE IN MB
    public static final int BLOCK_SIZE  = 1 * MEGABYTE; // Size in MB, TODO: decide a better size
    public static String SENDER_ID = "unknown";
    public static String SERVER_IP = "";
    public static int SERVER_PORT = -1;
    public static String peerRootFolder;
    public static String backupFolder;
    public static String restoredFolder;
    public static final String peerParentFolder = "peers/";
    public static final String idSeparation = "#";
}
