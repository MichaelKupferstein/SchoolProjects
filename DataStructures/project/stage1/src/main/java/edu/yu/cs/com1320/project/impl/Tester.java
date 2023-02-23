package edu.yu.cs.com1320.project.impl;

public class Tester {

    public static void main(String[] args){
        HashTableImpl<Integer,Integer> test = new HashTableImpl<>();

        for(int i = 1; i <= 100; i++){
            test.put(i,i);
        }

        for(int i = 1; i <= 100; i++){
            System.out.print(test.get(i) + " ");
            if(i % 10 == 0){
                System.out.println();
            }
        }
        System.out.println();
        System.out.println();
//        for(int i = 1; i <= 100; i++){
//            test.put(i,i*2);
//        }
        test.put(77,null);
        test.put(59,null);
        for(int i = 1; i <= 100; i++){
            System.out.print(test.get(i) + " ");
            if(i % 10 == 0){
                System.out.println();
            }
        }



    }

}
