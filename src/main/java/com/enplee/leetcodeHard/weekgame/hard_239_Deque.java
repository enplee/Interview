package com.enplee.leetcodeHard.weekgame;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class hard_239_Deque {
    public int[] maxSlidingWindow(int[] nums, int k) {
        int idx = 0;
        int[] res = new int[nums.length-k+1];
        Deque<Integer> deque = new LinkedList<>();
        for(int i=0;i<k;i++){
            while (!deque.isEmpty() && nums[deque.peekLast()]<= nums[i]){
                deque.pollLast();
            }
            deque.offerLast(i);
        }
        res[idx++] = nums[deque.peekFirst()];

        for(int i=k;i< nums.length;i++){
            while (!deque.isEmpty() && nums[deque.peekLast()]<= nums[i]){
                deque.pollLast();
            }
            while (!deque.isEmpty() && deque.peekFirst() <= i-k) {
                deque.pollFirst();
            }
            deque.offerLast(i);
            res[idx++] = nums[deque.peekFirst()];
        }
        return res;
    }
}
