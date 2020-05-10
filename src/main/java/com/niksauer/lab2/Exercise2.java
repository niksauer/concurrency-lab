package com.niksauer.lab2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Exercise2 {
    class MatchTask implements Callable {
        private Matcher matcher;
        private List<String> lines;

        public MatchTask(Pattern pattern, List<String> lines) {
            this.matcher = pattern.matcher("");
            this.lines = lines;
        }

        @Override
        public Integer call() {
            int occurrences = 0;

            for (String line : lines) {
                matcher.reset(line);

                while (matcher.find()) {
                    occurrences++;
                }
            }

            return occurrences;
        }
    }

    public void run(File file, String searchString, boolean ignoreCase, int threadCount) throws IOException, InterruptedException {
        System.out.println("Exercise 2");

        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);

        List<String> lines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        int partitionSize = (int) Math.ceil(lines.size() / threadCount);

        Pattern pattern = Pattern.compile(searchString, ignoreCase ? Pattern.CASE_INSENSITIVE : null);

        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        List<Callable<Integer>> taskList = new ArrayList<>();

        for (int i = 0; i < lines.size(); i += partitionSize) {
            List<String> partition = lines.subList(i, Math.min(i + partitionSize, lines.size()));

            taskList.add(new MatchTask(pattern, partition));
        }

        List<Future<Integer>> results = pool.invokeAll(taskList);

        pool.shutdown(); // Disable new tasks from being submitted

        // Wait a while for existing tasks to terminate
        if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
            pool.shutdownNow(); // Cancel currently executing tasks

            // // Wait a while for tasks to respond to being cancelled
            // if (!pool.awaitTermination(60, TimeUnit.SECONDS))
            //    System.err.println("Pool did not terminate");
        }

        int occurrences = results.stream().map(result -> {
            try {
                return result.get();
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).reduce(0, Integer::sum);

        System.out.printf("Result: %d \n", occurrences);
    }
}

