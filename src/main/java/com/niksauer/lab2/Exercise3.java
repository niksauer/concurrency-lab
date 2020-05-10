package com.niksauer.lab2;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class Exercise3 {
    CyclicBarrier barrier;

    class PrintTask implements Runnable {
        String output;

        public PrintTask(String output) {
            this.output = output;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    barrier.await();
                    System.out.println(output);
                }
            } catch (BrokenBarrierException | InterruptedException e) {
                System.out.printf("'%s' thread finished \n", output);
            }
        }
    }

    public void run(Duration printDelay, Duration timeout) throws InterruptedException, BrokenBarrierException {
        System.out.println("Exercise 3");

        barrier = new CyclicBarrier(1, () -> {
            try {
                Thread.sleep(printDelay.toMillis());
            } catch (InterruptedException e) {
                System.out.println("Failed to sleep between output");
            }
        });

        Thread javaThread = new Thread(new PrintTask("Java"));
        Thread cSharpThread = new Thread(new PrintTask("C#"));

        javaThread.start();
        cSharpThread.start();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // javaThread.interrupt();
                // cSharpThread.interrupt();
            }
        }, timeout.toMillis());

        javaThread.join();
        cSharpThread.join();

        System.out.println("Joined threads");
    }
}
