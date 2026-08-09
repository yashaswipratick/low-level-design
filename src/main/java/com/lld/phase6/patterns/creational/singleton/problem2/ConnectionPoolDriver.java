package com.lld.phase6.patterns.creational.singleton.problem2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConnectionPoolDriver {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== Proof 1: Same instance across references ===");
        ConnectionPool p1 = ConnectionPool.INSTANCE;
        ConnectionPool p2 = ConnectionPool.INSTANCE;
        System.out.println("p1 == p2: " + (p1 == p2));           // must be true
        System.out.println("Pool created message above must appear ONCE only");

        System.out.println("\n=== Proof 2: Shared state — borrow from p1, check via p2 ===");
        System.out.println("Available before borrow: " + p1.getSize());   // 10
        String conn = p1.getConnection();
        System.out.println("Borrowed: " + conn);
        System.out.println("Available via p2 after borrow: " + p2.getSize()); // 9 — same pool
        p1.releaseConnection(conn);
        System.out.println("After release via p1: " + p2.getSize());          // 10 — same pool

        System.out.println("\n=== Proof 3: Multi-threaded access — 10 threads each borrow 1 connection ===");
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            final int threadId = i;
            executor.submit(() -> {
                ConnectionPool pool = ConnectionPool.INSTANCE;
                String c = pool.getConnection();
                System.out.println("Thread-" + threadId + " borrowed: " + c
                        + " | remaining: " + pool.getSize());
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                pool.releaseConnection(c);
                System.out.println("Thread-" + threadId + " released: " + c
                        + " | remaining: " + pool.getSize());
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("\nFinal pool size (must be 10): " + ConnectionPool.INSTANCE.getSize());
    }
}

