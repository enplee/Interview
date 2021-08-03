package com.enplee.leetcodeHard.weekgame;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class WeekGame_210627 {
    public int maxProductDifference(int[] nums) {
        int maxP = Integer.MIN_VALUE, minP = Integer.MAX_VALUE;
        for(int i=0;i< nums.length;i++){
            for(int j=i+1;j<nums.length;j++){
                int mul = nums[i]* nums[j];
                maxP = Math.max(maxP,mul);
                minP = Math.min(minP,mul);
            }
        }
        return maxP-minP;
    }
    public int[][] rotateGrid(int[][] grid, int k) {
        dfs(grid,0,0, grid.length-1, grid[0].length-1,k);
        return grid;
    }
    public void dfs(int[][] grid, int x1,int y1,int x2,int y2,int k) {
        if(x1>x2 || y1>y2) return;
        int len = (x2-x1+y2-y1)*2;
        int[] temp = new int[len];
        int i = x1,j = y1,idx = 0;
        for(int t=y1;t<y2;t++){
            temp[idx++] = grid[i][j];
            j++;
        }
        for(int t=x1;t<x2;t++) {
            temp[idx++] = grid[i][j];
            i++;
        }
        for(int t=y2;t>y1;t--){
            temp[idx++] = grid[i][j];
            j--;
        }
        for(int t=x2;t>x1;t--){
            temp[idx++] = grid[i][j];
            i--;
        }
        int trueK = k%len;
        reversePart(temp,0,trueK-1);
        reversePart(temp,trueK,temp.length-1);
        reversePart(temp,0,temp.length-1);
        for(int t=y1;t<y2;t++){
            grid[i][j] = temp[idx++];
            j++;
        }
        for(int t=x1;t<x2;t++) {
            grid[i][j] = temp[idx++];
            i++;
        }
        for(int t=y2;t>y1;t--){
            grid[i][j] = temp[idx++];
            j--;
        }
        for(int t=x2;t>x1;t--){
            grid[i][j] = temp[idx++];
            i--;
        }
        dfs(grid,x1+1,y1+1,x2-1,y2-1,k);
    }
    public void reversePart(int[] arr,int i,int j) {
        while(i<j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
            i++;
            j--;
        }
    }
    long[] fullSort;
    int MOD = 1000000000+7;
    public int waysToBuildRooms(int[] prevRoom) {

        List<Integer>[] graf = new List[prevRoom.length];
        for(int i=0;i< prevRoom.length;i++) graf[i] = new LinkedList<>();
        for(int i=1;i<prevRoom.length;i++){
            graf[prevRoom[i]].add(i);
        }
        System.out.println(Arrays.toString(graf));
        int maxL = 0;
        for(int i=1;i< prevRoom.length;i++){
            maxL = Math.max(maxL,graf[i].size());
        }

        fullSort = new long[maxL+1];
        fullSort[1] = 1;
        for(int i=2;i<=maxL;i++){
            fullSort[i] = i*fullSort[i-1];
        }
        return dfs(graf,0);
    }
    public int dfs(List<Integer>[] grap,int i) {
        long res = 1;
        if(grap[i].size()==0) return (int)res;
        for(int child : grap[i]) {
            res = (res*(dfs(grap,child)+1))%MOD;
        }
        int k = grap[i].size();
        return (int)(mpow(res, fullSort[k],MOD)%MOD);
    }
    public long mpow(long a,long b,long m) {
        long t = 1;
        while(b>0) {
            if((b&1)==1) {
                t = (t*a)%m;
            }
            a = (a*a)%m;
            b >>= 1;
        }
        return t;
    }

    public long wonderfulSubstrings(String word) {
        return 1;
    }
}
