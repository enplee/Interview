package com.enplee.leetcodeHard;

public class hard_664_dp {
    public int strangePrinter(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];
        for(int i=n-1;i>=0;i--){
            for(int j=i;j<n;j++){
                if(i==j) dp[i][j] = 1;
                else if(s.charAt(i)==s.charAt(j)) dp[i][j] = dp[i-1][j];
                else {
                    int min = Integer.MAX_VALUE;
                    for(int k=i;k<=j;k++){
                        min = Math.min(min,dp[i][k]+dp[k][j]);
                    }
                    dp[i][j] = min;
                }
            }
        }
        return dp[0][n-1];
    }
}
