package edu.yu.da.tests;

import edu.yu.da.ThereAndBackAgain;
import edu.yu.da.ThereAndBackAgainBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThereAndBackAgainTest {

    @Test
    void TestFromDoc(){
        final String startVertex = "a";
        final ThereAndBackAgainBase taba = new ThereAndBackAgain(startVertex);
        taba.addEdge(startVertex,"b",1.0);
        taba.addEdge("b","c",2.0);
        taba.doIt();
        assertEquals(null,taba.goalVertex(),"goalVertex");
        assertEquals(0.0,taba.goalCost(),"goalCost");
        assertEquals(null,taba.getOneLongestPath());
        assertEquals(null,taba.getOtherLongestPath());
    }

}