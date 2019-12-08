package ru.alta.svd.client.web.main.tray;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileLocker {

    private FileLock lock;
    private FileChannel channel;

    public boolean isAppActive() {
        File file = new File(System.getProperty("user.home"),
                "soring-lock-file" + ".tmp");
        try {
            channel = new RandomAccessFile(file, "rw").getChannel();
            lock = channel.tryLock();
        } catch (Exception ignored) { }

        if (lock == null) {
            return true;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                lock.release();
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        return false;
    }

    public static boolean isHeadless(){
        boolean headless;
        String nm = System.getProperty("java.awt.headless");

        if (nm == null) {
            if (System.getProperty("javaplugin.version") != null) {
                headless = false;
            } else {
                String osName = System.getProperty("os.name");
                headless = ("Linux".equals(osName) || "SunOS".equals(osName)) &&
                        (System.getenv("DISPLAY") == null);
            }
        } else headless = nm.equals("true");
        return headless;
    }
}
