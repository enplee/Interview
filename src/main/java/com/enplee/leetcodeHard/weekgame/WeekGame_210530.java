package com.enplee.leetcodeHard.weekgame;

import java.util.*;

public class WeekGame_210530 {
    public int[] assignTasks(int[] servers, int[] tasks) {
        if(servers.length==1){
            int[] res = new int[tasks.length];

        }
        PriorityQueue<Integer> serverHeap = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if(servers[o1]== servers[o2]) return o1-o2;
                return servers[o1]-servers[o2];
            }
        });
        
        Deque<Integer> taskQueue = new LinkedList<>();
        Map<Integer,List<Integer>> reLive = new HashMap<>();
        for(int i=0;i< servers.length;i++) serverHeap.add(i);
        int[] res = new int[tasks.length];
        for(int i=0;i< 200000;i++) {
            if(i< tasks.length) taskQueue.offerLast(i);
            if(reLive.containsKey(i)){
                serverHeap.addAll(reLive.get(i));
            }
            while (serverHeap.size() > 0 && taskQueue.size()>0) {
                int topServer = serverHeap.poll();
                int topTask  = taskQueue.pollLast();
                res[topTask] = topServer;
                int limitTime = i+tasks[topTask];
                if(!reLive.containsKey(limitTime)) reLive.put(limitTime,new LinkedList<>());
                reLive.get(limitTime).add(topServer);
            }
        }
        return res;
    }
}
