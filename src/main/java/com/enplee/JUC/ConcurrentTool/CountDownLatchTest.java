package com.enplee.JUC.ConcurrentTool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchTest {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch count = new CountDownLatch(3);
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        for(int i=0;i<3;i++) {
            threadPool.execute(() -> {
                System.out.println("111");
                count.countDown();
            });
        }
        count.await();
        System.out.println("finish");
        threadPool.shutdown();
    }
}
