package com.enplee.leetcodeHard;

import java.util.Comparator;
import java.util.PriorityQueue;

public class hard_295_heap {

}
class MedianFinder {
    public int length;
    public PriorityQueue<Integer> minQueue;
    public PriorityQueue<Integer> maxQueue;
    /** initialize your data structure here. */
    public MedianFinder() {
        minQueue = new PriorityQueue<>();
        maxQueue = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2-o1;
            }
        });
        this.length = 0;
    }

    public void addNum(int num) {
        this.length++;
        int minLength = this.length/2, maxLength = this.length-minLength;
        if(!minQueue.isEmpty() && num > minQueue.peek()){
            if(minQueue.size()==minLength){
                maxQueue.add(minQueue.poll());
            }
            minQueue.add(num);
        }else if(!maxQueue.isEmpty() && num < maxQueue.peek()){
            if(maxQueue.size()==maxLength){
                minQueue.add(maxQueue.poll());
            }
            maxQueue.add(num);
        }else {
            if(minQueue.size()<minLength){
                minQueue.add(num);
            }else {
                maxQueue.add(num);
            }
        }
        System.out.println(minQueue.toString());
        System.out.println(maxQueue.toString());
    }

    public double findMedian() {
        if(this.length%2==1) {
            return (double)maxQueue.peek();
        }
        return ((double)(maxQueue.peek()+minQueue.peek()))/2;
    }
}