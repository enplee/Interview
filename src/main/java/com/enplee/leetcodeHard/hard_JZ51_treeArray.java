package com.enplee.leetcodeHard;

import javax.swing.plaf.IconUIResource;
import java.util.*;

public class hard_JZ51_treeArray {
    public int reversePairs(int[] nums) {
        TreeSet<Integer> set = new TreeSet<>();
        for(int num : nums) set.add(num);
        Map<Integer,Integer> map = new HashMap<>();
        int idx = 1;
        for(int num:nums) {
            map.put(num,idx);
            idx++;
        }
        int res = 0;
        FrenwickTree tree = new FrenwickTree(set.size());
        for(int i= nums.length-1;i>=0;i--){
            int rank = map.get(nums[i]);
            tree.update(rank,1);
            res += tree.query(rank-1);
        }

        return res;
    }
}
class FrenwickTree {
    public int[] tree;
    private int len;

    public FrenwickTree(int len) {
        this.tree = new int[len+1];
        this.len = len;
    }
    public int lowBit(int n) {
        return n & (-n);
    }
    public void update(int pos,int val){
        while (pos<=this.len) {
            tree[pos] += val;
            pos += lowBit(pos);
        }
    }
    public int query(int pos) {
        int sum = 0;
        while (pos>0) {
            sum += tree[pos];
            pos -= lowBit(pos);
        }
        return sum;
    }
}