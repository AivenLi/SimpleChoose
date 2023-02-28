package com.aiven.acode;

public class MyAesUtil {

    static {
        System.loadLibrary("openfile-lib");
    }

    public static native boolean openFile(String path);
}
