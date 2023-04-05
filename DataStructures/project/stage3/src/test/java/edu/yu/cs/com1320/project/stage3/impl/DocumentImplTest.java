package edu.yu.cs.com1320.project.stage3.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentImplTest {
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        this.uri = generateRandomURI();
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
    }@Test
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
    }@Test
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
    @Test
    @DisplayName("Should return true when the two objects are equal")
    void equalsShouldReturnTrue() throws URISyntaxException {
        URI uri = generateRandomURI();
        DocumentImpl temp = new DocumentImpl(uri, "test");
        DocumentImpl temp2 = new DocumentImpl(uri, "test");
        assertTrue(temp.equals(temp2));
    }

    @Test
    @DisplayName("Should return false when the two objects are not equal")
    void equalsShouldReturnFalse() throws URISyntaxException {
        DocumentImpl temp = new DocumentImpl(generateRandomURI(), "test");
        DocumentImpl temp2 = new DocumentImpl(generateRandomURI(), "test2");
        assertFalse(temp.equals(temp2));
    }

    @Test
    @DisplayName("Testing the hashCode method")
    void testHashCode() throws URISyntaxException {
        URI uri = generateRandomURI();
        DocumentImpl temp1 = new DocumentImpl(uri,"This is a test TXT document");
        DocumentImpl temp2 = new DocumentImpl(uri,"This is a test TXT document");
        DocumentImpl temp3 = new DocumentImpl(generateRandomURI(),"This is another test TXT document");
        assertEquals(temp1.hashCode(),temp2.hashCode());
        assertNotEquals(temp1.hashCode(),temp3.hashCode());
    }

    @Test
    void getWordsTest()throws Exception{
        String testInput = "This$ is a 12   test to&    see   if thi*s works' li!ke it's   supposed to, 213 I am addi(ng words; 431 and Random ch@aracters " +
                "to see if it get's rid of them... let's    find o-ut";
        URI uri = generateRandomURI();
        DocumentImpl temp = new DocumentImpl(uri,testInput);
        String cleanedTxt = "This is a 12 test to see if this works like its supposed to 213 I am adding words 431 and Random characters to see " +
                "if it gets rid of them lets find out";
        String[] tempArr = cleanedTxt.split(" ");
        Set<String> tempSet = new HashSet<>(Arrays.asList(tempArr));
        assertEquals(tempSet, temp.getWords());
    }
    @Test
    void wordCountTest() throws Exception{
        String testInput = "This text document will have multiple of the same word. What will that word be you may ask? " +
                "The answer to that question is unknown, but maybe the computer can tell you. You and you are considered diffrent words" +
                " because it is case sensitive so you and You are diffrent, just like You and YOu and yoU are all different, this will" +
                " help in the trie implentation just to make sure it works. Ok this is the end of the string now lets see if the word count " +
                "thing works";
        URI uri = generateRandomURI();
        DocumentImpl temp = new DocumentImpl(uri, testInput);
        assertEquals(6, temp.wordCount("the"));
        assertEquals(3, temp.wordCount("will"));
        assertEquals(4, temp.wordCount("you"));
        assertEquals(3, temp.wordCount("You"));
    }

    /*
     * Method Generated by chat.openai.com
     * */
    private URI generateRandomURI() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        return new URI("https", "test.com", "/" + uuid.toString(), null);
    }
}
