package com.enplee.leetcodeHard;

import java.util.*;

/**
 *  @author: leezy
 *  @Date: 2021/5/20 10:16
 *  @Description:
 *  两个有序数组找中位数，可以考虑成：{num1[:m1],num2[:m2]} | {num1[m1:],num2[m2:]} 其中：m1+m2 = (len(num1)+len(num2))/2
 *  并且保证：max(num1[m1-1],num2[m2-1]) < max(num1[m1],num2[m2])
 *  m1与m2的取值范围，通过是nums1<nums2长度来保证。
 */
public class hard_004_bitSearch {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int n1 = nums1.length, n2 = nums2.length;
        if(n2<n1) return findMedianSortedArrays(nums2,nums1);
        int k = (n1+n2+1)/2;
        int i = 0, j = n1;
        while (i<j){
            int m1 = (i+j)>>1, m2 = k-m1;

            if(m1>0 && nums1[m1-1]> nums2[m2]){
                j = m1-1;
            }else if(m2>0 && nums2[m2-1]> nums1[m1]){
                i = m1+1;
            }else {
                break;
            }
        }
        int m1 = (i+j)>>1,m2 = k-m1;
        System.out.println(m1+","+m2);
        int c1 = 0;
        if(m1==0) c1 = nums2[m2-1];
        else if(m2==0) c1 = nums1[m1-1];
        else c1 = Math.max(nums2[m2-1],nums1[m1-1]);
        if((n1+n2)%2==1) return c1;

        int c2 = 0;
        if(m1==n1) c2 = nums2[m2];
        else if(m2==n2) c2 = nums1[m1];
        else c2 = Math.min(nums1[m1],nums2[m2]);
        return ((double)(c1+c2))/2;
    }
    /*
        每日一题
     */
    public List<String> topKFrequent(String[] words, int k) {
        Map<String,Integer> map = new HashMap<>();
        for(String s:words){
            map.put(s,map.getOrDefault(s,0)+1);
        }

        PriorityQueue<String> pq = new PriorityQueue<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(map.get(o1).equals(map.get(o2))){
                    return o1.compareTo(o2);
                }
                return map.get(o1)-map.get(o2);
            }
        });

        for(String key : map.keySet()){
            if(pq.size()<k) pq.add(key);
            else {
                if(compare(key,pq.peek(),map)>0){
                    pq.poll();
                    pq.add(key);
                }
            }
        }

        List<String> res = new ArrayList<>(pq.size());
        System.out.println(pq.size());
        for(int i=0;i<pq.size();i++) res.add(" ");
        for(int i=pq.size()-1;i>=0;i--){
            res.set(i,pq.poll());
        }
        return res;

    }
    public int compare(String o1, String o2, Map<String,Integer> map) {
        if(map.get(o1).equals(map.get(o2))){
            return o1.compareTo(o2);
        }
        return map.get(o1)-map.get(o2);
    }
}
