package edu.yu.cs.com1320.project.stage1.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentImplTest {
    private URI uri;
    @BeforeEach
    void setUp() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        this.uri = new URI("https","test.com","/" + uuid.toString(),null);
    }
    @Test
    @DisplayName("Creating with a null where a null isnt supposed to go")
    void createWithNullUri(){
        String test = "This is a test string";
        byte[] temp = new byte[10];
        new Random().nextBytes(temp);
        assertThrows(IllegalArgumentException.class, () -> new DocumentImpl(null,test));
        assertThrows(IllegalArgumentException.class, () -> new DocumentImpl(null,temp));
        String strNull = null;
        byte[] nullByte = null;
        assertThrows(IllegalArgumentException.class, () -> new DocumentImpl(this.uri,strNull));
        assertThrows(IllegalArgumentException.class, () -> new DocumentImpl(this.uri,nullByte));
    }
    @Test
    @DisplayName("Creating a TXT document and using all methods on it")
    void createATXTDocumentAndUseMethods() {
        DocumentImpl test = new DocumentImpl(this.uri,"This is a test String");
        assertEquals("This is a test String", test.getDocumentTxt());
        assertNull(test.getDocumentBinaryData());
        assertEquals(this.uri,test.getKey());
        assertTrue(test.equals(test));
        assertFalse(test.equals(null));
        int tempInt = 10235323;
        assertFalse(test.equals(tempInt));
        DocumentImpl test2 = new DocumentImpl(this.uri,"This is a test String");
        assertTrue(test.equals(test2));
    }

    @Test
    @DisplayName("Creating a Binary document and using all methods on it")
    void createABinaryDocumentAndUseMethods() {
        byte[] temp = new byte[10];
        new Random().nextBytes(temp);
        DocumentImpl test = new DocumentImpl(this.uri,temp);
        assertEquals(temp,test.getDocumentBinaryData());
        assertNull(test.getDocumentTxt());
        assertEquals(this.uri,test.getKey());
        assertTrue(test.equals(test));
        assertFalse(test.equals(null));
        int tempInt = 10235323;
        assertFalse(test.equals(tempInt));
        byte[] temp2 = new byte[10];
        new Random().nextBytes(temp2);
        DocumentImpl test2 = new DocumentImpl(this.uri,temp2);
        assertFalse(test.equals(test2));
        DocumentImpl test3 = new DocumentImpl(this.uri,temp);
        assertTrue(test.equals(test3));
    }

}
