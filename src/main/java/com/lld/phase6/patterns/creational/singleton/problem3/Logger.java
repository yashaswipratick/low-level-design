package com.lld.phase6.patterns.creational.singleton.problem3;

public class Logger {

    private static volatile Logger INSTANCE;

    private Logger() {
        System.out.println("Logger initialised...");
    }

    //double check locking
    public static Logger getInstance(){
        if(INSTANCE==null){
            synchronized (Logger.class) {
                if(INSTANCE==null){
                    INSTANCE=new Logger();
                }
            }
        }
        return INSTANCE;
    }

    public synchronized void log(String message) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + message);
    }
}
