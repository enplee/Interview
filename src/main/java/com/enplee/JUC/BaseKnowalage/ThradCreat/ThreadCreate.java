package com.enplee.JUC.BaseKnowalage.ThradCreat;

/**
 *  Thread缺点：单继承，无法继承替他的类
 *  Thread/Runnable缺点：执行任务没有返回值
 */
public class ThreadCreate {
    public static class CreatedThread extends Thread {
        @Override
        public void run() {
            System.out.println(this.getName());
        }
    }

    public static void main(String[] args) {
        new CreatedThread().start();
    }
}
