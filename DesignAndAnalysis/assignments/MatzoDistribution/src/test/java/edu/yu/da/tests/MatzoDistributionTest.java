package edu.yu.da.tests;

import edu.yu.da.MatzoDistribution;
import edu.yu.da.MatzoDistributionBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MatzoDistributionTest {

    @Test
    void testConstuctorThrows(){
        assertThrows(IllegalArgumentException.class, () -> new MatzoDistribution("", 4, "t"));//sourceWarehouse cannot be empty
        assertThrows(IllegalArgumentException.class, () -> new MatzoDistribution("s", 0, "t"));//sourceConstraint must be positive
        assertThrows(IllegalArgumentException.class, () -> new MatzoDistribution("s", 4, ""));//destinationWarehouse cannot be empty
        assertThrows(IllegalArgumentException.class, () -> new MatzoDistribution("s", 4, "s"));//sourceWarehouse and destinationWarehouse cannot be equal

    }

    @Test
    void testAddWarehouseThrows(){
        MatzoDistributionBase md = new MatzoDistribution("s", 4, "t");
        md.addWarehouse("A", 10);
        assertThrows(IllegalArgumentException.class, () -> md.addWarehouse("", 10));//warehouseId cannot be empty
        assertThrows(IllegalArgumentException.class, () -> md.addWarehouse("B", 0));//constraint must be positive
        assertThrows(IllegalArgumentException.class, () -> md.addWarehouse("A", 10));//warehouseId cannot be added twice
    }

    @Test
    void testRoadExistsThrows(){
        MatzoDistributionBase md = new MatzoDistribution("s", 4, "t");
        md.addWarehouse("A", 10);
        md.roadExists("s", "A", 10);
        assertThrows(IllegalArgumentException.class, () -> md.roadExists("", "A", 10));//w1 cannot be empty
        assertThrows(IllegalArgumentException.class, () -> md.roadExists("A", "", 10));//w2 cannot be empty
        assertThrows(IllegalArgumentException.class, () -> md.roadExists("A", "t", 0));//constraint must be positive
        assertThrows(IllegalArgumentException.class, () -> md.roadExists("A", "A", 10));//w1 and w2 cannot be equal
        assertThrows(IllegalArgumentException.class, () -> md.roadExists("A", "B", 10));//w1 and w2 must be added to the network
        assertThrows(IllegalArgumentException.class, () -> md.roadExists("B", "A", 10));//w1 and w2 must be added to the network
    }

    @Test
    void testFromDoc(){
        MatzoDistributionBase md = new MatzoDistribution("s", 4, "t");
        md.addWarehouse("A", 10);
        md.roadExists("s", "A", 10);
        md.roadExists("A", "t", 10);
        assertEquals(4, md.max());
    }

    @Test
    void test1(){
        MatzoDistributionBase md = new MatzoDistribution("s", 33, "t");
        md.addWarehouse("A", 4);
        md.addWarehouse("B", 6);
        md.addWarehouse("C", 3);
        md.addWarehouse("D", 9);
        md.addWarehouse("E", 5);
        md.addWarehouse("F", 4);

        md.roadExists("s", "A", 11);
        md.roadExists("s", "B", 9);
        md.roadExists("s", "E", 7);
        md.roadExists("A", "C", 3);
        md.roadExists("A", "F", 2);
        md.roadExists("B", "D", 5);
        md.roadExists("C", "t", 12);
        md.roadExists("D", "t", 13);
        md.roadExists("E", "D", 6);
        md.roadExists("F", "C", 6);

        assertEquals(12, md.max());
    }
}
