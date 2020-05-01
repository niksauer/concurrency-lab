package com.niksauer.lab1;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Exercise1 {
    public void run() throws InterruptedException {
        System.out.println("Exercise 1");

        System.out.println("Starting synchronous incrementation");
        int synchronousCount = incrementSynchronously(0, 5000);
        System.out.printf("Result: %d \n", synchronousCount);

        System.out.println("Starting threaded incrementation");
        int threadedCount = incrementWithThreads(0, 5000, 2);
        System.out.printf("Result: %d \n", threadedCount);
    }

    public int incrementSynchronously(int counter, int repeats) {
        Instant start = Instant.now();

        for (int i = 0; i < repeats; i++) {
            counter = counter + 1;
        }

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));

        return counter;
    }

    public int incrementWithThreads(int counter, int repeats, int threadCount) throws InterruptedException {
        Instant start = Instant.now();

        AtomicInteger safeCounter = new AtomicInteger(counter);
        int incrementsPerThread = repeats / threadCount;

        List<Thread> threadList = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            IncrementTask incrementTask = new IncrementTask(safeCounter, incrementsPerThread);
            threadList.add(new Thread(incrementTask));
        }

        for (Thread thread : threadList) {
            thread.start();
        }

        for (Thread thread : threadList) {
            thread.join();
        }

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));

        return safeCounter.get();
    }
}

class IncrementTask implements Runnable {
    private AtomicInteger counter;
    private int repeats;

    public IncrementTask(AtomicInteger counter, int repeats) {
        this.counter = counter;
        this.repeats = repeats;
    }

    @Override
    public void run() {
        System.out.println("Running IncrementTask Thread");

        for (int i = 0; i < this.repeats; i++) {
            this.counter.incrementAndGet();
        }

        System.out.println("Done with IncrementTask");
    }
}