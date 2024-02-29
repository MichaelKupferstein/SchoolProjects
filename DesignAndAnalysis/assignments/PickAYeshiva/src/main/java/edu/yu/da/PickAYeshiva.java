package edu.yu.da;

import java.util.*;

public class PickAYeshiva extends PickAYeshivaBase{

    private Set<Yeshiva> yeshivaSet;
    private List<Yeshiva> yeshivaList;
    private double[] facultyRatioRankings;
    private double[] cookingRankings;
    private Yeshiva highest;
    /**
     * Constructor which supplies the yeshiva rankings in terms of two factors
     * of interest.  The constructor executes a divide-and-conquer algorithm to
     * determine the minimum number of yeshiva-to-yeshiva comparisons required to
     * make a "which yeshiva to attend" decision.  The getters can be accessed in
     * O(1) time after the constructor executes successfully.
     * <p>
     * It is the client's responsibility to ensure that no pair of
     * facultyRatioRankings and cookingRankings values are duplicates.
     *
     * @param facultyRatioRankings Array whose ith element is the value of the
     *                             ith yeshiva with respect to its faculty-to-student ratio (Rabbeim etc).
     *                             Client maintains ownership.  Can't be null and must be same length as the
     *                             other parameter.
     * @param cookingRankings      Array whose ith element is the value of the ith
     *                             yeshiva with respect to the quality of the cooking.  Client maintains
     *                             ownership.  Can't be null and must be same length as other parameter.
     * @throws IllegalArgumentException if pre-conditions are violated.
     */
    public PickAYeshiva(double[] facultyRatioRankings, double[] cookingRankings) {
        super(facultyRatioRankings, cookingRankings);
        if(facultyRatioRankings == null || cookingRankings == null) throw new IllegalArgumentException("Arrays must be non-null");
        if(facultyRatioRankings.length == 0 || cookingRankings.length == 0) throw new IllegalArgumentException("Array lengths must be > 0");
        if(facultyRatioRankings.length != cookingRankings. length) throw new IllegalArgumentException("Arrays must be equal in length");

        this.yeshivaSet = new HashSet<>();//might remove from here
        this.yeshivaList = new ArrayList<>();
        this.highest = null;

        for(int i = 0; i < facultyRatioRankings.length; i++){
            Yeshiva temp = new Yeshiva(facultyRatioRankings[i], cookingRankings[i]);
            if(!yeshivaSet.add(temp)) throw new IllegalArgumentException("Arrays can't contain duplicates");
            yeshivaList.add(temp);
            if(this.highest == null || temp.check(this.highest) == 1){
                this.highest = temp;
            }
        }//to here

        Collections.sort(yeshivaList, Comparator.reverseOrder());
        divideAndConquer(yeshivaList);

        this.facultyRatioRankings = new double[yeshivaList.size()];
        this.cookingRankings = new double[yeshivaList.size()];
        for(int i = 0; i < yeshivaList.size(); i++){
            this.facultyRatioRankings[i] = yeshivaList.get(i).getFacultyRatioRanking();
            this.cookingRankings[i] = yeshivaList.get(i).getCookingRanking();
        }

    }

    private void divideAndConquer(List<Yeshiva> yeshivaList){
        if(yeshivaList.size() <= 1) {
            return;
        }

        int mid = yeshivaList.size() / 2;
        List<Yeshiva> left = new ArrayList<>(yeshivaList.subList(0, mid));
        List<Yeshiva> right = new ArrayList<>(yeshivaList.subList(mid, yeshivaList.size()));
        divideAndConquer(left);
        divideAndConquer(right);
        //System.out.println(yeshivaList);
        merge(left, right, yeshivaList);

    }

    private void merge(List<Yeshiva> left, List<Yeshiva> right, List<Yeshiva> yeshivaList){
        int leftIndex = 0;
        int rightIndex = 0;
        while(leftIndex < left.size() && rightIndex < right.size()){
            int check = left.get(leftIndex).check(right.get(rightIndex));
            if(check == 1){
                right.remove(rightIndex); // remove the Yeshiva from the right list
            }else if(check == -1){
                left.remove(leftIndex); // remove the Yeshiva from the left list
            }else{
                leftIndex++;
                rightIndex++;
            }
        }
        yeshivaList.clear();
        yeshivaList.addAll(left);
        yeshivaList.addAll(right);
    }

    /**
     * Returns an array of yeshiva faculty ranking ratio values that MUST be
     * evaluated (along with the yeshiva's cooking rankings) to make the best
     * "which yeshiva to attend" decision.
     *
     * @return An array, that together with the other getter, represents the
     * MINIMUM set of yeshivos that must be evaluated.  The ith element of this
     * array MUST BE associated with the ith element of the other getter's array.
     * @see getCookingRankings
     */
    @Override
    public double[] getFacultyRatioRankings() {
        return facultyRatioRankings;
    }

    /**
     * Returns an array of yeshiva cooking ranking values that MUST be evaluated
     * (along with the yeshiva's faculty ratio rankings) to make the best "which
     * yeshiva to attend" decision.
     *
     * @return An array, that together with the other getter, represents the
     * MINIMUM set of yeshivos that must be evaluated.  The ith element of this
     * array MUST BE associated with the ith element of the other getter's array.
     * @see getFacultyRatioRankings
     */
    @Override
    public double[] getCookingRankings() {
        return cookingRankings;
    }
}
