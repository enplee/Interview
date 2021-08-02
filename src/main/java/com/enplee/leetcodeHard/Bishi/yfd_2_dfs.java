package com.enplee.leetcodeHard.Bishi;

import java.util.Scanner;

public class yfd_2_dfs {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String s  = sc.nextLine();
            System.out.println(s);
            System.out.println(solution(s,0,s.length()-1));
        }
    }
    public static int solution(String s, int i,int j) {
        //System.out.println(i+ " " + j);
        if(i>j) return 0;
        int idx = i+1, l = 1, r = 0;
        while (idx <= j && l != r) {
            //System.out.println(l+" "+ r);
            if(s.charAt(idx) == '[') l++;
            if (s.charAt(idx) == ']') r++;
            idx++;
        }
       // System.out.println(idx);
        if(idx > j) {
            return solution(s,i+1,j-1) + 1;
        }
        char c = s.charAt(idx);
        if(Character.isDigit(c)) {
            return (solution(s,i+1,idx-2)+1)*(c-'0') + solution(s,idx+1,j);
        }
        return solution(s,i+1,idx-2)+1 + solution(s,idx,j);
    }
}
