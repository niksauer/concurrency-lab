package com.niksauer.lab2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.BrokenBarrierException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, BrokenBarrierException {
        System.out.println("Lab 2");

        Exercise1 exercise1 = new Exercise1();
        Path dir = new File("./src/resources/lab2").toPath();
        exercise1.run(dir, 20000);

        Exercise2 exercise2 = new Exercise2();
        exercise2.run(new File("./src/resources/lab1/LoremText.txt"), "lorem", true, 2);

        // Exercise3 exercise3 = new Exercise3();
        // exercise3.run(Duration.ofSeconds(2), Duration.ofSeconds(5));

        Exercise5 exercise5 = new Exercise5();
        exercise5.run(2);
    }
}
