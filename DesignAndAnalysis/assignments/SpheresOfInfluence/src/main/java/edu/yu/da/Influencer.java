package edu.yu.da;

import java.util.Objects;

public class Influencer implements Comparable<Influencer> {
    String id;
    int xValue;
    int yValue;
    int radius;

    public Influencer(String id, int xValue, int yValue, int radius) {
        this.id = id;
        this.xValue = xValue;
        this.yValue = yValue;
        this.radius = radius;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Influencer that = (Influencer) o;
        return xValue == that.xValue && yValue == that.yValue && radius == that.radius && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, xValue, yValue, radius);
    }

    @Override
    public int compareTo(Influencer o) {
        return Integer.compare(this.radius, o.radius);
    }
}