package com.enplee.leetcodeHard;

import java.util.*;

public class hard_329_topoDP {
    /**
     *  递增序列抽象成有向图：if : maxtrix[i][j] > maxtrix[u][v] :  [u][v] ------->>>> [i][j] 存在有向边
     *  严格递增序列一定保证无环，可以获得通过拓扑排序获得有向序列，之后线性DP。
     */
    public int longestIncreasingPath(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[] dx = new int[]{1,-1,0,0};
        int[] dy = new int[]{0,0,1,-1};
        int[][] inDegree = new int[m][n];
        Deque<Integer> queue = new LinkedList<>();
        List<Integer> topo = new LinkedList<>();
        // topo
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                for(int k=0;k<4;k++){
                    int x = i+dx[k], y = j+dy[k];
                    if (x>=0 && x<m && y>=0 && y<n && matrix[i][j] > matrix[x][y]) inDegree[i][j]++;
                }
            }
        }
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                if(inDegree[i][j]==0) queue.offerLast(i*n+j);
            }
        }

        while (!queue.isEmpty()) {
            Integer top = queue.pollFirst();
            topo.add(top);
            int i = top/n, j = top%n;
            for(int k=0;k<4;k++){
                int x = i+dx[k], y = j+dy[k];
                if(x>=0 && x<m && y>=0 && y<n && matrix[x][y] > matrix[i][j]) {
                    inDegree[x][y]--;
                    if(inDegree[x][y] == 0) queue.offerLast(x*n+y);
                }

            }
        }
        // dp
        int res = 0;
        int[] dp = new int[n*m];
        //Arrays.fill(dp,1);

        for(int i=topo.size()-1;i>=0;i--) {
            int point = topo.get(i);
            int u = point/n, v = point%n;
            for(int k=0;k<4;k++){
                int x = u+dx[k], y = v+dy[k];
                if(x>=0 && x<m && y>=0 && y<n && matrix[u][v] < matrix[x][y]) dp[point] = Math.max(dp[point],dp[x*n+y]);
            }
            dp[point]+=1;
            res = Math.max(res,dp[point]);
        }
        return res;
    }
}
