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
