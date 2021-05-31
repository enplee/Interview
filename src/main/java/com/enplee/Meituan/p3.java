package com.enplee.Meituan;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class p3 {
    public static Map<Integer,Boolean> visted;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()){
            int m = sc.nextInt(),n = sc.nextInt(),k = sc.nextInt();
            Map<Integer,Map<Integer,Integer>> Graph = new HashMap<>();
            for(int i=0;i<k;i++){
                int x = sc.nextInt(),y = sc.nextInt(),u = sc.nextInt(), v = sc.nextInt(),w = sc.nextInt();
                int pre = decode(x,y),next = decode(u,v);
                if(Graph.get(pre)==null){
                    Map<Integer,Integer> temp = new HashMap<>();
                    temp.put(next,w);
                    Graph.put(pre,temp);
                }else {
                    Graph.get(pre).put(next,w);
                }
            }
            visted = new HashMap<>();
            if(!Graph.containsKey(decode(1,1))){
                System.out.println(-1);
            }else {
                System.out.println(solution(1,1,m,n,Graph));
            }
        }
    }
    public static int decode(int x,int y){
        return x*1000+y;
    }

    public static int solution(int i, int j, int m, int n, Map<Integer,Map<Integer, Integer>> Graph){
        if(m==i && n==j) {
            return 0;
        }
        Map<Integer,Integer> temp = Graph.get(decode(i,j));
        if(temp==null) return -1;
        visted.put(decode(i,j), true);
        int mmin = Integer.MAX_VALUE;
        for(int key : temp.keySet()){
            int u = key/1000,v = key%1000;

            if(!visted.containsKey(key)) {
                int res = solution(u,v,m,n,Graph);
                if(res!=-1){
                    mmin = Math.min(mmin,temp.get(key)+res);
                }
            }
        }
        visted.put(decode(i,j), false);
        if(mmin == Integer.MAX_VALUE){
            return -1;
        }
        return mmin;
    }




}

