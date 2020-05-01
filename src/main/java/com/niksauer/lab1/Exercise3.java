package com.niksauer.lab1;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Exercise3 {
    public void run(File file, String searchString, boolean ignoreCase) throws IOException, InterruptedException {
        System.out.println("Exercise 3");

        System.out.printf("Starting synchronous '%s' count \n", searchString);
        int synchronousCount = countSynchronously(file, searchString, ignoreCase);
        System.out.printf("Result: %d \n", synchronousCount);

        System.out.printf("Starting threaded '%s' count\n", searchString);
        int threadedCount = countWithThreads(file, searchString, ignoreCase, 2);
        System.out.printf("Result: %d \n", threadedCount);
    }

    public int countSynchronously(File file, String searchString, boolean ignoreCase) throws IOException {
        Instant start = Instant.now();

        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);

        Pattern pattern = Pattern.compile(searchString, ignoreCase ? Pattern.CASE_INSENSITIVE : null);
        Matcher matcher = pattern.matcher("");

        int occurrences = 0;
        String line;

        while ((line = reader.readLine()) != null) {
            matcher.reset(line);

            while (matcher.find()) {
                occurrences++;
            }
        }

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));

        return occurrences;
    }

    public int countWithThreads(File file, String searchString, boolean ignoreCase, int threadCount) throws IOException, InterruptedException {
        Instant start = Instant.now();

        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);

        List<String> lines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        Pattern pattern = Pattern.compile(searchString, ignoreCase ? Pattern.CASE_INSENSITIVE : null);

        List<MatchTask> taskList = new ArrayList<>();
        List<Thread> threadList = new ArrayList<>();

        int partitionSize = (int) Math.ceil(lines.size() / threadCount);

        for (int i = 0; i < lines.size(); i += partitionSize) {
            List<String> partition = lines.subList(i, Math.min(i + partitionSize, lines.size()));
            MatchTask matchTask = new MatchTask(pattern, partition);
            taskList.add(matchTask);
            threadList.add(new Thread(matchTask));
        }

        for (Thread thread : threadList) {
            thread.start();
        }

        for (Thread thread : threadList) {
            thread.join();
        }

        int occurrences = taskList.stream().map(task -> task.getOccurrences()).reduce(0, (a, b) -> a+b);

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));

        return occurrences;
    }
}

class MatchTask implements Runnable {
    private int occurrences = 0;
    private Matcher matcher;
    private List<String> lines;

    public MatchTask(Pattern pattern, List<String> lines) {
        this.matcher = pattern.matcher("");
        this.lines = lines;
    }

    @Override
    public void run() {
        for (String line : lines) {
            matcher.reset(line);

            while (matcher.find()) {
                occurrences++;
            }
        }
    }

    public int getOccurrences() {
        return occurrences;
    }
}