package com.enplee.dataStruc;

import javax.print.DocFlavor;
import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    // get 调整到头 put 满了删除尾部节点(队列和map) 没满就直接放在队头
    private int capacity;
    private int size;
    private Map<Integer,LRUNode> map = null;
    private LRUNode head = null;
    private LRUNode trail = null;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>();
        head = new LRUNode();
        trail = new LRUNode();
        head.next = trail;
        trail.pre = head;
    }

    public int get(int key){
        if(map.containsKey(key)){
            LRUNode node = map.get(key);
            pop(node);
            insertHead(node);
            return node.val;
        }
        return -1;
    }

    public void put(int key,int val){
        LRUNode node = null;
        if(map.containsKey(key)){
            node = map.get(key);
            node.val = val;
            pop(node);
        }else {
            node = new LRUNode(key,val);
            if(size < capacity){
                size++;
            }else {
                LRUNode temp = trail.pre;
                pop(temp);
                map.remove(temp.key,temp);
            }
            map.put(key,node);
        }
        insertHead(node);
    }

    private class LRUNode{
        private int key;
        private int val;
        private LRUNode next;
        private LRUNode pre;

        public LRUNode() {
        }

        public LRUNode(int key, int val) {
            this.key = key;
            this.val = val;
        }
    }

    private void pop(LRUNode node){
        node.next.pre = node.pre;
        node.pre.next = node.next;
    }

    private void insertHead(LRUNode node){
        node.next = head.next;
        node.pre = head;
        head.next.pre = node;
        head.next = node;
    }
}
