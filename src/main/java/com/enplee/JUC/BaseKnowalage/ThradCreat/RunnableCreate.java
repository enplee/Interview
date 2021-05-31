package com.enplee.JUC.BaseKnowalage.ThradCreat;

public class RunnableCreate {
    public static class RunnableCreated implements Runnable {

        @Override
        public void run() {
            System.out.println("impl runnable");
        }
    }

    public static void main(String[] args) {
        RunnableCreated runnableCreated = new RunnableCreated();
        new Thread(runnableCreated).start();
    }
}
