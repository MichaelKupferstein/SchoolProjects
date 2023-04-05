package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    }

    @Test
    void put() {
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
    void getAllSorted() {
    }

    @Test
    void getAllWithPrefixSorted() {
    }

    @Test
    void deleteAllWithPrefix() {
    }

    @Test
    void deleteAll() {
    }

    @Test
    void delete() {
    }

}
