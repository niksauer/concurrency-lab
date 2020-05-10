package com.niksauer.lab2;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Exercise5 {
    CountDownLatch latch = new CountDownLatch(1);

    class RandomTask implements Runnable {
        int min;
        int max;

        public RandomTask(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public void run() {
            try {
                latch.await();
                System.out.println(ThreadLocalRandom.current().nextInt(min, max+1));
            } catch (InterruptedException e) {
                System.out.println("Failed to wait for latch");
            }
        }
    }

    public void run(int threadCount) throws InterruptedException {
        System.out.println("Exercise 5");

        List<Thread> threadList = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            int min = ThreadLocalRandom.current().nextInt(0, 51);
            int max = ThreadLocalRandom.current().nextInt(51, 101);
            RandomTask randomTask = new RandomTask(min, max);
            threadList.add(new Thread(randomTask));
        }

        threadList.add(new Thread(() -> {
            long waitSeconds = ThreadLocalRandom.current().nextInt(3, 8);

            System.out.printf("Signaling threads in %d seconds\n", waitSeconds);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    latch.countDown();
                    System.out.println("Signal sent");
                }
            }, waitSeconds * 1000);
        }));

        for (Thread thread : threadList) {
            thread.start();
        }

        for (Thread thread : threadList) {
            thread.join();
        }

        // System.out.println(threadList.stream().map(thread -> thread.getState()).collect(Collectors.toList()));
    }
}
