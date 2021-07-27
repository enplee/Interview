package com.enplee.leetcodeHard;

import java.util.TreeSet;

public class hard_363_treeMap {
    public int maxSumSubmatrix(int[][] matrix, int k) {
        int m = matrix.length, n = matrix[0].length;
        int[][] dp = new int[m+1][n+1];
        for(int i=1;i<=m;i++) {
            for(int j=1;j<=n;j++) {
                dp[i][j] = dp[i - 1][j] + dp[i][j - 1] - dp[i - 1][j - 1] + matrix[i - 1][j - 1];
            }
        }
        int res = Integer.MIN_VALUE;
        for(int h = 1;h<=m;h++){
            for(int l=h;l<=m;l++) {
                TreeSet<Integer> set = new TreeSet<>();
                for(int r=0;r<=n;r++) {
                    // val - sum <= k --> val - k <= sum
                    int val = dp[l][r] - dp[h-1][r];
                    Integer ceiling = set.ceiling(val - k);
                    if(ceiling != null) {
                        res = Math.max(res,val-ceiling);
                    }
                    set.add(val);
                }
            }
        }
        return res;
    }
}
