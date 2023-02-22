package edu.yu.cs.com1320.project.impl;

public class Tester {

    public static void main(String[] args){
        HashTableImpl<Integer,String> test = new HashTableImpl<>();
        test.put(1,"Hello");
        test.put(2,"This1");
        test.put(3,"is");
        test.put(4,"a");
        test.put(5,"test");
        test.put(2,"This2");
        test.put(1,"Hello2");

        for(int i = 1; i < 6; i++){
            System.out.println(test.get(i));
        }



    }

}
