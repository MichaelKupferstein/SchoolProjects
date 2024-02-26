package edu.yu.da;

import java.util.Objects;

public class Yeshiva {

    private double facultyRatioRanking;
    private double cookingRanking;

    public Yeshiva(double facultyRatioRanking, double cookingRanking) {
        this.facultyRatioRanking = facultyRatioRanking;
        this.cookingRanking = cookingRanking;
    }

    public double getFacultyRatioRanking() {
        return facultyRatioRanking;
    }

    public double getCookingRanking() {
        return cookingRanking;
    }

    /**
     * This method compares the current Yeshiva object with another Yeshiva object based on their cookingRanking and facultyRatioRanking.
     *
     * @param other The other Yeshiva object to be compared with.
     * @return returns 1 if the current Yeshiva object has higher cookingRanking and facultyRatioRanking than the other Yeshiva object,
     * indicating that the other Yeshiva object should be deleted.
     * It returns -1 if the other Yeshiva object has higher cookingRanking and facultyRatioRanking than the current Yeshiva object,
     * indicating that the current Yeshiva object should be deleted.
     * It returns 0 if neither of the Yeshiva objects should be deleted.
     */
    public int check(Yeshiva other){
        //if(this.cookingRanking == other.cookingRanking && this.facultyRatioRanking == other.facultyRatioRanking) return 0; //cant be bc no duplicates
        if(this.cookingRanking > other.cookingRanking && this.facultyRatioRanking > other.facultyRatioRanking)
            return 1; // this is greater in both aspects, so other should be deleted
        if(this.cookingRanking < other.cookingRanking && this.facultyRatioRanking < other.facultyRatioRanking)
            return -1; //other is greater in both aspects, so this should be deleted
        return 0; // neither should be deleted
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Yeshiva yeshiva = (Yeshiva) o;
        return Double.compare(facultyRatioRanking, yeshiva.facultyRatioRanking) == 0 && Double.compare(cookingRanking, yeshiva.cookingRanking) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(facultyRatioRanking, cookingRanking);
    }
}
