package com.enplee.leetcodeHard;

import java.util.HashMap;
import java.util.Map;

public class hard_810_bestChoose {
    public boolean xorGame(int[] nums) {
        int sum = 0,n = nums.length;
        for(int num:nums) sum ^=num;
        return sum!=0 && n%2==0;
    }
}
