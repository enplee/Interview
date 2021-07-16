package com.enplee.leetcodeHard;

import java.util.Deque;
import java.util.LinkedList;

public class hard_224_calculator_2 {
    public int calculate(String s) {
        return calculate(s,0,s.length()-1);
    }
    private int calculate(String s,int l,int r) {
        Deque<Integer> stack = new LinkedList<>();
        int cnt = 0,idx = l;
        char op = '+';
        while(idx <= r) {
            char c = s.charAt(idx);
            if(c == '(') {
                int cl = 1,cr = 0, iTemp = idx+1;
                while(cl != cr) {
                    char t = s.charAt(iTemp);
                    if(t == '(') cl++;
                    if(t == ')') cr++;
                    iTemp++;
                }
                cnt = calculate(s,idx+1,iTemp-2);
                idx = iTemp-1;
            }
            if(Character.isDigit(c)) {
                cnt = cnt*10 + (c-'0');
            }
            if(!Character.isDigit(c) && c != ' ' || idx == r) {
                if(op == '+') stack.offerLast(cnt);
                if(op == '-') stack.offerLast(-cnt);
                cnt = 0;
                op  = c;
            }
            idx++;
        }
        int res = 0;
        for(int i:stack) res+=i;
        return res;
    }
}
