package com.enplee.dataStruc;

import com.sun.source.tree.Tree;

import java.util.Deque;
import java.util.LinkedList;

public class Preorder {
    public void dfs(TreeNode root){
        System.out.println(root.val);
        dfs(root.left);
        dfs(root.right);
    }
    public void Traveral(TreeNode root){
        Deque<TreeNode> stack = new LinkedList<>();
        while (root !=null || !stack.isEmpty()){
            while (root != null){
                System.out.println(root.val);
                stack.push(root);
                root = root.left;
            }
            root = stack.pop().right;
        }
    }
}
class TreeNode{
    public int val;
    public TreeNode left;
    public TreeNode right;
}
