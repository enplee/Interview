package com.enplee.leetcodeHard;

import java.util.Deque;
import java.util.LinkedList;

public class mid_1190_stack {
    // 标准栈解法 res+stack
    public String reverseParentheses(String s) {
        Deque<String> stack = new LinkedList<>();
        String res = "";
        for(char c : s.toCharArray()) {
            if(c == '(') {
                stack.offerLast(res);
                res = "";
            }else if (c == ')') {
                res = stack.pollLast() + new StringBuffer(res).reverse().toString();
            }else {
                res += c;
            }
        }
        return res;
    }
    // 巧妙解法 括号决定了遍历顺序 遇到一个括号就改变遍历顺序
    public String reverseParentheses_2(String s) {
        int n = s.length();
        int[] next = new int[n];
        Deque<Integer> stack = new LinkedList<>();
        for(int i=0;i<n;i++){
            if(s.charAt(i) == '('){
                stack.offerLast(i);
            }
            if(s.charAt(i) == ')') {
                int j = stack.pollLast();
                next[j] = i;
                next[i] = j;
            }
        }
        int dir = 1;
        StringBuilder res = new StringBuilder();
        for(int i=0;i<n;i+=dir) {
            if(s.charAt(i)=='(' || s.charAt(i) == ')'){
                dir = -dir;
                i = next[i];
            }else {
                res.append(s.charAt(i));
            }
        }
        return res.toString();
    }
}
