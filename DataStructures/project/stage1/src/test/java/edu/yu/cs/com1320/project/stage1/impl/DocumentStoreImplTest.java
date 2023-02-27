package edu.yu.cs.com1320.project.stage1.impl;

import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage1.DocumentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static edu.yu.cs.com1320.project.stage1.DocumentStore.DocumentFormat.TXT;
import static org.junit.jupiter.api.Assertions.*;

public class DocumentStoreImplTest {
    DocumentStoreImpl hashTable;
    DocumentImpl TXTTemp1;
    DocumentImpl TXTTemp2;
    DocumentImpl TXTTemp3;
    DocumentImpl TXTTemp4;
    DocumentImpl TXTTemp5;
    DocumentImpl BinTemp1;
    DocumentImpl BinTemp2;
    DocumentImpl BinTemp3;
    DocumentImpl BinTemp4;
    DocumentImpl BinTemp5;


    @BeforeEach
    void setUp() throws URISyntaxException {
        this.hashTable = new DocumentStoreImpl();
        //create a bunch of random DocumentImpl
        this.TXTTemp1 = new DocumentImpl(generateRandomURI(),generateRandomString());
        this.TXTTemp2 = new DocumentImpl(generateRandomURI(),generateRandomString());
        this.TXTTemp3 = new DocumentImpl(generateRandomURI(),generateRandomString());
        this.TXTTemp4 = new DocumentImpl(generateRandomURI(),generateRandomString());
        this.TXTTemp5 = new DocumentImpl(generateRandomURI(),generateRandomString());
        this.BinTemp1 = new DocumentImpl(generateRandomURI(),generateRandomByteArray());
        this.BinTemp2 = new DocumentImpl(generateRandomURI(),generateRandomByteArray());
        this.BinTemp3 = new DocumentImpl(generateRandomURI(),generateRandomByteArray());
        this.BinTemp4 = new DocumentImpl(generateRandomURI(),generateRandomByteArray());
        this.BinTemp5 = new DocumentImpl(generateRandomURI(),generateRandomByteArray());

    }

    @Test
    void initialPut() throws Exception {
        //assertEquals(0, this.hashTable.put(this.TXTTemp1,this.TXTTemp1.getKey(),TXT));
    }


    @Test
    void get() {
    }

    @Test
    void delete() {
    }
    private URI generateRandomURI() throws URISyntaxException {
        String scheme = "http";
        String host = "example.com";
        String randomString = generateRandomString();
        String path = "/" + randomString;
        return new URI(scheme, host, path, null);
    }
    private String generateRandomString() {
        return new String(generateRandomByteArray(), java.nio.charset.StandardCharsets.UTF_8);
    }

    private byte[] generateRandomByteArray(){
        byte[] temp = new byte[10];
        new Random().nextBytes(temp);
        return temp;
    }
}
