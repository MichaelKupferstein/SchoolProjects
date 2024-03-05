package edu.yu.da;

import java.util.*;

public class PickAYeshiva extends PickAYeshivaBase{

    private Set<Yeshiva> yeshivaSet;
    //private List<Yeshiva> yeshivaList;
    private Yeshiva[] yeshivaArray;
    private double[] facultyRatioRankings;
    private double[] cookingRankings;

    public PickAYeshiva(double[] facultyRatioRankings, double[] cookingRankings) {
        super(facultyRatioRankings, cookingRankings);
        if(facultyRatioRankings == null || cookingRankings == null) throw new IllegalArgumentException("Arrays must be non-null");
        if(facultyRatioRankings.length == 0 || cookingRankings.length == 0) throw new IllegalArgumentException("Array lengths must be > 0");
        if(facultyRatioRankings.length != cookingRankings. length) throw new IllegalArgumentException("Arrays must be equal in length");

        this.yeshivaSet = new HashSet<>();
        //this.yeshivaList = new ArrayList<>();
        this.yeshivaArray = new Yeshiva[facultyRatioRankings.length];

        for(int i = 0; i < facultyRatioRankings.length; i++){
            Yeshiva temp = new Yeshiva(facultyRatioRankings[i], cookingRankings[i]);
            //if(!yeshivaSet.add(temp)) throw new IllegalArgumentException("Arrays can't contain duplicates");
            //yeshivaList.add(temp);
            yeshivaArray[i] = temp;
        }

        Arrays.parallelSort(yeshivaArray,Comparator.reverseOrder());
        //Collections.sort(yeshivaList,Comparator.reverseOrder());

        //this.yeshivaList = divideAndConquer(this.yeshivaList);
        //this.yeshivaList = notDC(this.yeshivaList);
        this.yeshivaArray = divideAndConquer(this.yeshivaArray);

        finishUp();
    }
    private void finishUp(){
        this.facultyRatioRankings = new double[yeshivaArray.length];
        this.cookingRankings = new double[yeshivaArray.length];
        for(int i = 0; i < yeshivaArray.length; i++){
            facultyRatioRankings[i] = yeshivaArray[i].getFacultyRatioRanking();
            cookingRankings[i] = yeshivaArray[i].getCookingRanking();
        }
    }

    private List<Yeshiva> notDC(List<Yeshiva> yeshivas){
        List<Yeshiva> res = new ArrayList<>();
        double bestCooking = Double.MIN_VALUE;
        for(Yeshiva yeshiva : yeshivas){
            if(yeshiva.getCookingRanking() > bestCooking){
                bestCooking = yeshiva.getCookingRanking();
                res.add(yeshiva);
            }
        }
        return res;
    }

    private Yeshiva[] divideAndConquer(Yeshiva[] yeshivas) {
        if (yeshivas.length == 1) {
            return yeshivas;
        }

        int mid = yeshivas.length / 2;

        Yeshiva[] left = divideAndConquer(Arrays.copyOfRange(yeshivas, 0, mid));
        Yeshiva[] right = divideAndConquer(Arrays.copyOfRange(yeshivas, mid, yeshivas.length));

        List<Yeshiva> res = new ArrayList<>(yeshivas.length);
        double bestCooking = Double.MIN_VALUE;
        for(Yeshiva yeshiva : yeshivas){
            if(yeshiva.getCookingRanking() > bestCooking){
                bestCooking = yeshiva.getCookingRanking();
                res.add(yeshiva);
            }
        }
        return res.toArray(new Yeshiva[res.size()]);
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