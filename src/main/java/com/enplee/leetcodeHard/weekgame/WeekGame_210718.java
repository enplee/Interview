package com.enplee.leetcodeHard.weekgame;

import java.util.HashSet;
import java.util.Set;

public class WeekGame_210718 {
    public int canBeTypedWords(String text, String brokenLetters) {
        String[] texts = text.split(" ");
        Set<Character> set = new HashSet<>();
        for(Character c : brokenLetters.toCharArray()) set.add(c);
        int cnt = 0;
        for(String s : texts) {
            int k = 1;
            for(char c : s.toCharArray()) {
                if(set.contains(c)) {
                    k = 0;
                    break;
                }
            }
            cnt += k;
        }
        return cnt;
    }
    public int addRungs(int[] rungs, int dist) {
        int pre = 0,res = 0;
        for(int rung : rungs) {
            if(rung-pre > dist) {
                res += (rung-pre-dist-1)/dist;
            }
            pre = rung;
        }
        return res;
    }
    public long maxPoints(int[][] points) {
        long res = 0;
        if(points == null || points.length == 0) return res;
        int m = points.length, n = points[0].length;
        if(n == 0) return res;

        long[][] dp = new long[m][n];
        for(int i=0;i<n;i++) dp[0][i] = points[0][i];
        for(int i=1;i<m;i++) {
            long lMax = dp[i-1][0];
            for(int j=0;j<n;j++) {
                lMax = Math.max(lMax-1,dp[i-1][j]);
                dp[i][j] = Math.max(lMax,dp[i][j]);
            }
            long rMax = dp[i-1][n-1];
            for(int j=n-1;j>=0;j--) {
                rMax = Math.max(rMax-1,dp[i-1][j]);
                dp[i][j] = Math.max(rMax,dp[i][j]);
            }
            for(int j=0;j<n;j++) {
                dp[i][j] += points[i][j];
            }
        }
        for(int i=0;i<n;i++) res = Math.max(dp[m-1][i],res);
        return res;
    }
}
