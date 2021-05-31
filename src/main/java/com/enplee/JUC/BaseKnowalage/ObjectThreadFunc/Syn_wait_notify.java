package com.enplee.JUC.BaseKnowalage.ObjectThreadFunc;

public class Syn_wait_notify {

    public static void main(String[] args) {
        Object syn = new Object();
        Thread thread1 = new Thread(()->{
            synchronized(syn){
                for(int i=0;i<26;i++){
                    System.out.println(i);
                    try {
                        syn.notify();
                        syn.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                syn.notify();
            }
        });
        Thread thread2 = new Thread(()->{
           synchronized (syn) {
               for(int i=0;i<26;i++){
                   System.out.println((char)(i+'a'));
                   try {
                       syn.notify();
                       syn.wait();
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
               syn.notify();
           }
        });
        thread1.start();
        thread2.start();
    }
}
