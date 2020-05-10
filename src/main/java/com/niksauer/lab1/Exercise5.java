package com.niksauer.lab1;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Exercise5 {
    public void run(List<URL> urls, File outputFile) throws InterruptedException, IOException {
        System.out.println("Exercise 5");

        System.out.println("Starting synchronous download");
        downloadSynchronously(urls, outputFile);

        System.out.println("Starting threaded download");
        downloadWithThreads(urls, outputFile);
    }

    private void downloadSynchronously(List<URL> urls, File outputFile) throws IOException {
        Instant start = Instant.now();

        List<String> responses = urls.stream().map(url -> {
            try {
                return new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        mergeResponses(responses, outputFile);

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));
    }

    private void downloadWithThreads(List<URL> urls, File outputFile) throws InterruptedException, IOException {
        Instant start = Instant.now();

        List<DownloadTask> downloads = download(urls);
        List<String> responses = downloads.stream().map(DownloadTask::getResult).collect(Collectors.toList());
        mergeResponses(responses, outputFile);

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));
    }

    private List<DownloadTask> download(List<URL> urls) throws InterruptedException {
        System.out.printf("Starting threaded download of %d urls \n", urls.size());

        List<DownloadTask> downloads = urls.stream().map(DownloadTask::new).collect(Collectors.toList());
        List<Thread> threadList = downloads.stream().map(Thread::new).collect(Collectors.toList());

        for (Thread thread : threadList) {
            thread.start();
        }

        for (Thread thread : threadList) {
            thread.join();
        }

        System.out.println("Completed threaded download");

        return downloads;
    }

    private void mergeResponses(List<String> responses, File outputFile) throws IOException {
        System.out.printf("Starting synchronous merging of %d download results \n", responses.size());

        if (outputFile.exists()) {
            outputFile.delete();
        }

        outputFile.createNewFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

        for (String response : responses) {
            writer.write(response);
        }

        System.out.println("Completed synchronous merging");

        writer.close();
    }
}

class DownloadTask implements Runnable {
    private URL url;
    private String result;

    public DownloadTask(URL url) {
        this.url = url;
    }

    @Override
    public void run() {
        try {
            result = new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        return result;
    }
}