package com.enplee.leetcodeHard;

import java.util.*;

public class hard_315_treeArray {
    public List<Integer> countSmaller(int[] nums) {
        TreeSet<Integer> set = new TreeSet();
        Map<Integer,Integer> map = new HashMap();
        int len = nums.length;

        for(int num:nums) set.add(num);
        int idx = 1;
        for(int num:set) {
            map.put(num,idx);
            idx++;
        }

        Fenwike tree = new Fenwike(set.size());
        Integer[] res = new Integer[len];
        for(int i=len-1;i>=0;i--) {
            int rank = map.get(nums[i]);
            tree.update(rank,1);
            res[i] = tree.quary(rank-1);
        }

        return Arrays.asList(res);
    }
}
class Fenwike {
    int[] tree;
    int len;
    public Fenwike(int n) {
        this.len = n;
        tree = new int[n+1];
    }
    public int lowBit(int i) {
        return i&(-i);
    }
    public void update(int i,int val){
        while(i<=this.len){
            tree[i] += val;
            i += lowBit(i);
        }
    }
    public int quary(int i) {
        int sum = 0;
        while(i>0) {
            sum += tree[i];
            i -= lowBit(i);
        }
        return sum;
    }
}