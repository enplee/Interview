package com.enplee.leetcodeHard;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class hard_85_sum_stack {
    //求01矩阵中 最大全一子矩阵的面积 解法：前缀和+单调栈
    public int maximalRectangle(char[][] matrix) {
        int n = matrix.length, m = matrix[0].length;
        int[][] dp = new int[n][m];
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                if(matrix[i][j]=='0') dp[i][j] = 0;
                else if(j==0) dp[i][j] = 1;
                else dp[i][j] = dp[i][j-1]+1;
            }
        }
        int res = 0;
        for(int i=0;i<n;i++){
            List<Integer> temp = new LinkedList<>();
            for(int j=0;j<m;j++){
                temp.add(dp[i][j]);
                System.out.println(temp.toString());
                res = Math.max(res,maxAre(temp));
            }
        }
        return res;
    }
    public int maxAre(List<Integer> nums){
        Deque<Integer> stack = new LinkedList<>();
        nums.add(0);
        nums.add(0,0);
        int res = 0;
        for(int i=0;i<nums.size();i++){
            while (!stack.isEmpty()&& nums.get(stack.peekLast()) > nums.get(i)) {
                Integer temp = stack.pollLast();
                res = Math.max(res,nums.get(temp)*(i-stack.peekLast()-1));
            }
            stack.offerLast(i);
        }
        System.out.println(res);
        return res;
    }
}
