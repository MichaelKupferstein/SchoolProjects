package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class DocumentPersistenceManagerTest{

    DocumentPersistenceManager dp;
    URI uri1;
    URI uri2;
    Document doc1;
    Document doc2;
    @BeforeEach
    void setUp() throws Exception{
        this.dp = new DocumentPersistenceManager(new File(System.getProperty("user.dir")));
        this.uri1 = new URI("https://www.yu.edu/documents/doc1");
        this.doc1 = new DocumentImpl(uri1,"This is a test document to test this class", null);


        this.uri2 = new URI("https://www.yu.edu/documents/doc2");
        String btyes = "This is a test for binary documents";
        byte[] byteArray = btyes.getBytes();
        this.doc2 = new DocumentImpl(uri2,byteArray);
        this.dp.serialize(uri1,doc1);
        this.dp.serialize(uri2,doc2);
    }

    @Test
    void serialize() throws Exception{
    }

    @Test
    void deserialize() throws Exception{
        Document test1 = dp.deserialize(uri1);

        Document test2 = dp.deserialize(uri2);

        assertEquals(test1,doc1);
        assertEquals(test2,doc2) ;

    }

    @Test
    void delete() throws Exception{

        assertTrue(dp.delete(uri1));
        assertTrue(dp.delete(uri2));
        assertFalse(dp.delete(uri1));
    }

    @AfterEach
    void cleanDelete() throws Exception{
        dp.delete(uri1);
        dp.delete(uri2);
    }
}