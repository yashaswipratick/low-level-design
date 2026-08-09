package com.lld.phase6.patterns.creational.singleton.problem3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoggerDriver {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== Proof 1: Same instance across references ===");
        Logger l1 = Logger.getInstance();
        Logger l2 = Logger.getInstance();
        System.out.println("l1 == l2: " + (l1 == l2));   // must be true
        System.out.println("'Logger initialised' must appear ONCE above");

        System.out.println("\n=== Proof 2: 20 threads all call getInstance() simultaneously ===");
        System.out.println("'Logger initialised' must still appear exactly ONCE total\n");

        ExecutorService executor = Executors.newFixedThreadPool(20);

        for (int i = 0; i < 20; i++) {
            final int threadId = i;
            executor.submit(() -> {
                Logger logger = Logger.getInstance();   // all 20 threads race here
                logger.log("Message from Thread-" + threadId);
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("\n=== Proof 3: log() is synchronized — no interleaved output ===");
        System.out.println("Each line above must be complete — no partial lines mixed together");

        System.out.println("\n=== Proof 4: hashCode confirms single instance ===");
        System.out.println("Instance 1 hashCode: " + System.identityHashCode(Logger.getInstance()));
        System.out.println("Instance 2 hashCode: " + System.identityHashCode(Logger.getInstance()));
        System.out.println("Both must be identical");
    }
}

