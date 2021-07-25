package com.enplee.leetcodeHard.weekgame;

import java.util.LinkedList;
import java.util.List;

public class WeekGame_210725 {
    public int getLucky(String s, int k) {
        StringBuilder sb = new StringBuilder();
        for(char c : s.toCharArray()) {
            sb.append(c-'a'+1);
        }
        int res = 0;
        for(char c : sb.toString().toCharArray()) {
            res += c-'0';
        }
        for(int i=0;i<k-1;i++){
            res = cf(res);
        }
        return res;
    }
    public int cf(int k) {
        if(k==0) return 0;
        int res = 0;
        while(k != 0) {
            res += k%10;
            k /= 10;
        }
        return res;
    }

    public String maximumNumber(String num, int[] change) {
        int idx = -1;
        for(int i=0;i<num.length();i++) {
            int n = num.charAt(i) - '0';
            if( n > change[n]) {
                idx = i;
                break;
            }
        }
        System.out.println(idx);
        if(idx == -1) return num;
        int j = idx+1;
        while(j<num.length() && change[num.charAt(j)-'0'] >= num.charAt(j)-'0') {
            j++;
        }
        StringBuilder sb = new StringBuilder();

        for(int i=0;i<num.length();i++) {
            if(i<idx || i>=j) {
                sb.append(num.charAt(i));
            }else {
                int n = num.charAt(i) - '0';
                sb.append(change[n]);
            }
        }
        return sb.toString();
    }
    int res = Integer.MIN_VALUE;
    public int maxCompatibilitySum(int[][] students, int[][] mentors) {

        int[] pl = new int[students.length];
        for(int i=0;i<students.length;i++) pl[i] = i;
        dfs(pl,0,students,mentors);
        return res;
    }
    public void dfs(int[] arr,int idx, int[][] students, int[][] mentors) {
        if(idx == arr.length-1) {
            int s = 0;
            for(int i=0;i<arr.length;i++) {
                s += check(students[i],mentors[arr[i]]);
            }
            res = Math.max(res,s);
        }
        for(int i=idx;i<arr.length;i++) {
            swap(arr,idx,i);
            dfs(arr,idx+1,students,mentors);
            swap(arr,idx,i);
        }
    }
    public int check(int[] a, int[] b ) {
        int res = 0;
        for(int i=0;i<a.length;i++) {
            if(a[i] == b[i]) res++;
        }
        return res;
    }
    public void swap(int[] a,int i,int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
