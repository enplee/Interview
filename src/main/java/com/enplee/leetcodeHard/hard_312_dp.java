package com.enplee.leetcodeHard;

public class hard_312_dp {
    /**
     * 戳气球,戳破i位置气球获得n[i-1]*n[i]*n[i+1]的分数,剩余气球合并
     * 如果正序去戳,那么造成不连续,难以处理。 所以考虑反向操作:
     * 设(i,j)是等待戳的开区间,k是最后一个被戳破的,那么 dp(i,j)的最优戳法是在[i+1,j-1]区间内选在一个最后戳
     * 然后去处理(i,k),(k,j).
     *
     * dp[i][j] = max(n[i]*n[k]*n[j] + dp[i][k] + dp[k][j]) K in (i+1,j-1);
     */
    public int maxCoins(int[] nums) {
        int n = nums.length;
        int[] val = new int[n+2];
        val[0] = val[n+1] = 1;
        System.arraycopy(nums, 0, val, 1, n);

        int[][] dp = new int[n+2][n+2];

        for(int i = n-1;i>=0;i--) {
            for(int j= i+2;j<n+2;j++) {
                for(int k=i+1;k<j;k++) {
                    int sum = val[i]*val[k]*val[j];
                    sum  = sum + dp[i][k] + dp[k][j];
                    dp[i][j] = Math.max(sum,dp[i][j]);
                }
            }
        }
        return dp[0][n+1];
    }
}
