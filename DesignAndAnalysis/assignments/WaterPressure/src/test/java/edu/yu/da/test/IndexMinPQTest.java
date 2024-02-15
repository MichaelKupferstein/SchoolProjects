package edu.yu.da.test;

import edu.yu.da.IndexMinPQ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IndexMinPQTest {

    private IndexMinPQ<Integer> indexMinPQ;

    @BeforeEach
    void setUp() {
        indexMinPQ = new IndexMinPQ<>(10);
    }

    @Test
    void testIsEmpty() {
        assertTrue(indexMinPQ.isEmpty());
        indexMinPQ.insert("A", 1);
        assertFalse(indexMinPQ.isEmpty());
    }

    @Test
    void testContains() {
        assertFalse(indexMinPQ.contains("A"));
        indexMinPQ.insert("A", 1);
        assertTrue(indexMinPQ.contains("A"));
    }

    @Test
    void testSize() {
        assertEquals(0, indexMinPQ.size());
        indexMinPQ.insert("A", 1);
        assertEquals(1, indexMinPQ.size());
    }

    @Test
    void testInsertAndMinIndex() {
        indexMinPQ.insert("A", 1);
        indexMinPQ.insert("B", 2);
        assertEquals("A", indexMinPQ.minIndex());
    }

    @Test
    void testDelMin() {
        indexMinPQ.insert("A", 1);
        indexMinPQ.insert("B", 2);
        assertEquals("A", indexMinPQ.delMin());
        assertEquals(1, indexMinPQ.size());
    }

    @Test
    void testMinKey() {
        indexMinPQ.insert("A", 1);
        indexMinPQ.insert("B", 2);
        assertEquals(1, indexMinPQ.minKey());
    }

    @Test
    void testChangeKey() {
        indexMinPQ.insert("A", 1);
        indexMinPQ.changeKey("A", 3);
        assertEquals(3, indexMinPQ.keyOf("A"));
    }

    @Test
    void testIncreaseKey() {
        indexMinPQ.insert("A", 1);
        indexMinPQ.increaseKey("A", 3);
        assertEquals(3, indexMinPQ.keyOf("A"));
    }

    @Test
    void testDecreaseKey() {
        indexMinPQ.insert("A", 3);
        indexMinPQ.decreaseKey("A", 1);
        assertEquals(1, indexMinPQ.keyOf("A"));
    }

    @Test
    void testDelete() {
        indexMinPQ.insert("A", 1);
        indexMinPQ.delete("A");
        assertFalse(indexMinPQ.contains("A"));
    }
}