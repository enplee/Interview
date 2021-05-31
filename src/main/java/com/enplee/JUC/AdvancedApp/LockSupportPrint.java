package com.enplee.JUC.AdvancedApp;

import javax.print.DocFlavor;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.LockSupport;

public class LockSupportPrint {
    static Thread t1 = null, t2 = null;
    public static void main(String[] args) throws InterruptedException {
        String s1 = "123456789";
        String s2 = "abcdefghi";
        t1= new Thread(()->{
            for(char s : s1.toCharArray()) {
                System.out.println(s);
                LockSupport.unpark(t2);
                LockSupport.park();
            }
        },"t1");
        t2 = new Thread(()-> {
            for(char s : s2.toCharArray()) {
                LockSupport.park();
                System.out.println(s);
                LockSupport.unpark(t1);
            }
        },"t2");

        t2.start();
        t1.start();
    }
}
