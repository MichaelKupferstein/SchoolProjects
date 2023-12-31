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
import java.util.*;

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

    @Test
    void newMethodTest()throws Exception{
        URI uri1 = generateRandomURI();
        URI uri2 = generateRandomURI();
        URI uri3 = generateRandomURI();
        String testTXT1 = "this document is going to contain a lot of words that start with the letters th, for example their, them " +
                "this, that, through, tough, all of those words start with the prefix th, some are even just th. the next doucment wont have " +
                "as much th's as this one and that is becuase i want it like that. this or that this or that there or their or even they're all " +
                "those words have the correct prefix adding your faviorte yo-yo, needs a unique word for testing so hers zoo";
        String testTXT2 = "Once upon a time there was a man. this man was testing his computer code for his assigment and he typed out all these " +
                "long strings. but this string wont have as many th's as the one above it so it should come second or third when i get all with prefix " +
                " however how many times does the prefix ho appear. even though im jewish i know santa says ho ho ho ho. i also know that that a hoe is " +
                "a gardening tool and that a hole cant really be endless, however sometimes in movies or video games they are. hope i can test this properly " +
                "i will hop if i do and jump through hoops, not basketball hoops but regular hoops with hopes in them like you, the end";
        String testTXT3 = "You dont know how many yous there are in a yogurt, as you can tell that sentance didnt make sense, thats because i hoped it wouldnt" +
                " you see, im trying to write words with the prefix yo so there arent many, theres you, your, you're, yo, yogurt, yo-yo, yourself. I might have" +
                " to do yoga to relax my brain and discover some words, or maybe i'll crack an egg and drink the yolk so i can yoddle. I feel like i have a yolk " +
                "weighing me down like a yogi bear";

        byte[] txt1In = testTXT1.getBytes();
        byte[] txt2In = testTXT2.getBytes();
        byte[] txt3In = testTXT3.getBytes();

        DocumentImpl txt1 = new DocumentImpl(uri1,testTXT1);
        DocumentImpl txt2 = new DocumentImpl(uri2,testTXT2);
        DocumentImpl txt3 = new DocumentImpl(uri3,testTXT3);

        this.docStore.put(new ByteArrayInputStream(txt1In),uri1,TXT);
        this.docStore.put(new ByteArrayInputStream(txt2In),uri2,TXT);
        this.docStore.put(new ByteArrayInputStream(txt3In),uri3,TXT);

        assertEquals(Arrays.asList(txt1,txt2,txt3), this.docStore.searchByPrefix("th"));
        assertEquals(Arrays.asList(txt2,txt3), this.docStore.searchByPrefix("ho"));
        assertEquals(Arrays.asList(txt3,txt1,txt2), this.docStore.searchByPrefix("yo"));
        assertEquals(Arrays.asList(txt1,txt2,txt3), this.docStore.search("the"));
        assertEquals(Arrays.asList(txt2), this.docStore.search("hoops"));
        assertEquals(Collections.emptyList(),this.docStore.search("Supercalifragilisticexpialidocious"));
        assertEquals(Arrays.asList(txt3),this.docStore.search("yoga"));

        assertEquals(txt1, this.docStore.get(uri1));
        assertEquals(txt2, this.docStore.get(uri2));
        assertEquals(txt3, this.docStore.get(uri3));

        assertEquals(Arrays.asList(txt1), this.docStore.search("zoo"));
        this.docStore.undo(uri1);
        assertNull(this.docStore.get(uri1));
        assertEquals(Collections.emptyList(),this.docStore.search("zoo"));
        assertEquals(Arrays.asList(txt2,txt3), this.docStore.searchByPrefix("th"));
        assertEquals(Arrays.asList(txt2,txt3), this.docStore.search("the"));
        assertEquals(Arrays.asList(txt3,txt2), this.docStore.searchByPrefix("yo"));

        assertEquals(0, this.docStore.put(new ByteArrayInputStream(txt1In),uri1,TXT));
        assertEquals(txt1, this.docStore.get(uri1));
        assertEquals(Arrays.asList(txt1,txt2,txt3), this.docStore.searchByPrefix("th"));
        this.docStore.delete(uri1);
        assertNull(this.docStore.get(uri1));
        assertEquals(Collections.emptyList(),this.docStore.search("zoo"));
        assertEquals(Arrays.asList(txt2,txt3), this.docStore.searchByPrefix("th"));
        assertEquals(Arrays.asList(txt2,txt3), this.docStore.search("the"));
        assertEquals(Arrays.asList(txt3,txt2), this.docStore.searchByPrefix("yo"));
        //---//
        this.docStore.undo();
        assertEquals(txt1, this.docStore.get(uri1));
        assertEquals(Arrays.asList(txt1,txt2,txt3), this.docStore.searchByPrefix("th"));
        assertEquals(Arrays.asList(txt3,txt1,txt2), this.docStore.searchByPrefix("yo"));
        assertEquals(Arrays.asList(txt1,txt2,txt3), this.docStore.search("the"));

        Set<URI> AllUriSet = new HashSet<>(Arrays.asList(uri1,uri2,uri3));
        Set<URI> uriSetWithUri1 = new HashSet<>(Arrays.asList(uri1));
        assertEquals(uriSetWithUri1,this.docStore.deleteAll("zoo"));
        assertEquals(Collections.emptyList(),this.docStore.search("zoo"));
        assertNull(this.docStore.get(uri1));
        assertEquals(Arrays.asList(txt2,txt3), this.docStore.searchByPrefix("th"));
        //---//
        this.docStore.undo();
        assertEquals(Arrays.asList(txt1), this.docStore.search("zoo"));
        assertEquals(txt1, this.docStore.get(uri1));

        assertEquals(uriSetWithUri1, this.docStore.deleteAllWithPrefix("zoo"));
        assertEquals(Collections.emptyList(),this.docStore.search("zoo"));
        assertEquals(Arrays.asList(txt2,txt3), this.docStore.searchByPrefix("th"));
        assertNull(this.docStore.get(uri1));
        this.docStore.undo();
        assertEquals(Arrays.asList(txt1), this.docStore.search("zoo"));
        assertEquals(txt1, this.docStore.get(uri1));

        assertEquals(AllUriSet, this.docStore.deleteAllWithPrefix("th"));
        assertEquals(Collections.emptyList(),this.docStore.searchByPrefix("th"));
        assertNull(this.docStore.get(uri1));
        assertNull(this.docStore.get(uri2));
        assertNull(this.docStore.get(uri3));
        this.docStore.undo(uri2);
        assertNull(this.docStore.get(uri1));
        assertEquals(txt2,this.docStore.get(uri2));
        assertNull(this.docStore.get(uri3));
        String breakpoint = "breakpont";
        this.docStore.undo(uri3);
        assertEquals(txt3,this.docStore.get(uri3));

        this.docStore.undo(uri1);
        assertEquals(txt1,this.docStore.get(uri1));
        assertEquals(Arrays.asList(txt1,txt2,txt3), this.docStore.searchByPrefix("th"));


        assertEquals(AllUriSet, this.docStore.deleteAllWithPrefix("th"));
        assertEquals(Collections.emptyList(),this.docStore.searchByPrefix("th"));
        assertNull(this.docStore.get(uri1));
        assertNull(this.docStore.get(uri2));
        assertNull(this.docStore.get(uri3));
        breakpoint = "breakpont";
        this.docStore.undo();
        breakpoint = "breakpont";
        assertEquals(txt1,this.docStore.get(uri1));
        assertEquals(txt2,this.docStore.get(uri2));
        assertEquals(txt3,this.docStore.get(uri3));
        assertEquals(Arrays.asList(txt1,txt2,txt3), this.docStore.searchByPrefix("th"));
        breakpoint = "breakpont";



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
