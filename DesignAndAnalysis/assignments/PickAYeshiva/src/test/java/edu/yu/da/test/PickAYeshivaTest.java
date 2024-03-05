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
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArraysAreEqual(facultyRatioRankings, cookingRankings, pickAYeshiva);
    }

    @Test
    void test1(){
        double[] facultyRatioRankings = {0,1,2,3,4,5,6,7,8,9};
             double[] cookingRankings = {10,9,8,7,6,5,4,3,2,1};
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
    void largeRandomTest(){
        double[] facultyRatioRankings = {7.0, 40.0, 12.0, 11.0, 27.0, 31.0, 23.0, 26.0, 17.0, 46.0, 14.0, 33.0, 5.0, 19.0};
        double[] cookingRankings =      {1.0, 45.0, 5.0, 12.0, 31.0, 3.0, 24.0, 5.0, 10.0, 38.0, 49.0, 30.0, 19.0, 29.0};

        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));
        assertArraysAreEqual(new double[]{14,40,46}, new double[]{49,45,38}, pickAYeshiva);

    }
    @Test
    void largeRandomTest2(){
        double[] facultyRatioRankings = {21.0, 1.0, 23.0, 16.0, 29.0, 48.0, 20.0, 24.0, 25.0, 34.0, 7.0, 43.0, 0.0, 26.0, 40.0};
        double[] cookingRankings =      {17.0, 39.0, 13.0, 21.0, 1.0, 10.0, 31.0, 21.0, 14.0, 17.0, 34.0, 36.0, 14.0, 16.0, 15.0};

        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        System.out.println(Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println(Arrays.toString(pickAYeshiva.getCookingRankings()));

        assertArraysAreEqual(new double[]{1,43,48}, new double[]{39,36,10}, pickAYeshiva);
    }

    @Test
    void testWithSameFacultyRatio(){
        double[] facultyRatioRankings = {1,1,1,1,1,1,1,1,1,1};
        double[] cookingRankings = {10,9,8,7,6,5,4,3,2,1};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        assertArraysAreEqual(new double[]{1}, new double[]{10}, pickAYeshiva);
    }
    @Test
    void testWithSameCooking(){
        double[] facultyRatioRankings = {10,9,8,7,6,5,4,3,2,1};
        double[] cookingRankings = {1,1,1,1,1,1,1,1,1,1};
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings, cookingRankings);
        assertArraysAreEqual(new double[]{10}, new double[]{1}, pickAYeshiva);
    }



    @Test
    void testMillion(){
        testN(1_000_000);
    }


    @Test
    void testFiveMillion(){
        testN(5_000_000);
    }

    @Test
    void testTenMillion(){
        testN(10_000_000);
    }

    @Test
    void test20Million(){
        testN(20_000_000);
    }

    @Test
    void test30Million() {
        testN(30_000_000);
    }

    void testN(int N){
        double[] facultyRatioRankings = new double[N];
        double[] cookingRankings = new double[N];

        for(int i = 0, j = cookingRankings.length; i < cookingRankings.length ; i++, j--){
            facultyRatioRankings[i] = i;
            cookingRankings[i] = j;
        }
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings,cookingRankings);
        assertArraysAreEqual(facultyRatioRankings, cookingRankings, pickAYeshiva);
    }

    @Test
    void randomTest(){
        double[] facultyRatioRankings = new double[50];
        double[] cookingRankings = new double[50];
        for(int i = 0; i < facultyRatioRankings.length; i++){
            facultyRatioRankings[i] = Math.round(Math.random() * 50);
            cookingRankings[i] = Math.round(Math.random() * 50);
        }
        PickAYeshivaBase pickAYeshiva = new PickAYeshiva(facultyRatioRankings,cookingRankings);

        System.out.println("Inital facultyRatioRankings: " + Arrays.toString(facultyRatioRankings));
        System.out.println("Inital cookingRankings:      " + Arrays.toString(cookingRankings));

        System.out.println("Post facultyRationRankings: " +Arrays.toString(pickAYeshiva.getFacultyRatioRankings()));
        System.out.println("Post cookingRankings:       " +Arrays.toString(pickAYeshiva.getCookingRankings()));
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