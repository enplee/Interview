package com.enplee.dataStruc;

public class MergeSort {
    private static Comparable[] copy;

    public static void mergeSort(Comparable[] arr){
        copy = new Comparable[arr.length];
    }

    private static void mergeSort(Comparable[] arr,int lo,int hi){
        if(lo>=hi) return;
        int mid = (lo+hi)>>1;
        mergeSort(arr,lo,mid);
        mergeSort(arr,mid+1,hi);
        merge(arr,lo,mid,hi);
    }
    private static void merge(Comparable[] arr,int lo,int mid,int hi){
        for(int k=lo;k<=hi;k++){
            copy[k] = arr[k];
        }
        int i = lo,j = mid+1;
        for(int k=lo;k<hi;k++){
            if(i>mid) arr[k] = copy[j++];
            else if(j>hi) arr[k] = copy[i++];
            else if(less(copy[i],copy[j])) arr[k] = copy[i++];
            else {
                // cnt += (j-mid)
                arr[k] = copy[j++];
            }
        }
    }
    private static boolean less(Comparable a,Comparable b){
        return a.compareTo(b) < 0;
    }
}
