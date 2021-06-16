package com.enplee.leetcodeHard;

public class hard_233_guilv {
    public int countDigitOne(int n) {
        int idx = 1, num = n;
        int res = 0;
        while (num > 0 ) {
            int k = (int)Math.pow(10,idx);
            int first = n/k, last = k/10;
            int bit = (n-first)/k;

            res += first*last;
            if(bit == 1) res += n%k;
            if(bit > 1) res += last;

            idx++;
            num /= 10;
            System.out.println(res);
        }
        return res;
    }
}
