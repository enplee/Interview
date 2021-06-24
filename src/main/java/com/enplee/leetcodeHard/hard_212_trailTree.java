package com.enplee.leetcodeHard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class hard_212_trailTree {

    int[] dx = new int[]{0,0,1,-1};
    int[] dy = new int[]{1,-1,0,0};
    boolean[][] visit;
    public List<String> findWords(char[][] board, String[] words) {
        Set<String> set = new HashSet<>();
        TrailTree tree = new TrailTree();
        for(String word : words) {
            tree.addString(word);
        }
        int m = board.length, n = board[0].length;
        for(int i=0;i< board.length;i++){
            for(int j=0;j< board[0].length;j++){
                visit = new boolean[m][n];
                dfs(board,tree,set,"",i,j);
            }
        }
        return List.copyOf(set);
    }

    public void dfs(char[][] board,TrailTree tree,Set<String> res,String path,int i,int j){
        path += board[i][j];
        visit[i][j] = true;
        if(tree.isPath(path)) {
            res.add(path);
        }
        if(tree.isPrefix(path)){
            for(int k=0;k<4;k++){
                int x = i+dx[k], y = j+dy[k];
                if(x>=0 && x< board.length && y>=0 && y< board[0].length && !visit[x][y]) {
                    dfs(board,tree,res,path,x,y);
                }
            }
        }
        visit[i][j] = false;
    }
}
class TrailTree {

    Node root;

    public TrailTree() {
        this.root = new Node();
    }

    public boolean isPrefix(String s){
        Node point = root;
        for(char c : s.toCharArray()){
            if(point.containKey(c)) point = point.getChild(c);
            else return false;
        }
        return true;
    }

    public boolean isPath(String s) {
        Node point = root;
        for(char c : s.toCharArray()){
            if(point.containKey(c)) point = point.getChild(c);
            else return false;
        }
        return point.isEnd();
    }

    public void addString(String s) {
        Node point = root;
        for(char c : s.toCharArray()){
            if(!point.containKey(c)) point.setChild(c);
            point = point.getChild(c);
        }
        point.setEnd(true);
    }
}
class Node {

    Node[] childs;
    boolean isEnd;

    public Node() {
        this.childs = new Node[26];
        this.isEnd = false;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public boolean containKey(char c){
        return childs[c-'a'] != null;
    }

    public Node getChild(char c){
        return childs[c-'a'];
    }

    public void setChild(char c){
        childs[c-'a'] = new Node();
    }
}
