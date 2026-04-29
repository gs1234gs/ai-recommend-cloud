package com.guanshiyun;

public class test01 {
    public static void main(String[] args) {
        int [] arr = {2,3,4,5,6,7,7,7,7,8};
        int a  = 7;
        int index = binarySearchBasic(arr, a);
//        System.out.println("index : " + (index == -1 ? ("not found " + a) : index));
//   int index1 = Test02.binarySearchBasic(arr,a);
//   System.out.println("index1 :" + index1 );
        System.out.println(Test02.leftBinary(arr, a));
//        int max  = Integer.MAX_VALUE;
//        int  i = 0;
//        int j = max -1;
//        System.out.println((i+j)/2);
//        i = (i+j)/2 + 1;
//        System.out.println((i+j)/2 >>>1);

    }

    static int binarySearchBasic(int [] arr, int a){
        int i = 0;
        int j = arr.length - 1;
        while (i <= j) {
            int index = (i+j)>>>1;
            int temp = arr[index];
            if(temp ==a){
                return index;
            }
            else if(temp < a){
                i = index + 1;
            }
            else {
                 j = index -1;
            }
        }
        return -1;
    }
}

class Test02{
    public static int binarySearchBasic(int [] arr ,int a){
        int i = 0;
        int j = arr.length;
        while(i<j){

            int index = (i+j) >>>1;
            int temp = arr[index];
            if(temp<a){
                i = ++index;
            }
            else if(a < temp){
                j = index;
            }
            else{
               return index;
            }
        }
        return -1;
    }


    public static int leftBinary(int [] arr , int a){
        int i = 0;
        int  j = arr.length -1;
        while(i<=j){
            int index = (i+j) >>>1;
            int temp = arr[index];
            if(temp >a){
                j = index - 1;
            }
            else if(temp < a){
                i = index + 1;
            }
            else{
                i = i -1;
            }
        }
        return i;
    }

    public static int binary(int [] arr ,int a){
        int i = 0;
        int j = arr.length;
        while(1  < j -i){
            int index  = (i+j)>>>1;
            if(arr[index] < a){
                i = index + 1;
            }
            else{
                j  = index;
            }
        }

        if(arr[i] == a){
            return i;
        }
        byte n = (byte) 23;
        return -1;
    }
}


