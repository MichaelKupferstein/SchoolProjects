package edu.yu.da.test;

import edu.yu.da.SpheresOfInfluence;
import edu.yu.da.SpheresOfInfluenceBase;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpheresOfInfluenceTest {


    @Test
    void testConstuctorThrows(){
        assertThrows(IllegalArgumentException.class, () -> new SpheresOfInfluence(0, 0));
        assertThrows(IllegalArgumentException.class, () -> new SpheresOfInfluence(1, 0));
        assertThrows(IllegalArgumentException.class, () -> new SpheresOfInfluence(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new SpheresOfInfluence(-1, 1));
        assertThrows(IllegalArgumentException.class, () -> new SpheresOfInfluence(1, -1));
    }

    @Test
    void testAddInfluencerThrows(){
        SpheresOfInfluenceBase soi = new SpheresOfInfluence(2,10);
        assertThrows(IllegalArgumentException.class, () -> soi.addInfluencer("", 2, 3));
        assertThrows(IllegalArgumentException.class, () -> soi.addInfluencer("A", -1, 3));
        assertThrows(IllegalArgumentException.class, () -> soi.addInfluencer("A", 2, -1));
        soi.addInfluencer("A", 2, 3);
        assertThrows(IllegalArgumentException.class, () -> soi.addInfluencer("A", 2, 3));
        assertThrows(IllegalArgumentException.class, () -> soi.addInfluencer("B", 2, 3));
    }

    @Test
    void testFromDoc(){
        SpheresOfInfluenceBase soi = new SpheresOfInfluence(2,10);
        soi.addInfluencer("A", 2, 3);
        soi.addInfluencer("B", 6, 5);
        assertEquals(List.of("A", "B"), soi.getMinimalCoverageInfluencers());
    }

    @Test
    void test2(){
        SpheresOfInfluenceBase soi = new SpheresOfInfluence(2,10);
        soi.addInfluencer("A", 2, 3);
        soi.addInfluencer("B", 6, 5);
        soi.addInfluencer("C", 0, 1);
        assertEquals(List.of("A", "B"), soi.getMinimalCoverageInfluencers());
    }

    @Test
    void test3(){
        SpheresOfInfluenceBase soi = new SpheresOfInfluence(2,10);
        soi.addInfluencer("A", 2, 3);
        soi.addInfluencer("B", 6, 5);
        soi.addInfluencer("C", 0, 1);
        soi.addInfluencer("D",  1, 1);
        assertEquals(List.of("A", "B"), soi.getMinimalCoverageInfluencers());
    }

    @Test
    void test4(){
        SpheresOfInfluenceBase soi = new SpheresOfInfluence(10,10);
        soi.addInfluencer("A", 0, 11);
        assertEquals(Collections.EMPTY_LIST, soi.getMinimalCoverageInfluencers());
    }

    @Test
    void test5(){
        SpheresOfInfluenceBase soi = new SpheresOfInfluence(10,10);
        soi.addInfluencer("A", 2, 3);
        soi.addInfluencer("B", 6, 5);
        soi.addInfluencer("C", 5, 8);
        assertEquals(List.of( "C"), soi.getMinimalCoverageInfluencers());
    }

    @Test
    void test6(){
        SpheresOfInfluenceBase soi = new SpheresOfInfluence(10,10);
        soi.addInfluencer("A", 0, 6);
        soi.addInfluencer("B", 6, 6);
        soi.addInfluencer("C", 8, 6);
        assertEquals(List.of("A","B","C"), soi.getMinimalCoverageInfluencers());
    }

    @Test
    void test7(){
        SpheresOfInfluenceBase soi = new SpheresOfInfluence(10,10);
        soi.addInfluencer("A", 0, 6);
        soi.addInfluencer("B", 6, 6);
        soi.addInfluencer("C", 8, 6);
        soi.addInfluencer("D", 9, 1);
        assertEquals(List.of("A","B","C"), soi.getMinimalCoverageInfluencers());
    }
    @Test
    void testWithTooSmall(){
        SpheresOfInfluenceBase soi = new SpheresOfInfluence(10,10);
        soi.addInfluencer("A", 3, 4);
        soi.addInfluencer("B", 2, 3);
        soi.addInfluencer("C", 8, 5);
        soi.addInfluencer("D", 5, 2);
        assertEquals(Collections.EMPTY_LIST, soi.getMinimalCoverageInfluencers());
    }

    @Test
    void testWith8000(){
        SpheresOfInfluenceBase soi = new SpheresOfInfluence(8000,8000);
        for(int i = 0; i < 8000; i++){
            int r = (int) (Math.random() * 4500) + 1;
            int x = (int) (Math.random() * 8000);
            soi.addInfluencer("A" + i, x, r);
        }
        System.out.println(soi.getMinimalCoverageInfluencers());
    }



}