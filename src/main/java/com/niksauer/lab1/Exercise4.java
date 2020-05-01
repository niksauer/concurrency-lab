package com.niksauer.lab1;

public class Exercise4 {
    public void run() throws InterruptedException {
        System.out.println("Exercise 4");

        Thread thread = new Thread(() -> {
            int result = 10/0;
        });

        thread.setUncaughtExceptionHandler((t, e) -> System.out.printf("Uncaught thread exception: %s", e));

        thread.start();
        thread.join();
    }
}
