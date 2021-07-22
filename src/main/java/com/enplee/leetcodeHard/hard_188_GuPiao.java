package com.enplee.leetcodeHard;

import java.util.Arrays;

public class hard_188_GuPiao {
    /**
     * prices股票价格 k标识可以操作次数 那么一共有 2k+1个状态: 不操作,买入和卖出
     * dp[i][j] = max(dp[i-1][j-1],dp[i-1][j]) 或者不操作,或者由前一天的状态转化过来
     * 最大的利润就是最后一天所有状态的最大值。
     */
    public int maxProfit(int k, int[] prices) {
        int[] dp = new int[k*2+1];
        Arrays.fill(dp,Integer.MIN_VALUE/2);
        dp[0] = 0;
        for(int price : prices) {
            for(int i=k*2;i>0;i--) {
                if(i%2 == 1) {
                    dp[i] = Math.max(dp[i-1]-price,dp[i]);
                }else {
                    dp[i] = Math.max(dp[i-1]+price,dp[i]);
                }
            }
        }

        int res = 0;
        for(int i : dp) res = Math.max(i,res);
        return res;
    }

    /**
     *  只允许买卖一次,状态就只有三个
     */
    public int maxProfit(int[] prices) {
        int none = 0, in = Integer.MIN_VALUE, out = Integer.MIN_VALUE;
        for(int price : prices) {
            in = Math.max(in,none - price);
            out = Math.max(out,in + price);
        }
        return Math.max(none,Math.max(in,out));
    }
    /**
     *  不限次数,实际上次数也是有限制的。n/2
     *  同时不限制次数,贪心的认为可以通过某种设计,吃掉所有的增长。
     */

    /**
     * 次数无限制,有手续费:
     * dp[i][0] = max(dp[i-1][0],dp[i-1][1]+price[i]-fee)
     * dp[i][1] = max(dp[i-1][1],dp[i-1][0]-price[i])
     * i = 0: dp[0][0] = 0,dp[0][1] = -prices[i]
     */
    public int maxProfit(int[] prices,int fee) {
        int in = -prices[0],out = 0;
        for(int price : prices) {
            in = Math.max(in,out - price);
            out = Math.max(out,in + price - fee);
        }
        return Math.max(in,out);
    }
}
