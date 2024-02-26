package edu.yu.da.test;

import edu.yu.da.PickAYeshiva;
import edu.yu.da.PickAYeshivaBase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PickAYeshivaTest {

    @Test
    void testFromDoc(){
        double[] facultyRatioRankings = {0,1,2};
        double[] cookingRankings = {3,2,1};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        //print out the arrays
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArrayEquals(new double[]{0,1,2}, pickAYeshiva.getFacultyRatioRankings());
        assertArrayEquals(new double[]{3,2,1}, pickAYeshiva.getCookingRankings());
    }

}