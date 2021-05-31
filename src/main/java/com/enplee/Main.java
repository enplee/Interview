package com.enplee;

import com.enplee.dataStruc.FastSort;
import com.enplee.dataStruc.Heap;
import com.enplee.dataStruc.MergeSort;
import com.enplee.singleton.SingleTon_03;

import java.util.Arrays;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        SingleTon_03 instance = SingleTon_03.getInstance();
        SingleTon_03 instance1 = SingleTon_03.getInstance();
        System.out.println(instance.equals(instance1));


        Heap<Integer> integerHeap = new Heap<>(10);
        Integer[] arr = new Integer[]{21,43453,2,32,4,43232};
        MergeSort.mergeSort(arr);
        System.out.println(Arrays.toString(arr));
    }
}
