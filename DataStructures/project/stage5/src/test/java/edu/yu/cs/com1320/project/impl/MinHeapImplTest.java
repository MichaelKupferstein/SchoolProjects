package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MinHeapImplTest {

    MinHeapImpl<testClass> heap;
    List<testClass> list;
    Stack<Integer> stackTime;
    Stack<testClass> stackTest;
    @BeforeEach
    void setUp() {
        this.heap = new MinHeapImpl<>();
        this.list = new ArrayList<>();
        this.stackTime = new Stack<>();
        this.stackTest = new Stack<>();
        Random random = new Random();
        for(int i = 0; i < 20; i++){
            int k = random.nextInt(1000);
            String name = "Number: " + i;
            testClass test = new testClass(name,k);
            this.heap.insert(test);
            this.list.add(test);
        }
        list.sort(Comparator.reverseOrder());
        createStack();
    }

    void createStack(){
        for(int i = 0; i < 20; i++){
            stackTime.push(list.get(i).getTime());
            stackTest.push(list.get(i));
        }
    }
    void printAll(){
        list.sort(Comparator.naturalOrder());
        System.out.println("This is the list:");
        System.out.print("[");
        for(testClass t : list){
            System.out.print(t.toString() + ", ");
        }
        System.out.println("]");

        System.out.println("This is the stack: ");
        System.out.print("[");
        for(Integer i : stackTime){
            System.out.print(i + ", ");
        }
        System.out.print("]\n");

        System.out.println("This is the heap: ");
        System.out.print("[");
        for(int i = 0; i < 21; i++){
            try{
                testClass temp = this.heap.remove();
                System.out.print(temp.getTime() + ", ");
            }catch (NoSuchElementException e){
                System.out.println("] END OF HEAP");
            }
        }
        reMakeHeap();
    }
    void reMakeHeap(){
        for(testClass t : list){
            heap.insert(t);
        }
    }
    @Test
    void TestReHeapify() {
        printAll();
        aE();
        testClass t = list.get(5);
        System.out.println("\nSwitched up " + t.toString());
        t.setTime(222);
        heap.reHeapify(t);
        System.out.println("After hepify ");
        printAll();
        String breakPoint = "Break";
    }

    void aE(){
        for(int i = 0; i < 20; i++){
            assertEquals(this.heap.remove(),this.stackTest.pop());
        }
        reMakeHeap();
    }
    @Test
    void getArrayIndex() {
    }

    @Test
    void doubleArraySize() {
    }
}