package com.enplee.singleton;

public class SingleTon_01 {
    private final static SingleTon_01 INSTANCE = new SingleTon_01();

    private SingleTon_01(){
    }

    public static SingleTon_01 getInstance(){
        return INSTANCE;
    }
}
