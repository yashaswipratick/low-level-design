package com.lld.phase6.patterns.creational.singleton.problem1;

public class AppConfig {

    private int maxRetryCount;
    private int timeoutSeconds;
    private String environment;


    // TODO: make this a Singleton
    // Step 1: hold the single instance in a static field
    private static volatile AppConfig INSTANCE;
    // Step 2: make the constructor private
    private AppConfig() {
        System.out.println("Loading config from file...");
        this.maxRetryCount = 3;
        this.timeoutSeconds = 30;
        this.environment = "production";
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public String getEnvironment() {
        return environment;
    }

    // Step 3: provide a static getInstance() method
    public static AppConfig getInstance() {
        if (INSTANCE == null) {
            synchronized (AppConfig.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppConfig();
                }
            }
        }
        return INSTANCE;
    }
}
