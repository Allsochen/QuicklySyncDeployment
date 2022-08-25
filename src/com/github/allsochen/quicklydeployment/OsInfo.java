package com.github.allsochen.quicklydeployment;

public class OsInfo {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return OS.contains("win");
    }
}
