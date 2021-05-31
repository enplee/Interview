package com.enplee.JUC.AdvancedApp;

import java.util.concurrent.CopyOnWriteArrayList;

public class copyOnwriteList {
    public static void main(String[] args) {
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        list.add(1);
        list.set(1,2);
        list.remove(2);
        list.get(1);
        list.iterator();
    }
}

