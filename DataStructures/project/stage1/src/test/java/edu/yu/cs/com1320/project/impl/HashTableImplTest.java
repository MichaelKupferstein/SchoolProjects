package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HashTableImplTest {
    private HashTableImpl<Integer,String> test;
    @BeforeEach
    void setUp() {
        this.test = new HashTableImpl<>();
    }

    @Test
    @DisplayName("Putting inputs in for the first time")
    void initialPut() {
        //initial put should return null bc there are no entries for given key
        assertNull(this.test.put(123,"1st Test"));
        assertNull(this.test.put(456,"2nd Test"));
        assertNull(this.test.put(789,"3rd Test"));
        assertNull(this.test.put(101112,"4th Test"));
        assertNull(this.test.put(131415,"5th Test"));
    }
    @Test
    @DisplayName("Getting the inputs that were put in originally")
    void initialGet() {
        initialPut();
        assertEquals("1st Test", this.test.get(123));
        assertEquals("2nd Test", this.test.get(456));
        assertEquals("3rd Test", this.test.get(789));
        assertEquals("4th Test", this.test.get(101112));
        assertEquals("5th Test", this.test.get(131415));

    }
    @Test
    @DisplayName("Replacing the original put and seeing if it returns the old value")
    void replacingWithPut(){
        initialPut();
        assertEquals("1st Test", this.test.put(123, "Replacing 1st Test"));
        assertEquals("2nd Test", this.test.put(456, "Replacing 2nd Test"));
        assertEquals("3rd Test", this.test.put(789, "Replacing 3rd Test"));
        assertEquals("4th Test", this.test.put(101112, "Replacing 4th Test"));
        assertEquals("5th Test", this.test.put(131415, "Replacing 5th Test"));
    }
    @Test
    @DisplayName("Checking to see if the table contains the appropriate values for each key")
    void containsKey() {
        replacingWithPut();
        assertTrue(this.test.containsKey(123));
        assertFalse(this.test.containsKey(321));
        assertFalse(this.test.containsKey(3820));
        assertTrue(this.test.containsKey(101112));
        assertThrows(NullPointerException.class, () -> this.test.containsKey(null));
        assertFalse(this.test.containsKey(1234));
    }
    @Test
    @DisplayName("Using get after the values have been replaced to see if they return correct values")
    void usingGetAfterValuesHaveBeenReplaced(){
        replacingWithPut();
        assertEquals("Replacing 1st Test", this.test.get(123));
        assertEquals("Replacing 2nd Test", this.test.get(456));
        assertEquals("Replacing 3rd Test", this.test.get(789));
        assertEquals("Replacing 4th Test", this.test.get(101112));
        assertEquals("Replacing 5th Test", this.test.get(131415));
    }
    @Test
    @DisplayName("Removing an entry by putting null as a value")
    void puttingNullAsVtoDeleteAnEntry(){
        replacingWithPut();
        assertEquals("Replacing 5th Test", this.test.put(131415,null));
        assertFalse(this.test.containsKey(131415));
        assertNull(this.test.get(131415));
    }

}
