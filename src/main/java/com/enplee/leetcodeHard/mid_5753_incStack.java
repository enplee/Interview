package com.enplee.leetcodeHard;

import javax.crypto.spec.PSource;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

public class mid_5753_incStack {
    Double MOD = 1e9 + 7;
    public int maxSumMinProduct(int[] nums) {
        int n = nums.length;
        int[] sum = new int[n+1];
        for(int i=1;i<=n;i++) {
            sum[i] = sum[i-1]+ nums[i-1];
        }

        Deque<Integer> stack = new LinkedList<>();
        int[] l = new int[n],r = new int[n];
        Arrays.fill(r,n-1);

        for(int i=0;i<n;i++){
            while (!stack.isEmpty() && nums[stack.peekLast()] < nums[i]) {
                Integer top = stack.pop();
                r[top] = i-1;
            }
            stack.push(i);
        }

        for(int i=n-1;i>=0;i--){
            while (!stack.isEmpty() && nums[stack.peekLast()] < nums[i]) {
                Integer top = stack.pop();
                l[top] = i+1;
            }
            stack.push(i);
        }

        long res = 0;
        for(int i=0;i<n;i++) {
            res = Math.max(res,nums[i]*(sum[r[i+1]]-sum[l[i]]));
        }
        return (int) (res%MOD);

    }
}
