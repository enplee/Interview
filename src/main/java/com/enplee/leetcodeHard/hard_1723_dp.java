package com.enplee.leetcodeHard;

public class hard_1723_dp {
    // 状态压缩dp的案例
    public int minimumTimeRequired(int[] jobs, int k) {
        int n = jobs.length;
        int m = 1<<n;
        int[][] dp = new int[k][m];
        int[] sum = new int[m];

        for(int i=1;i<m;i++){
            int j = Integer.numberOfTrailingZeros(i), x = i - (1<<j);
            sum[i] = sum[x] + jobs[j];
            dp[0][1] = sum[i];
        }

        for(int i=1;i<k;i++){
            for(int j=1;j<m;j++){
                int maxn = Integer.MAX_VALUE;
                for(int t=k;t!=0;t=(t-1)&j) {
                    maxn = Math.min(maxn,Math.max(dp[i-1][j-t],sum[t]));
                }
                dp[i][j] = maxn;
            }
        }
        return dp[k-1][m-1];
    }
}
