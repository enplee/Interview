package com.enplee.JUC.BaseKnowalage.ObjectThreadFunc;

public class thread_join {

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(()->{
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        System.out.println("thread1 start");

        thread1.join();
        System.out.println("thread1 finish");
    }
}
