package com.enplee.leetcodeHard.weekgame;

import java.util.HashMap;
import java.util.Map;

public class WeekGame_210506 {
    public int subsetXORSum(int[] nums) {
        int len = nums.length;
        int res = 0;
        for(int i=0;i<(1<<len);i++){
            res += getXOR(nums,i);
        }
        return res;
    }
    public int getXOR(int[] nums,int i){
        int sum = 0;
        if(i==0) return 0;
        for(int k=0;k<31;k++){
            if((i&k)!=0) sum^= nums[k];
        }
        return sum;
    }

    public int minSwaps(String s) {
        int OneN = count(s,'1'),ZeorN = count(s,'0');
        if(Math.abs(OneN-ZeorN)>1) return -1;
        int cnt1 = 0,cnt2 = 0;
        char c;
        for(int i=0;i<s.length();i++){
            if(i%2==0) c = '1';
            else c = '0';
            if(s.charAt(i)==c) cnt1++;
        }
        for(int i=0;i<s.length();i++){
            if(i%2==0) c = '0';
            else c = '1';
            if(s.charAt(i)==c) cnt2++;
        }
        if(OneN==ZeorN) return Math.min(cnt1,cnt2);
        if(OneN >ZeorN) return cnt1;
        return cnt2;
    }
    public int count(String s,char c){
        int res = 0;
        for(char ch : s.toCharArray()){
            if(ch==c) res++;
        }
        return res;
    }
}

class FindSumPairs {
    int[] nums1;
    int[] nums2;
    Map<Integer,Integer> map;
    public FindSumPairs(int[] nums1, int[] nums2) {
        this.nums1 = nums1;
        this.nums2 = nums2;
        this.map = new HashMap<>();
        for(int i: nums2){
            map.put(i, map.getOrDefault(i,0)+1);
        }
    }

    public void add(int index, int val) {
        int newVal = nums2[index]+val;
        map.put(newVal, map.getOrDefault(newVal,0)+1);
        map.put(nums2[index], map.getOrDefault(nums2[index],0)-1);
    }

    public int count(int tot) {
        int res = 0;
        for(int i:nums1){
            if(map.containsKey(tot-i)){
                res += map.get(tot-i);
            }
        }
        return res;
    }

//    public int rearrangeSticks(int n, int k) {
//        int MOD = 1000000000+7;
//        long[] JC= new long[n];
//        JC[0] = 1;
//        for(int i=1;i<n;i++){
//            JC[i] = (JC[i-1]*i)%MOD;
//        }
//        System.out.println(Arrays.toString(JC));
//        int[][] dp = new int[n+1][k+1];
//
//        for(int i=1;i<=n;i++){
//            for(int j=1;j<=k;j++){
//                if(j<=i){
//                    if(i==j) dp[i][j] = 1;
//                    else if(i==1) dp[i][j] = 1;
//                    else {
//                        for(int y=j;y<=i;y++){
//                            dp[i][j] += dp[y-1][j-1]*((C(i-1,y-1,JC)*JC[i-y])%MOD)%MOD;
//                        }
//                    }
//                }
//            }
//        }
//        return dp[n][k];
//    }
//    public int C(int a,int b,long[] jc){
//        return (int)jc[a]/(jc[b]*jc[a-b]);
//    }
}
