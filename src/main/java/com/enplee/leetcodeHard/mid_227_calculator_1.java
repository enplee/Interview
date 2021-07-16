package com.enplee.leetcodeHard;

import java.util.Deque;
import java.util.LinkedList;

public class mid_227_calculator_1 {
    public int calculate(String s) {
        Deque<Integer> stack = new LinkedList<>();
        int cnt = 0;
        char op = '+';
        for(int i=0;i<s.length();i++) {
            char c = s.charAt(i);
            if(Character.isDigit(c)) {
                cnt = cnt*10+ (c-'0');
            }
            if((!Character.isDigit(c) && c != ' ') || i == s.length()-1){
                if(op == '+') {
                    stack.offerLast(cnt);
                }else if(op == '-') {
                    stack.offerLast(-cnt);
                }else if(op == '*') {
                    int top = stack.pollLast();
                    stack.offerLast(top*cnt);
                }else {
                    int top = stack.pollLast();
                    stack.offerLast(top/cnt);
                }
                cnt = 0;
                op = c;
            }

        }
        int res = 0;
        for(int i:stack) res += i;
        return res;
    }
}
