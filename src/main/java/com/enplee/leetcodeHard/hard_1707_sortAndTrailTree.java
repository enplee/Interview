package com.enplee.leetcodeHard;

import java.util.*;

public class hard_1707_sortAndTrailTree {
    public int[] maximizeXor(int[] nums, int[][] queries) {
        // 排序+01字典树
        Arrays.sort(nums);
        Integer[] qIdx = new Integer[queries.length];
        for(int i=0;i< queries.length;i++) qIdx[i] = i;
        Arrays.sort(qIdx, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return queries[o1][1]- queries[o2][1];
            }
        });
        tTree tree = new tTree();
        int[] res = new int[queries.length];
        int k = 0;
        for(int idx : qIdx) {
            while (k< nums.length && nums[k] <= queries[idx][1]) {
                tree.addNum(nums[k++]);
            }
            if(k==0) res[idx] = -1;
            else res[idx] = getMaxXOR(tree, queries[idx][0]);
        }
        return res;
    }
    public int getMaxXOR(tTree tree,int num) {
        int temp = 0;
        tNode point = tree.root;
        for(int i=31;i>=0;i--) {
            int bit = (num&(1<<i))==0 ? 1:0;
            if(point.child[bit]!=null) {
                temp += (1<<i);
                point = point.child[bit];
            }else {
                if(bit==0) point = point.child[1];
                else point = point.child[0];
            }
        }
        return temp;
    }
}
class tTree {
    tNode root;

    public tTree() {
        root = new tNode();
    }
    public void addNum(int num) {
        tNode point = root;
        for(int i=31;i>=0;i--) {
            int idx = (num&(1<<i))==0 ? 0:1;
            if(point.child[idx]==null) point.child[idx] = new tNode();
            point = point.child[idx];
        }
    }
}
class tNode {
    tNode[] child;

    public tNode() {
        child = new tNode[2];
    }
}

