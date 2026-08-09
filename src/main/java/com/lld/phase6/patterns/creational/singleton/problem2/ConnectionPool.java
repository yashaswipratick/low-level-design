package com.lld.phase6.patterns.creational.singleton.problem2;

import java.util.ArrayDeque;
import java.util.Queue;

public enum ConnectionPool {
    INSTANCE;

    private final Queue<String> queue = new ArrayDeque<>();

    ConnectionPool() {
        for (int i = 0; i < 10; i++) {
            queue.add("connection" + i);
        }
        System.out.println("Pool Created");
    }

    public String getConnection() {
        return queue.poll();
    }

    public boolean releaseConnection(String connection) {
        return queue.offer(connection);
    }

    public int getSize() {
        return queue.size();
    }
}
