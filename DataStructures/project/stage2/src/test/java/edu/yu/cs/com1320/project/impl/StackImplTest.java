package edu.yu.cs.com1320.project.impl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class StackImplTest {
    private StackImpl<String> test;
    @BeforeEach
    void setUp() {
        this.test = new StackImpl<>();
    }

    @Test
    @DisplayName("Pushing strings onto stack then popping")
    void initialPush(){
        this.test.push("1st element");
        assertEquals(1,this.test.size());
        this.test.push("2nd element");
        assertEquals(2,this.test.size());
        this.test.push("3rd element");
        assertEquals(3,this.test.size());
        this.test.push("4th element");
        assertEquals(4,this.test.size());
        this.test.push("5th element");
        assertEquals(5,this.test.size());

        assertEquals("5th element",this.test.pop());
        assertEquals(4,this.test.size());
        assertEquals("4th element",this.test.pop());
        assertEquals(3,this.test.size());
        assertEquals("3rd element",this.test.pop());
        assertEquals(2,this.test.size());
        assertEquals("2nd element",this.test.pop());
        assertEquals(1,this.test.size());
        assertEquals("1st element",this.test.pop());
        assertEquals(0,this.test.size());

    }
    @Test
    @DisplayName("Testing peek")
    void testPeek(){
        this.test.push("1st element");
        this.test.push("2nd element");
        this.test.push("3rd element");
        this.test.push("4th element");
        assertEquals(4,this.test.size());
        assertEquals("4th element",this.test.peek());
        assertEquals(4,this.test.size());
        this.test.pop();
        assertEquals(3,this.test.size());
        assertEquals("3rd element",this.test.peek());
        assertEquals(3,this.test.size());
    }

    @Test
    @DisplayName("Testing to make sure the resize method works correctly")
    void testResize(){
        //test uses .length() which needs to be private to work but cant
        StackImpl<Integer> tempTest = new StackImpl<>();
        for(int i = 0; i < 15; i++){
            tempTest.push(i);
/*
            if(i < 9){
                assertEquals(10,tempTest.length());
            }else{
                assertEquals(20,tempTest.length());
            }
*/

        }

        for(int i = 0, count = 14; i < 15; i++,count--){
            assertEquals(count,tempTest.pop());
        }
    }
}
