package com.niksauer.lab1;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Lab 1");

        Exercise1 exercise1 = new Exercise1();
	    exercise1.run();

        Exercise3 exercise3 = new Exercise3();
        exercise3.run(new File("./src/resources/lab1/LoremText.txt"), "lorem", true);

        Exercise4 exercise4 = new Exercise4();
        exercise4.run();

        Exercise5 exercise5 = new Exercise5();
        List<URL> urls = new ArrayList<>();
        urls.add(new URL("http://example.com"));
        urls.add(new URL("https://9to5mac.com"));
        urls.add(new URL("https://www.google.com/"));
        exercise5.run(urls, new File("./src/resources/lab1/download.txt"));
    }
}
