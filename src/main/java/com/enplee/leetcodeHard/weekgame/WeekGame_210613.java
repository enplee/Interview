package com.enplee.leetcodeHard.weekgame;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WeekGame_210613 {
    public boolean makeEqual(String[] words) {
        Map<Character,Integer> map = new HashMap<>();
        for(String word : words) {
            for(char c : word.toCharArray()){
                map.put(c,map.getOrDefault(c,0)+1);
            }
        }
        int n = words.length;
        for(char key : map.keySet()) {
            if(map.get(key)%n != 0) return false;
        }
        return true;
    }

    public int maximumRemovals(String s, String p, int[] removable) {
        int n = Math.min(removable.length,s.length()-p.length());// 最长可以搜索的长度
        int l = 0,r = n-1;
        int res = 0;
        while (l<r) {
            int mid = (l+r)>>1;
            if(isSubsequence(s,p,removable,mid)){
                System.out.println(mid);
                res = mid;
                l = mid+1;
            }else {
                r = mid-1;
            }
        }
        return res;
    }
    public boolean isSubsequence(String s,String p,int[] removable, int k) {
        int i=0,j=0;
        Set<Integer> set = new HashSet<>();
        for(int t=0;t<k;t++) set.add(removable[t]);
        while (i<s.length() || j<p.length()) {
            if(!set.contains(i) && s.charAt(i)==p.charAt(j)){
                j++;
            }
            i++;
        }
        return j == p.length();
    }
    public boolean mergeTriplets(int[][] triplets, int[] target) {
        int x = target[0], y = target[1], z = target[2];
        List<Integer> list = new LinkedList<>();
        for(int i=0;i< triplets.length;i++) {
            int[] triplet = triplets[i];
            if(triplet[0]<=x && triplet[1]<=y && triplet[2]<=z){
                list.add(i);
            }
        }
        int tempx = 0,tempy = 0, tempz = 0;
        for(int idx : list) {
            int[] triplet = triplets[idx];
            tempx = Math.max(tempx,triplet[0]);
            tempy = Math.max(tempy,triplet[1]);
            tempz = Math.max(tempz,triplet[2]);
        }
        return tempx == x && tempy == y && tempz == z;
    }
    public int[] earliestAndLatest(int n, int firstPlayer, int secondPlayer) {
        return null;
    }
    public int earliest(int n,int firstPlayer, int secondPlayer) {
        if(firstPlayer + secondPlayer == n+1) return 1;
        return 0;
    }
    public int Latest(int n,int firstPlayer, int secondPlayer) {
        if(firstPlayer + secondPlayer == n+1) return 1;
        return 0;
    }
}
