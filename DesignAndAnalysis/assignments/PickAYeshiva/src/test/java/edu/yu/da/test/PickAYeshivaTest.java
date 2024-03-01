package edu.yu.da.test;

import edu.yu.da.PickAYeshiva;
import edu.yu.da.PickAYeshivaBase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class PickAYeshivaTest {

    @Test
    void testFromDoc(){
        double[] facultyRatioRankings = {0,1,2};
        double[] cookingRankings = {3,2,1};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        assertArraysAreEqual(facultyRatioRankings, cookingRankings, pickAYeshiva);
    }

    @Test
    void test1(){
        double[] facultyRatioRankings = {0,1,2,3,4,5,6,7,8,9};
             double[] cookingRankings = {9,8,7,6,5,4,3,2,1,0};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);

        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArraysAreEqual(facultyRatioRankings, cookingRankings, pickAYeshiva);


    }

    @Test
    void testSimpleItDelets(){
        double[] facultyRatioRankings = {10,5};
        double[] cookingRankings = {10,5};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArraysAreEqual(new double[]{10},new double[]{10},pickAYeshiva);;
    }
    @Test
    void testSimpleItDelets2() {
        double[] facultyRatioRankings = {10, 4, 5};
        double[] cookingRankings = {10, 11, 5};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArraysAreEqual(new double[]{10, 4}, new double[]{10, 11}, pickAYeshiva);
    }
    @Test
    void testSimpleItDelets3(){
        double[] facultyRatioRankings = {10,5,4};
        double[] cookingRankings = {10,5,11};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArraysAreEqual(new double[]{10, 4}, new double[]{10, 11}, pickAYeshiva);
    }



    @Test
    void testThatItDeletes(){
        double[] facultyRatioRankings = {10,2,3,2,4,5};
             double[] cookingRankings = {10,1,0,6,11,5};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArraysAreEqual(new double[]{10, 4}, new double[]{10, 11}, pickAYeshiva);
    }

    @Test
    void largeTest(){
        double[] facultyRatioRankings = {6,2,8,9,2,0,8,5,7,1,3};
             double[] cookingRankings = {4,8,1,2,9,3,5,6,7,0,10};

        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArraysAreEqual(new double[]{3,7,8,9}, new double[]{10,7,5,2}, pickAYeshiva);
    }

    @Test
    void testMillion(){
        double[] facultyRatioRankings = new double[1_000_000];
        double[] cookingRankings = new double[1_000_000];


        for(int i = 0, j = cookingRankings.length - 1; i < cookingRankings.length ; i++, j--){
            facultyRatioRankings[i] = i;
            cookingRankings[i] = j;
        }
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings,cookingRankings);
        assertArraysAreEqual(facultyRatioRankings, cookingRankings, pickAYeshiva);

    }
    @Test
    void testFiveMillion(){
        double[] facultyRatioRankings = new double[5_000_000];
        double[] cookingRankings = new double[5_000_000];


        for(int i = 0, j = cookingRankings.length - 1; i < cookingRankings.length ; i++, j--){
            facultyRatioRankings[i] = i;
            cookingRankings[i] = j;
        }
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings,cookingRankings);
        assertArraysAreEqual(facultyRatioRankings, cookingRankings, pickAYeshiva);

    }

    @Test
    void testTenMillion(){
        double[] facultyRatioRankings = new double[10_000_000];
        double[] cookingRankings = new double[10_000_000];


        for(int i = 0, j = cookingRankings.length - 1; i < cookingRankings.length ; i++, j--){
            facultyRatioRankings[i] = i;
            cookingRankings[i] = j;
        }
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings,cookingRankings);
        assertArraysAreEqual(facultyRatioRankings, cookingRankings, pickAYeshiva);
    }

    @Test
    void test20Million(){
        double[] facultyRatioRankings = new double[20_000_000];
        double[] cookingRankings = new double[20_000_000];


        for(int i = 0, j = cookingRankings.length - 1; i < cookingRankings.length ; i++, j--){
            facultyRatioRankings[i] = i;
            cookingRankings[i] = j;
        }
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings,cookingRankings);
        assertArraysAreEqual(facultyRatioRankings, cookingRankings, pickAYeshiva);
    }

    private void assertArraysAreEqual(double[] facultyRatioRankings, double[] cookingRankings, PickAYeshivaBase pickAYeshiva) {
        Arrays.sort(facultyRatioRankings);
        Arrays.sort(cookingRankings);
        double[] facultyRatioRankingsResult = pickAYeshiva.getFacultyRatioRankings();
        double[] cookingRankingsResult = pickAYeshiva.getCookingRankings();
        Arrays.sort(facultyRatioRankingsResult);
        Arrays.sort(cookingRankingsResult);
        assertArrayEquals(facultyRatioRankings, facultyRatioRankingsResult);
        assertArrayEquals(cookingRankings, cookingRankingsResult);
    }


}