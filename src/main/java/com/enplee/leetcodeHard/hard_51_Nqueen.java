package com.enplee.leetcodeHard;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class hard_51_Nqueen {
    List<List<String>> res;
    public List<List<String>> solveNQueens(int n) {
        res = new LinkedList<>();
        dfs(n,new LinkedList<>());
        return res;
    }

    public void dfs(int n,List<Integer> path) {
        if(path.size()==n) {
            System.out.println(path.toString());
            drawRes(path);
            return;
        }
        for(int i=0;i<n;i++){
            if(check(path,i)) {
                path.add(i);
                dfs(n,path);
                path.remove(path.size()-1);
            }
        }
    }

    public boolean check(List<Integer> path, int k) {
        if(path.contains(k)) return false;
        int n = path.size();
        for(int i=0;i<n;i++){
            if(Math.abs(((float)(k-path.get(i)))/(n-i))==1) return false;
        }
        return true;
    }

    public void drawRes(List<Integer> path) {
        int n = path.size();
        List<String> solution = new LinkedList<>();
        for (Integer integer : path) {
            char[] temp = new char[n];
            for (int j = 0; j < n; j++) {
                temp[j] = '.';
            }
            temp[integer] = 'Q';
            solution.add(String.valueOf(temp));
        }
        res.add(solution);
    }
}
