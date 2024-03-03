package edu.yu.da;

import java.util.*;

public class PickAYeshiva extends PickAYeshivaBase{

    private Set<Yeshiva> yeshivaSet;
    private Yeshiva[] yeshivaArray;
    private double[] facultyRatioRankings;
    private double[] cookingRankings;

    public PickAYeshiva(double[] facultyRatioRankings, double[] cookingRankings) {
        super(facultyRatioRankings, cookingRankings);
        if(facultyRatioRankings == null || cookingRankings == null) throw new IllegalArgumentException("Arrays must be non-null");
        if(facultyRatioRankings.length == 0 || cookingRankings.length == 0) throw new IllegalArgumentException("Array lengths must be > 0");
        if(facultyRatioRankings.length != cookingRankings. length) throw new IllegalArgumentException("Arrays must be equal in length");

        this.yeshivaSet = new HashSet<>();
        this.yeshivaArray = new Yeshiva[facultyRatioRankings.length];

        for(int i = 0; i < facultyRatioRankings.length; i++){
            Yeshiva temp = new Yeshiva(facultyRatioRankings[i], cookingRankings[i]);
            if(!yeshivaSet.add(temp)) throw new IllegalArgumentException("Arrays can't contain duplicates");
            yeshivaArray[i] = temp;
        }

        Arrays.sort(yeshivaArray);

        divideAndConquer(yeshivaArray, 0, yeshivaArray.length);

        int nullIndex = findFirstNull(yeshivaArray);
        this.facultyRatioRankings = new double[nullIndex];
        this.cookingRankings = new double[nullIndex];
        for(int i = 0; i < nullIndex; i++){
            if(yeshivaArray[i] == null) break;
            this.facultyRatioRankings[i] = yeshivaArray[i].getFacultyRatioRanking();
            this.cookingRankings[i] = yeshivaArray[i].getCookingRanking();
        }
    }
    private int findFirstNull(Yeshiva[] yeshivaArray){
        for(int i = 0; i < yeshivaArray.length; i++){
            if(yeshivaArray[i] == null) return i;
        }
        return yeshivaArray.length;
    }

    private void divideAndConquer(Yeshiva[] yeshivaArray, int start, int end){
        if(end - start <= 1) {
            return;
        }

        int mid = start + (end - start) / 2;
        divideAndConquer(yeshivaArray, start, mid);
        divideAndConquer(yeshivaArray, mid, end);
        merge(yeshivaArray, start, mid, end);
    }

    private void merge(Yeshiva[] yeshivaArray, int start, int mid, int end){
        Yeshiva[] temp = new Yeshiva[end - start];
        int leftIndex = start;
        int rightIndex = mid;
        int tempIndex = 0;

        while(leftIndex < mid && rightIndex < end){
            if(yeshivaArray[leftIndex] == null) break;
            int check = yeshivaArray[leftIndex].check(yeshivaArray[rightIndex]);
            if(check == 1) temp[tempIndex++] = yeshivaArray[leftIndex++];
            else if(check == -1) temp[tempIndex++] = yeshivaArray[rightIndex++];
            else{
                temp[tempIndex++] = yeshivaArray[leftIndex++];
                temp[tempIndex++] = yeshivaArray[rightIndex++];
            }
        }

        while(rightIndex < end){
            temp[tempIndex++] = yeshivaArray[rightIndex++];
        }

        System.arraycopy(temp, 0, yeshivaArray, start, temp.length);
    }

    @Override
    public double[] getFacultyRatioRankings() {
        return facultyRatioRankings;
    }

    @Override
    public double[] getCookingRankings() {
        return cookingRankings;
    }
}