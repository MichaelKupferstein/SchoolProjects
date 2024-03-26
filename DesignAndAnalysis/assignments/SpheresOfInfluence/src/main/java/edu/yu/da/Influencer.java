package edu.yu.da;

import java.util.Objects;

public class Influencer implements Comparable<Influencer> {
    private final String id;
    private final int xValue, radius, maxStrength;
    private final double area, left, right;

    public Influencer(String id, int xValue, int radius, double leftIntersection, double rightIntersection, int maxStrength, int maxRight) {
        this.id = id;
        this.xValue = xValue;
        this.radius = radius;
        this.left = Math.max(leftIntersection, 0);
        this.right = Math.min(rightIntersection, maxRight);
//        this.left = leftIntersection;
//        this.right = rightIntersection;
        this.maxStrength = maxStrength;
        this.area = calculateArea();
        //System.out.println("Influencer: " + id + " left: " + left + " right: " + right + " area: " + area);
    }

    private double calculateArea(){
        //calculate the area of the rectangle
        double width = right - left;
        return width * (double) maxStrength;
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

    public double getLeft() {
        return left;
    }

    public double getRight() {
        return right;
    }
    public double getArea() {
        return area;
    }

//    public boolean areaAlreadyCovered(double left, double right){
//        return left >= this.left && right <= this.right;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Influencer that = (Influencer) o;
        return xValue == that.xValue && radius == that.radius;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xValue, radius);
    }

    @Override
    public int compareTo(Influencer o) {
        return Double.compare(this.area, o.area);
    }
}