package com.enplee.leetcodeHard;

import java.util.*;

public class hard_030_window {
    public List<Integer> findSubstring(String s, String[] words) {
        List<Integer> res = new LinkedList<>();
        int wordL = words[0].length(), stringL = words.length*wordL;
        if(s.length() < stringL) return res;

        Map<String,Integer> wordM = new HashMap<>();
        for(String word : words) {
            wordM.put(s,wordM.getOrDefault(word,0)+1);
        }

        for(int i=0;i<s.length();i++) {
            if(i+stringL <= s.length() && check(wordM,s.substring(i,i+stringL),wordL)) {
                res.add(i);
            }
        }
        return res;
    }
    public boolean check(Map<String,Integer> words,String subString,int len) {
        Map<String,Integer> sub = new HashMap<>();
        int k = subString.length()/len;
        for(int i=0;i<k;i++) {
            String word = subString.substring(i*len,(i+1)*len);
            if(!words.containsKey(word) || sub.getOrDefault(word,0)==words.getOrDefault(word,0)){
                return false;
            }
            sub.put(word,sub.getOrDefault(word,0)+1);
        }

        if(words.size() != sub.size()) return false;
        for(String key : sub.keySet()) {
            int subV = sub.get(key),wordV = words.getOrDefault(key,0);
            if(subV != wordV) return false;
        }
        return true;
    }
}
