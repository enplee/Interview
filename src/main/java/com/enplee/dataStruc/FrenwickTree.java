package com.enplee.dataStruc;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class FrenwickTree {
    private int[] tree;
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







