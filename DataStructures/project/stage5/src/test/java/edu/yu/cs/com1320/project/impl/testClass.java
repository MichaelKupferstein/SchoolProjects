package edu.yu.cs.com1320.project.impl;

import java.util.Objects;

public class testClass implements Comparable<testClass> {

    private String name;
    private int time;
    public testClass(String name, int time) {
        this.name = name;
        this.time = time;
    }

    public int getTime(){
        return this.time;
    }

    public void setTime(int time){
        this.time = time;
    }

    @Override
    public String toString() {
        return name + " " +time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof testClass)) return false;
        testClass temp = (testClass) o;
        return this.name.equals(temp.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, getTime());
    }

    @Override
    public int compareTo(testClass o) {
        return Integer.compare(this.time,o.getTime());
    }
}
