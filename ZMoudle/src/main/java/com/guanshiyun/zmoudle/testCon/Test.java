package com.guanshiyun.zmoudle.testCon;

import java.util.*;

public class Test {
    public static void main(String[] args) {
        test1();

    }
    //删除链表中倒数第 N 个节点
     static void test1(){
        System.out.print("请输入 head = ,");
        System.out.println("请输入 n = ");
        Scanner sc = new Scanner(System.in);
        int headNum = 5;
        ListNode head = new ListNode("");
        ListNode headNext = head;
        for (int i = 0; i < headNum; i++) {
            String next = sc.next();
            headNext.next = new ListNode(next);
            headNext = headNext.next;
        }
        int n = sc.nextInt();
        ListNode result = remove(head, n);
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        while (result != null) {
            if(result.val != null && !result.val.isEmpty()){
                sb.append(result.val);
                if(result.next != null){
                    sb.append(", ");
                }

            }
            result = result.next;
        }
        sb.append("]");
        System.out.println(sb.toString());
    }

    static ListNode remove(ListNode head, int n) {
        // 边界校验
        if (head == null) {
            throw new IllegalArgumentException("head is null");
        }
        if (n <= 0 ) {
            throw new IllegalArgumentException("n is illegal" + n);
        }
        int length = 0;
        ListNode temp = head;
        while (temp != null) {
            length++;
            temp = temp.next;
        }
        if (n > length) {
            throw new IllegalArgumentException("n is illegal  "+n + " > " + length);
        }

        if (n == length) {
            return head.next;
        }
        temp = head;
        int steps = length - n - 1;
        for (int i = 0; i < steps; i++) {
            temp = temp.next;
        }


        temp.next = temp.next.next;
        return head;
    }



    static class ListNode {
        String val;
        ListNode next;
        public ListNode(String val) {
            this.val = val;
        }
    }
}


class Test2{
    public static void main(String[] args) {
        test2();
    }
    // 二. 识别有效括号
    static void test2(){
        HashMap<String, String> map = new HashMap<>();
        //这里暂时无法写出通用对称括号匹配的代码,需要类型，就要往map里面加对应的关系
        map.put("(",")");
        map.put("[","]");
        map.put("{","}");
        Scanner sc = new Scanner(System.in);
        String next = sc.nextLine();
        if(next.isEmpty() || next.length()%2 != 0){
            System.out.println(false);
        }
        for (int i = 0; i < next.length()-1; i++) {
            char c1 = next.charAt(i);
            char c2 = next.charAt(i + 1);
            boolean equals = map.get(String.valueOf(c1)).equals(String.valueOf(c2));
            if(!equals){
                System.out.println(equals);
                return;
            }
        }
        System.out.println(true);
    }
}



class Test3{
    public static void main(String[] args) {
        test3();

    }
    //3. 最小路径和

    static void test3(){
        Scanner sc = new Scanner(System.in);
        int n = 3;//行
        int m = 3;//列
        int [][]arrN = new int[n][m];
        for (int i = 0; i < n; i++) {
            int[] aarM = new int[m];
            for (int j = 0; j < m; j++) {
                int nextInt = sc.nextInt();
                aarM[j] = nextInt;
            }
            arrN[i] = aarM;
        }
        for(int i = 0; i < arrN.length; i++){
            for (int j = 0; j < arrN[i].length; j++) {
                if (i == 0 && j == 0) {
                    continue;
                } else if (i == 0) {
                    arrN[i][j] += arrN[i][j - 1];
                } else if (j == 0) {
                    arrN[i][j] += arrN[i - 1][j];
                } else {
                    arrN[i][j] += Math.min(arrN[i - 1][j], arrN[i][j - 1]);
                }
            }
        }
        System.out.println( arrN[n - 1][m - 1]);
    }
}

