package com.niksauer.lab2;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Timer;
import java.util.TimerTask;

public class Exercise1 {
    boolean stopMonitor = false;

    public void run(Path monitorPath, long timeout) throws IOException {
        System.out.println("Exercise 1");

        WatchService watcher = FileSystems.getDefault().newWatchService();
        WatchKey watchKey = monitorPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

        System.out.printf("Monitoring path %s for %d seconds...\n", monitorPath.toString(), timeout/1000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                stopMonitor = true;
            }
        }, timeout);

        while (true) {
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // only registered for ENTRY_MODIFY, but OVERFLOW can always occur if events are lost or discarded
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                Path filename = pathEvent.context();
                Path child = monitorPath.resolve(filename);
                long byteSize = child.toFile().length();

                System.out.printf("%s \t %s\n", filename, FileUtils.byteCountToDisplaySize(byteSize));
            }

            boolean stillValid = watchKey.reset();

            if (!stillValid) {
                System.out.println("Stopping monitoring because watchKey is invalid");
                break;
            }

            if (stopMonitor) {
                System.out.println("Stopping monitoring because of timeout");
                break;
            }
        }
    }
}
