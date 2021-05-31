package com.enplee.leetcodeHard;

public class hard_JZ51_mergeSort {
    int[] copy;
    int res = 0;
    public int reversePairs(int[] nums) {
        int n = nums.length;
        copy = new int[n];
        mergeSort(nums,0,n-1);
        return res;
    }

    public void mergeSort(int[] nums,int l,int r){
        if(l>=r) return;
        int mid = (l+r)>>1;
        mergeSort(nums,l,mid);
        mergeSort(nums,mid+1,r);
        merge(nums,l,mid,r);
    }

    public void merge(int[] nums,int l,int mid,int r){
        for(int k=l;k<=r;k++) copy[k] = nums[k];
        int i = l,j = mid+1;
        for(int k=l;k<=r;k++) {
            if(i>mid) {
                res += j-k;
                nums[k] = copy[j++];
            }else if(j>r){
                nums[k] = copy[i++];
            }else if(copy[i]<=copy[j]){
                nums[k] = copy[i++];
            }else{
                res += j-k;
                nums[k] = copy[j++];
            }
        }
    }
}
