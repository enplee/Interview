package com.enplee.leetcodeHard.weekgame;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class WeekGame_210523 {
    public boolean checkZeroOnes(String s) {
        int maxOne = 0, maxZero = 0;
        int idx = 0;
        while (idx<s.length()) {
            if(s.charAt(idx)=='1'){
                int temp = 0;
                while (idx<s.length() && s.charAt(idx) == '1'){
                    idx++;
                    temp++;
                }
                maxOne = Math.max(maxOne,temp);
            }else {
                int temp = 0;
                while (idx<s.length() && s.charAt(idx) == '0'){
                    idx++;
                    temp++;
                }
                maxZero = Math.max(maxZero,temp);
            }
        }
        return maxOne>maxZero;
    }
    public boolean canReach(String s, int minJump, int maxJump) {
        if(s.charAt(s.length()-1)=='1') return false;
        Deque<Integer> queue = new LinkedList<>();
        int maxLen = minJump-1;
        queue.addLast(0);

        while (!queue.isEmpty()) {
            int top = queue.pollFirst();
            if(top==s.length()-1) return true;
            for(int idx = Math.max(maxLen+1,top+minJump);idx<=Math.min(s.length()-1,top+maxJump);idx++){
                if(s.charAt(idx)=='0') {
                    queue.addLast(idx);
                }
            }
            maxLen = Math.max(maxLen,Math.min(s.length()-1,top+maxJump));
        }
        return false;
    }
}
