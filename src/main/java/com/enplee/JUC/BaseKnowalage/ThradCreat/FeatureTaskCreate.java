package com.enplee.JUC.BaseKnowalage.ThradCreat;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FeatureTaskCreate {
    public static class CallerTask implements Callable<String> {

        @Override
        public String call() throws Exception {
            return "return calling results";
        }
    }

    public static void main(String[] args) {
        FutureTask<String> futureTask = new FutureTask<>(new CallerTask());
        new Thread(futureTask).start();
        try {
            String res = futureTask.get();
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
