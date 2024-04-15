package edu.yu.da.tests;

import edu.yu.da.MatzoDistribution;
import edu.yu.da.MatzoDistributionBase;
import org.junit.jupiter.api.Test;

public class MatzoDistributionTest {

    @Test
    void testFromDoc(){
        MatzoDistributionBase md = new MatzoDistribution("s", 4, "t");
        md.addWarehouse("A", 10);
        md.roadExists("s", "A", 10);
        md.roadExists("A", "t", 10);
        System.out.println(md.max());
    }
}
