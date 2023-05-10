package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.BTree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BTreeImplTest {

    @Test
    public void testPutAndGet() {
        BTree<Integer, String> tree = new BTreeImpl<Integer,String>();
        tree.put(10, "ten");
        tree.put(20, "twenty");
        tree.put(5, "five");
        tree.put(6, "six");
        tree.put(12, "twelve");
        tree.put(30, "thirty");


        assertEquals("ten", tree.get(10));
        assertEquals("twenty", tree.get(20));
        assertEquals("five", tree.get(5));
        assertEquals("six", tree.get(6));
        assertEquals( "twelve", tree.get(12));
        assertEquals( "thirty", tree.get(30));
        assertNull(null, tree.get(7));

        assertEquals("ten",tree.put(10,null));
        assertNull(tree.get(10));
    }

}