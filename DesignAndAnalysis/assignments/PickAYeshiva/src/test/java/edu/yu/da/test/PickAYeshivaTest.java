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
        assertArrayEquals(facultyRatioRankings, pickAYeshiva.getFacultyRatioRankings());
        assertArrayEquals(cookingRankings, pickAYeshiva.getCookingRankings());
    }

    @Test
    void test1(){
        double[] facultyRatioRankings = {0,1,2,3,4,5,6,7,8,9};
        double[] cookingRankings = {9,8,7,6,5,4,3,2,1,0};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        assertArrayEquals(facultyRatioRankings, pickAYeshiva.getFacultyRatioRankings());
        assertArrayEquals(cookingRankings, pickAYeshiva.getCookingRankings());
    }

    @Test
    void testSimpleItDelets(){
        double[] facultyRatioRankings = {10,5};
        double[] cookingRankings = {10,5};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        assertArrayEquals(new double[]{10}, pickAYeshiva.getFacultyRatioRankings());
        assertArrayEquals(new double[]{10}, pickAYeshiva.getCookingRankings());
    }
    @Test
    void testSimpleItDelets2(){
        double[] facultyRatioRankings = {10,4,5};
        double[] cookingRankings = {10,11,5};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        assertArrayEquals(new double[]{10,4}, pickAYeshiva.getFacultyRatioRankings());
        assertArrayEquals(new double[]{10,11}, pickAYeshiva.getCookingRankings());
    }
    @Test
    void testSimpleItDelets3(){
        double[] facultyRatioRankings = {10,5,4};
        double[] cookingRankings = {10,5,11};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        assertArrayEquals(new double[]{10,4}, pickAYeshiva.getFacultyRatioRankings());
        assertArrayEquals(new double[]{10,11}, pickAYeshiva.getCookingRankings());
    }



    @Test //TODO: fix this
    void testThatItDeletes(){ //FALES
        double[] facultyRatioRankings = {10,2,3,2,4,5};
             double[] cookingRankings = {10,1,0,6,11,5};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArrayEquals(new double[]{10,4}, pickAYeshiva.getFacultyRatioRankings());
        assertArrayEquals(new double[]{10,11}, pickAYeshiva.getCookingRankings());
    }

}