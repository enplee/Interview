package com.enplee.singleton;

public class SingleTon_03 {
    private SingleTon_03(){}

    private static class InstaceHolder{
        private static final SingleTon_03 INSTANCE = new SingleTon_03();
    }

    public static SingleTon_03 getInstance(){
        return InstaceHolder.INSTANCE;
    }
}
