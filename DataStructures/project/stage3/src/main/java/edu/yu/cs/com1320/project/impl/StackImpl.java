package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {
    private int itemCount;
    private T[] stack;

    public StackImpl() {
        this.itemCount = 0;
        this.stack = (T[]) new Object[10];
    }

    private void resize(){
        T[] temp = (T[]) new Object[this.stack.length*2];
        for(int i = 0; i < this.stack.length; i++){
            temp[i] = this.stack[i];
        }
        this.stack = temp;
    }
    /**
     * @param element object to add to the Stack
     */
    @Override
    public void push(T element) {
        this.stack[this.itemCount] = element;
        this.itemCount++;
        if(this.itemCount >= this.stack.length){
            resize();
        }
    }

    /**
     * removes and returns element at the top of the stack
     *
     * @return element at the top of the stack, null if the stack is empty
     */
    @Override
    public T pop() {
        if(this.itemCount == 0) return null;
        T temp = this.stack[this.itemCount - 1];
        this.stack[this.itemCount - 1] = null;
        this.itemCount--;
        return temp;
    }

    /**
     * @return the element at the top of the stack without removing it
     */
    @Override
    public T peek() {
        if(itemCount == 0) return null;
        return this.stack[this.itemCount - 1];
    }

    /**
     * @return how many elements are currently in the stack
     */
    @Override
    public int size() {
        return this.itemCount;
    }
}
