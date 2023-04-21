package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E>{

    public MinHeapImpl() {
        this.elements = (E[]) new Comparable[10];
    }

    @Override
    public void reHeapify(E element) {
        int index = getArrayIndex(element);
        for(int i = index; i < this.elements.length - 1; i++){
            this.elements[i] = this.elements[i + 1];
        }
        this.elements[this.count] = element;
        //percolate it up to maintain heap order property
        this.upHeap(this.count);
    }

    @Override
    protected int getArrayIndex(E element) {
        for(int i = 1; i < this.elements.length; i++){
            if(this.elements[i].equals(element)){
                return i;
            }
        }
        throw new NoSuchElementException("Element " + element.toString() + " does not exist in this heap");
    }

    @Override
    protected void doubleArraySize() {
        E[] tempArr = (E[]) new Comparable[this.elements.length * 2];
        for(int i = 0; i < this.elements.length; i++){
            tempArr[i] = this.elements[i];
        }
        this.elements = tempArr;
    }
}
