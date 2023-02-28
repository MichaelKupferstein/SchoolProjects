package edu.yu.cs.com1320.project.stage1.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.UUID;

import static edu.yu.cs.com1320.project.stage1.DocumentStore.DocumentFormat.BINARY;
import static edu.yu.cs.com1320.project.stage1.DocumentStore.DocumentFormat.TXT;
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

    private URI generateRandomURI() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        return new URI("https", "test.com", "/" + uuid.toString(), null);
    }

    private String generateRandomString() {
        final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(CHARACTERS.charAt(rand.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private byte[] generateRandomByteArray() {
        byte[] temp = new byte[10];
        new Random().nextBytes(temp);
        return temp;
    }

}
