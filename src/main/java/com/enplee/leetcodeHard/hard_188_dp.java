package com.enplee.leetcodeHard;

import java.util.Arrays;

public class hard_188_dp {
    public int maxProfit(int k, int[] prices) {
        int[] dp = new int[k*2+1];
        Arrays.fill(dp,Integer.MIN_VALUE);
        dp[0] = 0;
        for(int price : prices) {
            for(int j=1;j<k*2+1;j++) {
                if(j%2==1) {
                    dp[j] = Math.max(dp[j],dp[j-1]-price);
                }else {
                    dp[j] = Math.max(dp[j],dp[j-1]+price);
                }
            }
        }
        int res = 0;
        for(int i:dp) res = Math.max(res,i);
        return res;
    }
}
