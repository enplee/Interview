package com.enplee.leetcodeHard;

import java.util.HashMap;
import java.util.Map;

public class hard_149_enum {
    public int maxPoints(int[][] points) {
        if(points.length<3) return points.length;
        int res =0;

        for(int i=0;i<points.length;i++){
            int duplicat = 0;
            int max = 0;
            Map<String,Integer> map = new HashMap<>();
            for(int j=i+1;j<points.length;j++){
                int x = points[j][0] - points[i][0];
                int y = points[j][1] - points[i][1];
                if(x==0 && y==0) duplicat++;
                else {
                    int gcd = gcd(x,y);
                    x /= gcd;
                    y /= gcd;
                    String key = x +"#"+y;
                    map.put(key,map.getOrDefault(key,0)+1);
                    max = Math.max(max,map.get(key));
                }
            }
            res = Math.max(res,max+duplicat+1);
        }
        return res;
    }
    public int gcd(int a,int b){
        return b==0 ? a : gcd(b,a%b);
    }
}
