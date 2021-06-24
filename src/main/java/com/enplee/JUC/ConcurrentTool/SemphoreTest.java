package com.enplee.JUC.ConcurrentTool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemphoreTest {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3);
        ExecutorService ec = Executors.newFixedThreadPool(5);
        for(int i=0;i<4;i++) {
            ec.execute(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName());
                    Thread.sleep(1000);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        System.out.println("finish");
        ec.shutdown();
    }
}
