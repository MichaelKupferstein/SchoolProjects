package edu.yu.da;

import java.util.Objects;

public class Influencer implements Comparable<Influencer> {
    private String id;
    private int xValue;
    private int radius;

    public Influencer(String id, int xValue, int radius) {
        this.id = id;
        this.xValue = xValue;
        this.radius = radius;
    }

    public String getId() {
        return id;
    }

    public int getXValue() {
        return xValue;
    }

    public int getRadius() {
        return radius;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Influencer that = (Influencer) o;
        return xValue == that.xValue && radius == that.radius && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xValue, radius);
    }

    @Override
    public int compareTo(Influencer o) {
        return Integer.compare(this.xValue, o.xValue);
    }
}