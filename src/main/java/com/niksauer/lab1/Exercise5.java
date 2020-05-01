package com.niksauer.lab1;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Exercise5 {
    public void run(List<URL> urls, File outputFile) throws InterruptedException, IOException {
        System.out.println("Exercise 5");
        List<DownloadTask> downloads = download(urls);
        mergeResponses(downloads, outputFile);
    }

    private List<DownloadTask> download(List<URL> urls) throws InterruptedException {
        System.out.printf("Starting threaded download of %d urls \n", urls.size());

        List<DownloadTask> downloads = urls.stream().map(url -> new DownloadTask(url)).collect(Collectors.toList());
        List<Thread> threadList = downloads.stream().map(downloadTask -> new Thread(downloadTask)).collect(Collectors.toList());

        for (Thread thread : threadList) {
            thread.start();
        }

        for (Thread thread : threadList) {
            thread.join();
        }

        System.out.printf("Completed threaded download \n", urls.size());

        return downloads;
    }

    private void mergeResponses(List<DownloadTask> downloads, File outputFile) throws IOException {
        System.out.printf("Starting synchronous merging of '%d' download results \n", downloads.size());

        if (outputFile.exists()) {
            outputFile.delete();
        }

        outputFile.createNewFile();

        List<String> responses = downloads.stream().map(downloadTask -> downloadTask.getResult()).collect(Collectors.toList());
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
            result = new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8);;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        return result;
    }
}