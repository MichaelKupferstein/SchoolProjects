package edu.yu.da;

import java.util.Objects;

public class Influencer implements Comparable<Influencer> {
    private String id;
    private int xValue, radius, maxStrength, maxRight;
    private double area;
    private Rectangle rect;

    public Influencer(String id, int xValue, int radius, double leftIntersection, double rightIntersection, int maxStrength, int maxRight) {
        this.id = id;
        this.xValue = xValue;
        this.radius = radius;
        this.rect = new Rectangle(leftIntersection, rightIntersection, maxStrength, maxRight);
        this.area = rect.getArea();
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

    public double getArea() {
        return area;
    }

    public double getLeft() {
        return rect.topLeft.x;
    }

    public double getRight() {
        return rect.topRight.x;
    }

    public boolean areaAlreadyCovered(double left, double right){
        return left >= rect.topLeft.x && right <= rect.topRight.x;
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
        return Objects.hash(id, xValue, radius);
    }

    @Override
    public int compareTo(Influencer o) {
        return Double.compare(this.area, o.area);
    }

    private class Rectangle{
        //store all four corner points of the rectangle
        private Point topLeft, topRight, bottomLeft, bottomRight;
        private double area;

        public Rectangle(double leftIntersection, double rightIntersection, int maxStrength, int maxRight){
            //calculate the four corner points of the rectangle
            this.topLeft = new Point(Math.max(leftIntersection, 0), maxStrength);
            this.topRight = new Point(Math.min(rightIntersection, maxRight), maxStrength);
            this.bottomLeft = new Point(Math.max(leftIntersection, 0), 0);
            this.bottomRight = new Point(Math.min(rightIntersection, maxRight), 0);
            this.area = calcuateArea();
        }
        private double calcuateArea(){
            //calculate the area of the rectangle
            double width = topRight.x - topLeft.x;
            double height = topLeft.y - bottomLeft.y;
            return width * height;
        }
        public double getArea(){
            return area;
        }

        @Override
        public String toString() {
            return topLeft + "------" + topRight + "\n" +
                    "|                                              |\n" +
                    "|                                              |\n" +
                    bottomLeft + "------" + bottomRight + "\n";
        }

    }
    private class Point{
        private double x, y;
        public Point(double x, double y){
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }
}