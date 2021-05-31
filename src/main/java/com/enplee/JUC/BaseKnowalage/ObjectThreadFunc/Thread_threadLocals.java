package com.enplee.JUC.BaseKnowalage.ObjectThreadFunc;

public class Thread_threadLocals {
    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    public static void main(String[] args) {

        threadLocal.set("main_val");
        Thread t1 = new Thread(()->{
           threadLocal.set("t1 val");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            print();
        });

        Thread t2 = new Thread(()->{
            threadLocal.set("t2.val");
            print();
        });
        t1.start();
        t2.start();
        System.out.println(threadLocal.get());
    }
    public static void print(){
        System.out.println("current thread:"+Thread.currentThread().getName()+" :"+threadLocal.get());
        threadLocal.remove();
    }
}
