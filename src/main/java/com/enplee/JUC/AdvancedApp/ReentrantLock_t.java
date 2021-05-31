package com.enplee.JUC.AdvancedApp;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantLock_t {
    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        Lock lock1 = new ReentrantReadWriteLock().readLock();
        List<Integer> list = new LinkedList<>();
    }
}
