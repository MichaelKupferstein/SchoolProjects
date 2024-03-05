package edu.yu.da;

import java.util.*;

public class PickAYeshiva extends PickAYeshivaBase{

    private Set<Yeshiva> yeshivaSet;
    private List<Yeshiva> yeshivaList;
    private double[] facultyRatioRankings;
    private double[] cookingRankings;

    public PickAYeshiva(double[] facultyRatioRankings, double[] cookingRankings) {
        super(facultyRatioRankings, cookingRankings);
        if(facultyRatioRankings == null || cookingRankings == null) throw new IllegalArgumentException("Arrays must be non-null");
        if(facultyRatioRankings.length == 0 || cookingRankings.length == 0) throw new IllegalArgumentException("Array lengths must be > 0");
        if(facultyRatioRankings.length != cookingRankings. length) throw new IllegalArgumentException("Arrays must be equal in length");

        this.yeshivaSet = new HashSet<>();
        this.yeshivaList = new ArrayList<>();

        for(int i = 0; i < facultyRatioRankings.length; i++){
            Yeshiva temp = new Yeshiva(facultyRatioRankings[i], cookingRankings[i]);
            if(!yeshivaSet.add(temp)) throw new IllegalArgumentException("Arrays can't contain duplicates");
            yeshivaList.add(temp);
        }

        Collections.sort(yeshivaList,Comparator.reverseOrder());

        this.yeshivaList = divideAndConquer(this.yeshivaList);
        //this.yeshivaList = notDC(this.yeshivaList);

        finishUp();
    }
    private void finishUp(){
        this.facultyRatioRankings = new double[yeshivaList.size()];
        this.cookingRankings = new double[yeshivaList.size()];
        for(int i = 0; i < yeshivaList.size(); i++){
            this.facultyRatioRankings[i] = yeshivaList.get(i).getFacultyRatioRanking();
            this.cookingRankings[i] = yeshivaList.get(i).getCookingRanking();
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
    private List<Yeshiva> divideAndConquer(List<Yeshiva> yeshivas) {
        if (yeshivas.size() == 1) {
            return yeshivas;
        }

        int mid = yeshivas.size() / 2;

        List<Yeshiva> leftHalf = divideAndConquer(yeshivas.subList(0, mid));
        List<Yeshiva> rightHalf = divideAndConquer(yeshivas.subList(mid, yeshivas.size()));

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

    @Override
    public double[] getFacultyRatioRankings() {
        return facultyRatioRankings;
    }

    @Override
    public double[] getCookingRankings() {
        return cookingRankings;
    }
}