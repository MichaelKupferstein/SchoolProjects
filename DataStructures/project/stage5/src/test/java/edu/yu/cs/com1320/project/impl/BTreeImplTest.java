package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BTreeImplTest {

    @Test
    public void testPutAndGet() {
        BTree<Integer, String> tree = new BTreeImpl<Integer,String>();
//        WrongBTree<Integer, String> tree = new WrongBTree<>();

        tree.put(10, "ten");
        tree.put(20, "twenty");
        tree.put(5, "five");
        tree.put(6, "six");
        tree.put(12, "twelve");
        tree.put(30, "thirty");
        tree.put(2, "two");
        tree.put(3, "three");
        tree.put(4, "four");
        tree.put(7, "seven");


        assertEquals("ten", tree.get(10));
        assertEquals("twenty", tree.get(20));
        assertEquals("five", tree.get(5));
        assertEquals("six", tree.get(6));
        assertEquals( "twelve", tree.get(12));
        assertEquals( "thirty", tree.get(30));
        assertNull(null, tree.get(7));

//        tree.put(10,null);
        assertEquals("ten",tree.put(10,null));
        assertNull(tree.get(10));
    }
    @Test
    void testWithDocs()throws Exception{
        BTree<URI,Document> tree = new BTreeImpl<URI, Document>();
        DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
        tree.setPersistenceManager(pm);

        URI uri1 = generateRandomURI();
        String txt1 = generateRandomString(100);
        Document doc1 = new DocumentImpl(uri1,txt1,null);
        URI uri2 = generateRandomURI();
        String txt2 = generateRandomString(100);
        Document doc2 = new DocumentImpl(uri2,txt2,null);
        URI uri3 = generateRandomURI();
        String txt3 = generateRandomString(100);
        Document doc3 = new DocumentImpl(uri3,txt3,null);
        tree.put(uri1,doc1);
        tree.put(uri2,doc2);
        tree.put(uri3,doc3);
        assertEquals(doc1,tree.get(uri1));
        assertEquals(doc2,tree.get(uri2));
        assertEquals(doc3,tree.get(uri3));
        String b = "B";
        tree.moveToDisk(uri1);
        b = "b";
       assertEquals(doc1,tree.get(uri1));
       assertEquals(doc2,tree.put(uri2,null));
       assertNull(tree.get(uri2));

    }
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
    private String generateRandomString(int length) {
        final String CHARACTERS = "a bcd e fg h ij kl mn o p q rstuv w x y zA B CDE F G H I JKLMN OP QR STU V WXYZ";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
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
        String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();
        for (int i = 0; i < temp.length; i++) {
            temp[i] = (byte) validChars.charAt(rand.nextInt(validChars.length()));
        }
        return temp;
    }
    private byte[] generateRandomByteArray(int length) {
        byte[] temp = new byte[length];
        String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();
        for (int i = 0; i < temp.length; i++) {
            temp[i] = (byte) validChars.charAt(rand.nextInt(validChars.length()));
        }
        return temp;
    }

}