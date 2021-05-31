package com.enplee.dataStruc;

import java.util.Deque;
import java.util.LinkedList;

public class Postorder {
    public static void dfs(TreeNode root){
        dfs(root.left);
        dfs(root.right);
        System.out.println(root.val);
    }

    public static void traversal(TreeNode root){
        Deque<TreeNode> stack = new LinkedList<>();
        TreeNode pre = null;
        while (root!=null || !stack.isEmpty()){
            while (root !=null){
                stack.push(root);
                root = root.left;
            }
            TreeNode peek = stack.peek();
            if(peek.right == null || peek.right == pre){
                System.out.println(peek);
                pre = peek;
                stack.pop();
            }else {
                root = peek.right;
            }
        }
    }
}
