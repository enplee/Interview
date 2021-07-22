package com.enplee.leetcodeHard;

import java.util.*;

public class mid_436_treeMap {
    public int[] findRightInterval(int[][] intervals) {
        int[] res = new int[intervals.length];
        TreeMap<Integer,Integer> map = new TreeMap<>();
        for(int i=0;i<intervals.length;i++) {
            map.put(intervals[i][0],i);

        }
        for(int i=0;i<intervals.length;i++) {
            Map.Entry<Integer, Integer> entry = map.ceilingEntry(intervals[i][1]);
            res[i] = entry == null ? -1 : entry.getValue();
        }
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        return res;
    }
}
