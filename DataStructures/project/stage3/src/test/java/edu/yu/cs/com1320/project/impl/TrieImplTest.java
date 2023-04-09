package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
public class TrieImplTest {
    TrieImpl<Integer> test;

    @BeforeEach
    void setUp() {
        this.test = new TrieImpl<>();
        this.test.put("Hello",420);
        this.test.put("Hello", 69);
        this.test.put("Hell",666);
        this.test.put("Hell",203);
        this.test.put("Help", 911);
        this.test.put("He", 12);
        this.test.put("hello",42);
        this.test.put("hell",66);
        this.test.put("Too",123);
        this.test.put("Tooth",456);
        this.test.put("Toot",789);
        this.test.put("Toodle",101);
        this.test.put("Tool",234);
        this.test.put("Too", 12345);
    }

    @Test
    void intialTest() {
        List<Integer> testList = new ArrayList<>();
        testList.add(420);
        testList.add(69);
        assertEquals(testList,this.test.getAllSorted("Hello", Collections.reverseOrder()));
        testList.add(666);
        testList.add(203);
        testList.add(911);
        testList.add(12);
        testList.sort(Collections.reverseOrder());
        assertEquals(testList,this.test.getAllWithPrefixSorted("He",Collections.reverseOrder()));
        List<Integer> testList2 = new ArrayList<>();
        testList2.add(420);
        testList2.add(69);
        testList2.add(666);
        testList2.add(203);
        testList2.sort(Collections.reverseOrder());
        assertEquals(testList2, this.test.getAllWithPrefixSorted("Hell",Collections.reverseOrder()));
    }

    @Test
    void prefixTest() {
        List<Integer> testList = new ArrayList<>();
        testList.addAll(Arrays.asList(123,456,789,101,234,12345));
        testList.sort(Collections.reverseOrder());
        assertEquals(testList, this.test.getAllWithPrefixSorted("Too", Collections.reverseOrder()));

    }

    @Test
    void testingDeleteAll() {
        Set<Integer> testSet = new HashSet<>();
        testSet.addAll(Arrays.asList(123,456,789,101,234,12345));
        assertEquals(testSet, this.test.deleteAllWithPrefix("Too"));
        assertEquals(Collections.emptyList(),this.test.getAllWithPrefixSorted("Too",Collections.reverseOrder()));
        assertEquals(Collections.emptyList(), this.test.getAllSorted("Toot", Collections.reverseOrder()));
        assertEquals(Collections.emptyList(), this.test.getAllSorted("Too", Collections.reverseOrder()));
    }

    @Test
    void deleteAll() {
        Set<Integer> testSet = new HashSet<>();
        testSet.addAll(Arrays.asList(123,12345));
        assertEquals(testSet, this.test.deleteAll("Too"));
        assertEquals(Collections.emptyList(),this.test.getAllSorted("Too",Collections.reverseOrder()));
        List<Integer> testList = new ArrayList<>();
        testList.add(456);
        assertEquals(testList, this.test.getAllSorted("Tooth", Collections.reverseOrder() ));
    }

    @Test
    void delete() {
        List<Integer> testList = new ArrayList<>();
        testList.addAll(Arrays.asList(12345,123));
        assertEquals(testList,this.test.getAllSorted("Too",Collections.reverseOrder()));
        assertEquals(123, this.test.delete("Too", 123));
        testList.remove(1);
        assertEquals(testList,this.test.getAllSorted("Too",Collections.reverseOrder()));
    }

    @Test
    void deleteTest2(){
        List<Integer> testList = new ArrayList<>();
        testList.addAll(Arrays.asList(12345,123));
        assertEquals(testList,this.test.getAllSorted("Too",Collections.reverseOrder()));
        assertEquals(123, this.test.delete("Too", 123));
        testList.remove(1);
        assertNull(this.test.delete("Too",123));
    }

    @Test
    void deleteTest3(){
        this.test.put("Apple", 456);
        this.test.put("App", 789);
        this.test.put("Apples", 123);
        this.test.put("Apendix", 1011);
        //System.out.print(this.test.getAllWithPrefixSorted("Ap",Collections.reverseOrder()));
        this.test.delete("App",789);
        //System.out.print("\n" + this.test.getAllWithPrefixSorted("Ap",Collections.reverseOrder()));
        this.test.delete("Apendix", 1011);
        this.test.delete("Apples", 123);
        this.test.delete("Apple", 456);
        //this.test.deleteAllWithPrefix("App");
        //System.out.print("\n" + this.test.getAllWithPrefixSorted("Ap",Collections.reverseOrder()));

    }

    @Test
    void isEmptyTest(){
        this.test.put("Test",402);
        this.test.put("Test",403);
        this.test.delete("Test",402);
        this.test.delete("Test",403);
        this.test.put("Abc",123);
        String breakpoint = "Test";
    }

}
