package com.enplee.leetcodeHard;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class hard_076_window {
    public String minWindow(String s, String t) {
        Map<Character,Integer> tMap = new HashMap<>();
        for(char c : t.toCharArray()) {
            tMap.put(c,tMap.getOrDefault(c,0)+1);
        }
        int len = Integer.MAX_VALUE;
        String res = "";

        Map<Character,Integer> charCnt = new HashMap<>();
        int start = 0, achieve = 0;
        for(int i=0;i<s.length();i++) {
            char c = s.charAt(i);
            charCnt.put(c,charCnt.getOrDefault(c,0)+1);
            if(tMap.containsKey(c) && Objects.equals(charCnt.get(c), tMap.get(c))) {
                achieve++;
            }

            while (achieve == tMap.size()) {
                if(i-start+1 < len) {
                    len = i-start+1;
                    res = s.substring(start,i+1);
                }
                char sc = s.charAt(start);
                charCnt.put(sc,charCnt.get(sc)-1);
                if(tMap.containsKey(sc) && charCnt.get(sc) == tMap.get(sc)-1) {
                    achieve --;
                }
                start++;
            }

        }
        return res;
    }
}
