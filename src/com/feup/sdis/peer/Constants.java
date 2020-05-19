package com.feup.sdis.peer;

public class Constants {
    public static final int RMI_PORT = 1099;
    public static final int  MEGABYTE = 1024 * 1024;
    public static final int MAX_REPL_DEGREE = 10;
    public static int  MAX_OCCUPIED_DISK_SPACE_MB = 6 * MEGABYTE; // SIZE IN MB
    public static final int BLOCK_SIZE  = 1  * MEGABYTE; // Size in MB, TODO: decide a better size
    public static String SENDER_ID = "unknown";
    public static String peerRootFolder;
    public static String backupFolder;
    public static String restoredFolder;
    public static String peerID; 
    public static final String peerParentFolder = "peers/";
    public static final String idSeparation = "#";
    public static final int REQUEST_RETRY_INTERVAL_MS = 500;
    public static final int MAX_REQUEST_RETRIES = 5;
}
