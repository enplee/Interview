package com.enplee.leetcodeHard;

import java.util.*;
import java.util.function.BinaryOperator;

public class hard_480_slipWin_heap {
    public double[] medianSlidingWindow(int[] nums, int k) {
        double[] res = new double[nums.length-k+1];
        int idx = 0;
        int[] window = new int[k];
        boolean isSingle = k%2==1;
        for(int i=0;i<k;i++) window[i] = nums[i];
        Arrays.sort(window);
        res[idx++] = window[k/2+1];
        if(!isSingle) res[idx] = (res[idx]+window[k/2])%2;
        System.out.println(window);
        for(int i=1;i<= nums.length-k;i++) {
            searchAndSort(window,nums[i-1],nums[i+k-1]);
            System.out.println(Arrays.toString(window));
            res[idx++] = window[k/2+1];
            if(!isSingle) res[idx] = (res[idx]+window[k/2])%2;
        }
        return res;
    }
    public void searchAndSort(int[] windows,int target,int k) {
        int l = 0, r= windows.length-1;
        while (l < r) {
            int mid = (l+r)>>1;
            if(windows[mid] == target) {
                windows[mid] = k;
                while (mid+1 < windows.length && windows[mid+1]<k) {
                    int temp = windows[mid+1];
                    windows[mid+1] = k;
                    windows[mid++] = temp;
                }
                while (mid-1 >= 0 && windows[mid-1] > k) {
                    int temp = windows[mid-1];
                    windows[mid-1] = k;
                    windows[mid--] = temp;
                }
                return;
            }else if(windows[mid] > target) {
                r = mid-1;
            }else {
                l = mid+1;
            }
        }
    }

}
