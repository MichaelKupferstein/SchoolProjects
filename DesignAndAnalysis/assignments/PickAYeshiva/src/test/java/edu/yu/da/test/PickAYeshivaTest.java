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
        assertArrayEquals(new double[]{0,1,2}, pickAYeshiva.getFacultyRatioRankings());
        assertArrayEquals(new double[]{3,2,1}, pickAYeshiva.getCookingRankings());
    }

    @Test
    void test1(){
        double[] facultyRatioRankings = {0,1,2,3,4,5,6,7,8,9};
             double[] cookingRankings = {9,8,7,6,5,4,3,2,1,0};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        assertArrayEquals(new double[]{0,1,2,3,4,5,6,7,8,9}, pickAYeshiva.getFacultyRatioRankings());
        assertArrayEquals(new double[]{9,8,7,6,5,4,3,2,1,0}, pickAYeshiva.getCookingRankings());
    }

    @Test
    void testSimpleItDelets(){
        double[] facultyRatioRankings = {10,5};
        double[] cookingRankings = {10,5};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArrayEquals(new double[]{10}, pickAYeshiva.getFacultyRatioRankings());
        assertArrayEquals(new double[]{10}, pickAYeshiva.getCookingRankings());
    }
    @Test
    void testSimpleItDelets2(){
        double[] facultyRatioRankings = {10,4,5};
        double[] cookingRankings = {10,11,5};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArrayEquals(new double[]{4,10}, pickAYeshiva.getFacultyRatioRankings());
        assertArrayEquals(new double[]{11,10}, pickAYeshiva.getCookingRankings());
    }
    @Test
    void testSimpleItDelets3(){
        double[] facultyRatioRankings = {10,5,4};
        double[] cookingRankings = {10,5,11};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArrayEquals(new double[]{4,10}, pickAYeshiva.getFacultyRatioRankings());
        assertArrayEquals(new double[]{11,10}, pickAYeshiva.getCookingRankings());
    }



    @Test
    void testThatItDeletes(){
        double[] facultyRatioRankings = {10,2,3,2,4,5};
             double[] cookingRankings = {10,1,0,6,11,5};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArrayEquals(new double[]{4,10}, pickAYeshiva.getFacultyRatioRankings());
        assertArrayEquals(new double[]{11,10}, pickAYeshiva.getCookingRankings());
    }

    @Test
    void largeTest(){
        double[] facultyRatioRankings = {6,2,8,9,2,0,8,5,7,1,3};
             double[] cookingRankings = {4,8,1,2,9,3,5,6,7,0,10};

        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArrayEquals(new double[]{3,7,8,9}, pickAYeshiva.getFacultyRatioRankings());
        assertArrayEquals(new double[]{10,7,5,2}, pickAYeshiva.getCookingRankings());

    }

}