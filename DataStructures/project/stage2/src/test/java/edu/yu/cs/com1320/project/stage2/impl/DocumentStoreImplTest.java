package edu.yu.cs.com1320.project.stage2.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.UUID;

import static edu.yu.cs.com1320.project.stage2.DocumentStore.DocumentFormat.BINARY;
import static edu.yu.cs.com1320.project.stage2.DocumentStore.DocumentFormat.TXT;
import static org.junit.jupiter.api.Assertions.*;

public class DocumentStoreImplTest {
    DocumentStoreImpl docStore;

    @BeforeEach
    void setUp() throws URISyntaxException {
        this.docStore = new DocumentStoreImpl();
    }
    @Test
    @DisplayName("Testing putting and getting with a TXT document")
    void initialPutAndGetWithTXTDoc() throws Exception {
        URI uri = generateRandomURI();
        String str = generateRandomString();
        DocumentImpl temp = new DocumentImpl(uri,str);
        byte[] byteArray = str.getBytes();
        assertEquals(0,this.docStore.put(new ByteArrayInputStream(byteArray),uri,TXT));
        assertEquals(temp,this.docStore.get(uri));
    }
    @Test
    @DisplayName("Testing putting and getting with a Binary document")
    void initialPutAndGetWithBinaryDoc() throws Exception {
        URI uri = generateRandomURI();
        byte[] byteArray = generateRandomByteArray();
        DocumentImpl temp = new DocumentImpl(uri,byteArray);
        assertEquals(0,this.docStore.put(new ByteArrayInputStream(byteArray),uri,BINARY));
        assertEquals(temp,this.docStore.get(uri));
    }

    @Test
    @DisplayName("Testing replacing TXT document")
    void testingReplace() throws Exception{
        URI uri = generateRandomURI();
        String str = generateRandomString();
        DocumentImpl temp = new DocumentImpl(uri,str);
        byte[] byteArray = str.getBytes();
        assertEquals(0,this.docStore.put(new ByteArrayInputStream(byteArray),uri,TXT));
        assertEquals(temp,this.docStore.get(uri));
        String str2 = generateRandomString();
        byte[] byteArray2 = str2.getBytes();
        DocumentImpl temp2 = new DocumentImpl(uri,str2);
        assertEquals(temp.hashCode(),this.docStore.put(new ByteArrayInputStream(byteArray2),uri,TXT));
        assertEquals(temp2,this.docStore.get(uri));
    }
    @Test
    @DisplayName("Testing replacing Binary document")
    void testingReplaceOnBinary() throws Exception{
        URI uri = generateRandomURI();
        byte[] byteArray = generateRandomByteArray();
        DocumentImpl temp = new DocumentImpl(uri,byteArray);
        assertEquals(0,this.docStore.put(new ByteArrayInputStream(byteArray),uri,BINARY));
        assertEquals(temp,this.docStore.get(uri));
        byte[] byteArray2 = generateRandomByteArray();
        DocumentImpl temp2 = new DocumentImpl(uri,byteArray2);
        assertEquals(temp.hashCode(),this.docStore.put(new ByteArrayInputStream(byteArray2),uri,BINARY));
        assertEquals(temp2,this.docStore.get(uri));
    }

    @Test
    @DisplayName("Putting a null where null isnt supposed to go")
    void puttingNull() throws Exception {
        URI uri = generateRandomURI();
        String str = generateRandomString();
        DocumentImpl temp = new DocumentImpl(uri,str);
        byte[] byteArray = str.getBytes();
        assertThrows(IllegalArgumentException.class, () -> this.docStore.put(new ByteArrayInputStream(byteArray),uri,null));
        assertThrows(IllegalArgumentException.class, () -> this.docStore.put(new ByteArrayInputStream(byteArray),null,TXT));
        assertThrows(IllegalArgumentException.class, () -> this.docStore.put(new ByteArrayInputStream(byteArray),null,null));
    }

    @Test
    @DisplayName("Deleting an enrty by putting a null input")
    void testingDelete() throws Exception{
        URI uri = generateRandomURI();
        String str = generateRandomString();
        DocumentImpl temp = new DocumentImpl(uri,str);
        byte[] byteArray = str.getBytes();
        assertEquals(0,this.docStore.put(new ByteArrayInputStream(byteArray),uri,TXT));
        assertEquals(temp,this.docStore.get(uri));
        assertEquals(temp.hashCode(),this.docStore.put(null,uri,TXT));
        assertNull(this.docStore.get(uri));
    }

    @Test
    @DisplayName("Deleting a document that doesnt exist by putting null")
    void deletingAdocThatDoesntExist() throws Exception{
        String str = generateRandomString();
        byte[] byteArr = str.getBytes();
        assertEquals(0,this.docStore.put(null,generateRandomURI(),TXT));
    }

    @Test
    @DisplayName("Deleting a document using the delete method")
    void deletingWithDelete() throws Exception {
        URI uri = generateRandomURI();
        String str = generateRandomString();
        byte[] bytes = str.getBytes();
        DocumentImpl temp = new DocumentImpl(uri,str);
        assertEquals(0,this.docStore.put(new ByteArrayInputStream(bytes),uri,TXT));
        assertEquals(temp,this.docStore.get(uri));
        assertTrue(this.docStore.delete(uri));
        assertNull(this.docStore.get(uri));
    }

    @Test
    @DisplayName("Deleting a document that doesnt exist with delete")
    void deletingFakeDoc()throws Exception{
        assertFalse(this.docStore.delete(generateRandomURI()));
    }

    @Test
    @DisplayName("Testing to see if commands are being added to the stack")
    void stackCount() throws Exception {
        URI uri = generateRandomURI();
        String str = generateRandomString();
        byte[] bytes = str.getBytes();
        DocumentImpl temp = new DocumentImpl(uri,str);
        assertEquals(0,this.docStore.put(new ByteArrayInputStream(bytes),uri,TXT));
        //assertEquals(1, this.docStore.commandStack.size());
        assertTrue(this.docStore.delete(uri));
        //assertEquals(2,this.docStore.commandStack.size());
        assertNull(this.docStore.get(uri));
        this.docStore.undo();
        //assertEquals(1, this.docStore.commandStack.size());
        assertEquals(temp, this.docStore.get(uri));
    }
    /*
     * Method Generated by chat.openai.com
     * Prompt: How to generate random URI
     * */
    private URI generateRandomURI() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        return new URI("https", "test.com", "/" + uuid.toString(), null);
    }

    /*
     * Method Generated by chat.openai.com
     * Prompt: How to generate random String
     * */
    private String generateRandomString() {
        final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(CHARACTERS.charAt(rand.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    /*
     * Method Generated by chat.openai.com
     * Prompt: How to generate random byte[]
     * */
    private byte[] generateRandomByteArray() {
        byte[] temp = new byte[10];
        new Random().nextBytes(temp);
        return temp;
    }

}
