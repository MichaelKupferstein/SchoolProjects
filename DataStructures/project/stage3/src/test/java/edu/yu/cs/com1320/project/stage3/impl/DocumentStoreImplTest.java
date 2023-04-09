package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.stage3.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static edu.yu.cs.com1320.project.stage3.DocumentStore.DocumentFormat.BINARY;
import static edu.yu.cs.com1320.project.stage3.DocumentStore.DocumentFormat.TXT;
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

    @Test
    @DisplayName("Testing undo() to undo two puts")
    void testUndo() throws Exception{
        URI uri = generateRandomURI();
        URI uri2 = generateRandomURI();
        String str = generateRandomString();
        String str2 = generateRandomString();
        byte[] bytes = str.getBytes();
        byte[] bytes2 = str2.getBytes();
        DocumentImpl temp = new DocumentImpl(uri,str);
        DocumentImpl temp2 = new DocumentImpl(uri2,str2);
        this.docStore.put(new ByteArrayInputStream(bytes),uri,TXT);
        this.docStore.put(new ByteArrayInputStream(bytes2),uri2,TXT);
        assertEquals(temp, this.docStore.get(uri));
        assertEquals(temp2, this.docStore.get(uri2));
        this.docStore.undo();
        assertNull(this.docStore.get(uri2));
        this.docStore.get(uri);
        this.docStore.undo();
        assertNull(this.docStore.get(uri));
    }

    @Test
    @DisplayName("Testing undo() on a delete")
    void testUndo2() throws Exception{
        URI uri = generateRandomURI();
        String str = generateRandomString();
        byte[] bytes = str.getBytes();
        DocumentImpl temp = new DocumentImpl(uri,str);
        this.docStore.put(new ByteArrayInputStream(bytes),uri,TXT);
        assertEquals(temp, this.docStore.get(uri));
        assertTrue(this.docStore.delete(uri));
        assertNull(this.docStore.get(uri));
        this.docStore.undo();
        assertEquals(temp,this.docStore.get(uri));
    }

    @Test
    @DisplayName("Testung undo(uri) on specific uri put")
    void testUndo3() throws Exception{
        URI uri = generateRandomURI();
        String str = generateRandomString();
        byte[] bytes = str.getBytes();
        DocumentImpl temp = new DocumentImpl(uri,str);
        this.docStore.put(new ByteArrayInputStream(bytes),uri,TXT);
        assertEquals(temp,this.docStore.get(uri));
        for(int i = 0; i < 10; i++){
            assertEquals(0,this.docStore.put(new ByteArrayInputStream(generateRandomByteArray()),generateRandomURI(),TXT));
        }
        for(int i = 0; i < 10; i++){
            assertEquals(0,this.docStore.put(new ByteArrayInputStream(generateRandomByteArray()),generateRandomURI(),BINARY));
        }
        this.docStore.undo(uri);
        assertNull(this.docStore.get(uri));
    }

    @Test
    @DisplayName("Testung undo(uri) on specific uri delete")
    void testUndo4() throws Exception{
        URI uri = generateRandomURI();
        String str = generateRandomString();
        byte[] bytes = str.getBytes();
        DocumentImpl temp = new DocumentImpl(uri,str);
        this.docStore.put(new ByteArrayInputStream(bytes),uri,TXT);
        assertEquals(temp,this.docStore.get(uri));
        assertTrue(this.docStore.delete(uri));
        for(int i = 0; i < 10; i++){
            assertEquals(0,this.docStore.put(new ByteArrayInputStream(generateRandomByteArray()),generateRandomURI(),TXT));
        }
        for(int i = 0; i < 10; i++){
            assertEquals(0,this.docStore.put(new ByteArrayInputStream(generateRandomByteArray()),generateRandomURI(),BINARY));
        }
        this.docStore.undo(uri);
        assertEquals(temp, this.docStore.get(uri));
    }

    @Test
    @DisplayName("Calling undo on an empty stack")
    void empty1(){
        assertThrows(IllegalStateException.class, () -> this.docStore.undo());
    }
    @Test
    @DisplayName("Calling undo(uri) with a uri thats not in the stack")
    void empty2()throws Exception{
        for(int i = 0; i < 10; i++){
            assertEquals(0,this.docStore.put(new ByteArrayInputStream(generateRandomByteArray()),generateRandomURI(),TXT));
        }
        URI uri = generateRandomURI();
        assertThrows(IllegalStateException.class, () -> this.docStore.undo(uri));
    }

    @Test
    @DisplayName("Calling undo(uri) on an empty stack")
    void empty3(){
        assertThrows(IllegalStateException.class, () -> this.docStore.undo(generateRandomURI()));
    }

    @Test
    @DisplayName("Undo by uri the contents of a doc being replaced")
    void undoOverwriteByURI() throws Exception{
        URI uri = generateRandomURI();
        String str = generateRandomString();
        byte[] bytes = str.getBytes();
        DocumentImpl temp = new DocumentImpl(uri,str);
        this.docStore.put(new ByteArrayInputStream(bytes),uri,TXT);
        assertEquals(temp,this.docStore.get(uri));
        String str2 = generateRandomString();
        byte[] bytes2 = str2.getBytes();
        DocumentImpl temp2 = new DocumentImpl(uri,str2);
        this.docStore.put(new ByteArrayInputStream(bytes2),uri,TXT);
        assertEquals(temp2,this.docStore.get(uri));
        this.docStore.undo(uri);
        assertEquals(temp,this.docStore.get(uri));
    }

    @Test
    void testingWithRealDocuments()throws Exception{
        //   C:/Users/mkupf/OneDrive/Documents
        URI uriForTxt1 = generateRandomURI();
        URI uriForTxt2 = generateRandomURI();
        URI uriForTxt3 = generateRandomURI();
        URI uriForTxt4 = generateRandomURI();
        URI uriForTxt5 = generateRandomURI();
        URI uriForTxt6 = generateRandomURI();
        String txt1 = readFile("C:/Users/mkupf/OneDrive/Documents/text1.txt");
        String txt2 = readFile("C:/Users/mkupf/OneDrive/Documents/text2.txt");
        String txt3 = readFile("C:/Users/mkupf/OneDrive/Documents/text3.txt");
        String txt4 = readFile("C:/Users/mkupf/OneDrive/Documents/text4.txt");
        String txt5 = readFile("C:/Users/mkupf/OneDrive/Documents/text5.txt");
        String txt6 = readFile("C:/Users/mkupf/OneDrive/Documents/text6.txt");
        InputStream inTxt1 = readFileToInputStream("C:/Users/mkupf/OneDrive/Documents/text1.txt");
        InputStream inTxt2 = readFileToInputStream("C:/Users/mkupf/OneDrive/Documents/text2.txt");
        InputStream inTxt3 = readFileToInputStream("C:/Users/mkupf/OneDrive/Documents/text3.txt");
        InputStream inTxt4 = readFileToInputStream("C:/Users/mkupf/OneDrive/Documents/text4.txt");
        InputStream inTxt5 = readFileToInputStream("C:/Users/mkupf/OneDrive/Documents/text5.txt");
        InputStream inTxt6 = readFileToInputStream("C:/Users/mkupf/OneDrive/Documents/text6.txt");
        DocumentImpl text1 = new DocumentImpl(uriForTxt1,txt1);
        DocumentImpl text2 = new DocumentImpl(uriForTxt2,txt2);
        DocumentImpl text3 = new DocumentImpl(uriForTxt3,txt3);
        DocumentImpl text4 = new DocumentImpl(uriForTxt4,txt4);
        DocumentImpl text5 = new DocumentImpl(uriForTxt5,txt5);
        DocumentImpl text6 = new DocumentImpl(uriForTxt6,txt6);
        this.docStore.put(inTxt1,uriForTxt1,TXT);
        this.docStore.put(inTxt2,uriForTxt2,TXT);
        this.docStore.put(inTxt3,uriForTxt3,TXT);
        this.docStore.put(inTxt4,uriForTxt4,TXT);
        this.docStore.put(inTxt5,uriForTxt5,TXT);
        this.docStore.put(inTxt6,uriForTxt6,TXT);
        List<Document> tempList = this.docStore.search("is");
        List<Document> tempList2 = this.docStore.searchByPrefix("B");
        String breakPoint = "Test";
    }

    private InputStream readFileToInputStream(String filePath){
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(filePath);
        } catch (IOException e) {
            System.err.println("Error opening file: " + e.getMessage());
            return null;
        }
        return inputStream;
    }
    private String readFile(String filePath){
        String fileContent;

        try{
            fileContent = Files.readString(Paths.get(filePath));
        }catch(IOException e){
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }
        return fileContent;
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
        String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();
        for (int i = 0; i < temp.length; i++) {
            temp[i] = (byte) validChars.charAt(rand.nextInt(validChars.length()));
        }
        return temp;
    }

}
