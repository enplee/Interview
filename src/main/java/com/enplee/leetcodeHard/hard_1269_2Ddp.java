package com.enplee.leetcodeHard;

public class hard_1269_2Ddp {
    // dp[i][j] = dp[i-1][j] dp[i-1][j-1] dp[i-1][j+1]
    int MOD = 1000000000+7;
    public int numWays(int steps, int arrLen) {
        int maxColumn = Math.min(arrLen-1,steps);
        int[][] dp = new int[steps+1][maxColumn+1];
        dp[0][0] = 1;
        for(int i=1;i<=steps;i++){
            for(int j=0;j<=maxColumn;j++){
                dp[i][j] = dp[i-1][j];
                if(j-1 >= 0){
                    dp[i][j] = (dp[i][j]+dp[i-1][j-1])%MOD;
                }
                if(j+1 <= maxColumn) {
                    dp[i][j] = (dp[i][j]+dp[i-1][j+1])%MOD;
                }
            }
        }
        return dp[steps][0];
    }
}
