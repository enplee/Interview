package com.enplee.dataStruc;

import java.util.HashMap;

public class Heap<K extends Comparable<K>>{
    private K[] pq = null;
    private int size = 0;

    public Heap(int capacity) {
        pq = (K[])new Comparable[capacity+1];
    }

    public K peek(){
        return pq[1];
    }

    public K pop(){
        K top = pq[1];
        exch(1,size);
        size--;

        sink(1);
        return top;
    }

    public void push(K key){
        pq[++size] = key;

        swim(size);
    }
    private void sink(int idx){
        while (left(idx)<=size){
            int older = left(idx);
            if(right(idx)<=size && less(older,right(idx))){
                older = right(idx);
            }
            if(less(older,idx)) break;
            exch(older,idx);
            idx = older;
        }
    }
    private void swim(int idx){
        while (idx<1 && less(idx,parent(idx))){
            exch(idx,parent(idx));
            idx = parent(idx);
        }
    }
    private int parent(int idx){
        return idx/2;
    }
    private int left(int idx){
        return idx*2;
    }
    private int right(int idx){
        return idx*2+1;
    }
    private void exch(int i,int j){
        K temp = pq[i];
        pq[i] = pq[j];
        pq[j] = temp;
    }
    private boolean less(int i,int j){
        return pq[i].compareTo(pq[j])<0;
    }
}
