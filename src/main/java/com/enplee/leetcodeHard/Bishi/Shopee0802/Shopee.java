package com.enplee.leetcodeHard.Bishi.Shopee0802;

import java.util.*;

public class Shopee {
    public static void main(String[] args) {
        Shopee shopee = new Shopee();
        String xml = "<people><name>shopee</name><fa>fa</fa></people>";
        String path = "people.fa";
        System.out.println(shopee.GetXMLValue(xml,path));
    }
    public int findBalancedIndex(int[] inputArray) {
        // write code here
        int[] pre = new int[inputArray.length];
        int[] trail = new int[inputArray.length];
        for(int i=1;i<inputArray.length;i++) {
            pre[i] = pre[i-1]+inputArray[i-1];
            int j = inputArray.length-1-i;
            trail[j] = trail[j+1]+inputArray[j+1];
        }

        for(int i=0;i<inputArray.length;i++) {
            if(pre[i] == trail[i]) return i;
        }
        return 0;
    }
    public int divide(int n, int k) {
        // write code here
        int[][] dp = new int[n+1][k+1];
        for(int i=1;i<=n;i++) dp[i][1] = 1;

        for(int i=2;i<=n;i++) {
            for(int j=2;j<=k;j++) {
                if(i>j) {
                    dp[i][j] = dp[i-1][j-1] + dp[i-j][j];
                }else {
                    dp[i][j] = dp[i-1][j-1];
                }
            }
        }

        return dp[n][k];
    }

    public String GetXMLValue(String inxml, String path) {
        // write code here
        String[] paths = path.split("\\.");

        List<XMLNode> list = parseXmlToNode(inxml, 0, inxml.length() - 1);
        for(XMLNode n : list)  {
            if(n == null) continue;
            Deque<String> pathQueue = new LinkedList<>();
            for(String s : paths) pathQueue.offerLast(s);
            String res = n.findPath(pathQueue);
            if(!Objects.equals(res, "")) return res;
        }
        return "";
    }
    public String findPath(String xml,Deque<String> path) {
        String top = path.pollFirst();
        int first = xml.indexOf(top);
        System.out.println(first);
        int last = xml.lastIndexOf(top);
        if(last == -1 || first != 1) return "";
        String substring = xml.substring(first + top.length() + 1, last - 2);
        if(path.size() == 0) {
            return substring;
        }else {
            return findPath(substring,path);
        }
    }
    public List<XMLNode> parseXmlToNode(String xml,int l,int r) {
        List<XMLNode> list = new LinkedList<>();
        if(l>r) return list;
        System.out.println(l+" "+r + xml.substring(l,r+1));
        char c = xml.charAt(l);
        if(c == '<') {
            int idx = l;
            while(xml.charAt(idx) != '>') {
                idx++;
            }

            String rootVal = xml.substring(l+1,idx);
            XMLNode node = new XMLNode(rootVal);
            list.add(node);

            int lidx = xml.lastIndexOf(rootVal);
            //System.out.println(lidx + " " + rootVal);
            List<XMLNode> list1 = parseXmlToNode(xml, idx + 1, lidx - 3);
            for(XMLNode n : list1) {
                if(n == null) continue;
                node.childs.put(n.val,n);
            }
            List<XMLNode> list2 = parseXmlToNode(xml, lidx + rootVal.length() + 1, r);
            list.addAll(list2);

        }else {
            XMLNode xmlNode = new XMLNode(xml.substring(l, r + 1));
            xmlNode.isFinal = true;
            list.add(xmlNode);
        }
        return list;
    }
}
class XMLNode {
    public String val;
    public Map<String,XMLNode> childs;
    public boolean isFinal;
    public XMLNode(String val) {
        this.val = val;
        childs = new HashMap<>();
    }

    public String findPath(Deque<String> path) {
        String top = path.pollFirst();
        if(!top.equals(this.val)) return "";
        if(path.size() == 0) {
            for(String s : this.childs.keySet()) {
                if(this.childs.get(s).isFinal) {
                    return s;
                }
                return "";
            }
        }
        String next = path.peekFirst();
        if(this.childs.containsKey(next)) {
            return this.childs.get(next).findPath(path);
        }
        return "";
    }
}
