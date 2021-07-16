package com.enplee.leetcodeHard;

public class hard_115_dp {
    /**
     * s,t两个字符串,s的所有子序列中t出现的个数
     * 思路: dp-> 初始值 t为“”,n=0；
     * 如果 s[i] == t[j] dp[i][j] = dp[i-1][j-1] + dp[i-1][j];
     * 否则 dp[i][j] = dp[i-1][j]
     */
    public int numDistinct(String s, String t) {
        int n = s.length(), m = t.length();
        int[][] dp = new int[n+1][m+1];
        for(int i=0;i<=n;i++) dp[i][0] = 1; // "" -> 1

        for(int i=1;i<=n;i++) {
            for(int j=1;j<=m;j++) {
                if(s.charAt(i-1) == t.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j] + dp[i-1][j-1];
                }else {
                    dp[i][j] = dp[i-1][j];
                }
            }
        }
        return dp[n][m];
    }
}
