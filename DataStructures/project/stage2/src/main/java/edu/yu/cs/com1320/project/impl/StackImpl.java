package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.Stack;

public class StackImpl implements Stack {
    private int itemCount;
    Command[] commandArr;

    public StackImpl() {
        this.itemCount = 0;
        this.commandArr = new Command[10];
    }

    private void resize(){
        Command[] temp = new Command[this.commandArr.length*2];
        for(int i = 0; i < this.commandArr.length;i++){
            temp[i] = this.commandArr[i];
        }
        this.commandArr = temp;
    }
    /**
     * @param element object to add to the Stack
     */
    @Override
    public void push(Object element) {
        this.commandArr[this.itemCount] = (Command) element;
        this.itemCount++;
        if(this.itemCount >= this.commandArr.length){
            resize();
        }
    }

    /**
     * removes and returns element at the top of the stack
     *
     * @return element at the top of the stack, null if the stack is empty
     */
    @Override
    public Object pop() {
        if(this.itemCount == 0) return null;
        Object temp = this.commandArr[this.itemCount];
        this.commandArr[this.itemCount] = null;
        this.itemCount--;
        return temp;
    }

    /**
     * @return the element at the top of the stack without removing it
     */
    @Override
    public Object peek() {
        return this.commandArr[this.itemCount];
    }

    /**
     * @return how many elements are currently in the stack
     */
    @Override
    public int size() {
        return this.itemCount;
    }
}
