package com.enplee.JUC.ConcurrentTool;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CyclicBarrierTest {
    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        ExecutorService ec = Executors.newFixedThreadPool(10);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(4);
        for(int i=0;i<3;i++){
            ec.execute(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("111111");
            });
        }
        System.out.println("22222");
        Thread.sleep(1000);
        cyclicBarrier.await();

        ec.shutdown();
    }
}
