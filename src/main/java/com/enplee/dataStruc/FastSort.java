package com.enplee.dataStruc;

import java.util.Arrays;

public class FastSort {
    public static void fastSort(int[] arr){
        fastSort(arr,0,arr.length-1);
        System.out.println(Arrays.toString(arr));
    }
    private static void fastSort(int[] arr,int start,int end){
        if(start>=end) return;
        int i = start, j = end;
        int temp = arr[i];
        while (i<j){
            while (i<j && arr[j]>temp) j--;
            if(i<j) {
                arr[i] = arr[j];
            }
            while (i<j && arr[i]<=temp) i++;
            if(i<j) {
                arr[j] = arr[i];
            }
        }
        arr[j]= temp;
        fastSort(arr,start,j-1);
        fastSort(arr,j+1,end);
    }
}
