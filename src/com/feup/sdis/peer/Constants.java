package com.feup.sdis.peer;

public class Constants {
    public static final int RMI_PORT = 1099;
    public static final int BLOCK_SIZE  = 2; // Size in MB, TODO: decide a better size
    public static String SENDER_ID = "unknown";
    public static String SERVER_IP = "";
    public static int SERVER_PORT = -1;
    public static String peerRootFolder;
    public static String backupFolder;
    public static String restoredFolder;
    public static final String peerParentFolder = "peers/";
    public static final long  MEGABYTE = 1024L * 1024L;
    public static final long  MAX_OCCUPIED_DISK_SPACE_MB = 6 * MEGABYTE; // SIZE IN MB
}
