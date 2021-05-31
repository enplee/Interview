package com.enplee.singleton;

public class SingleTon_02 {
    private static SingleTon_02 INSTANCE = null;

    private SingleTon_02(){}

    public static SingleTon_02 getInstance(){
        if(INSTANCE == null){
            synchronized (SingleTon_02.class){
                if(INSTANCE == null){
                    INSTANCE = new SingleTon_02();
                }
            }
        }
        return INSTANCE;
    }
}
