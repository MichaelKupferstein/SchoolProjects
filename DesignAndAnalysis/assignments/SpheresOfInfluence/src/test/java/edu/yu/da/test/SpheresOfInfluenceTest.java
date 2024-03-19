package edu.yu.da.test;

import edu.yu.da.SpheresOfInfluence;
import edu.yu.da.SpheresOfInfluenceBase;
import org.junit.jupiter.api.Test;

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
}