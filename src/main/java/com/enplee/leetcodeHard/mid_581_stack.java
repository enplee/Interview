package com.enplee.leetcodeHard;

import java.util.Deque;
import java.util.LinkedList;

public class mid_581_stack {
    /**
     * 1. 一般做法排序,以为找出一个子数组排序是整个有序,那就进可能保留两端的位置。
     * 2. 所以最终的我们需要知道的就是数组两端有多长是不需要动的,是符合有序的。
     * 3. 单调栈中由于不断添加元素,会不断有元素因为不在正确的位置被poll,找到两边的最值就是数组的范围。
     */
    public int findUnsortedSubarray(int[] nums) {
        int l = nums.length,r =-1;
        Deque<Integer> lq = new LinkedList<>();
        Deque<Integer> rq = new LinkedList<>();
        for(int i=0;i<nums.length;i++) {
            while(!lq.isEmpty() && nums[lq.peekLast()] > nums[i] ) {
                Integer top = lq.pollLast();
                l = Math.min(top,l);
            }
            lq.offerLast(i);
        }
        //System.out.println(lq.toString());
        for(int i=nums.length-1;i>=0;i--) {
            while (!rq.isEmpty() && nums[rq.peekLast()] < nums[i]) {
                Integer top = rq.pollLast();
                r = Math.max(r,top);
            }
            rq.offerLast(i);
        }
        //System.out.println(rq.toString());
        return Math.max(r-l+ 1, 0);
    }
}
