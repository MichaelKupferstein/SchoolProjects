package edu.yu.cs.com1320.project.impl;

public class Tester {

    public static void main(String[] args){
        HashTableImplUsingMyList<String,Integer> test = new HashTableImplUsingMyList<>();
        test.put("Hello",1);
        test.put("This",2);
        test.put("Is",3);
        test.put("A",4);
        test.put("Hello",5);
        int not = test.get("Hello");
        System.out.println(not);
    }

}
