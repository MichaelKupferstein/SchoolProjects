package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.stage4.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.print.Doc;
import javax.swing.event.DocumentEvent;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static edu.yu.cs.com1320.project.stage4.DocumentStore.DocumentFormat.BINARY;
import static edu.yu.cs.com1320.project.stage4.DocumentStore.DocumentFormat.TXT;
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
        String breakPoint = "break";
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
        //creates a document
        URI uri = generateRandomURI();
        String str = generateRandomString();
        byte[] bytes = str.getBytes();
        DocumentImpl temp = new DocumentImpl(uri,str);
        //puts that document in the docStore
        this.docStore.put(new ByteArrayInputStream(bytes),uri,TXT);
        assertEquals(temp,this.docStore.get(uri));
        //creates a second different string with the same uri and makes a new doc
        String str2 = generateRandomString();
        byte[] bytes2 = str2.getBytes();
        DocumentImpl temp2 = new DocumentImpl(uri,str2);
        //puts that new doc in the docStore, but it just acts as a replace
        this.docStore.put(new ByteArrayInputStream(bytes2),uri,TXT);
        assertEquals(temp2,this.docStore.get(uri));
        //undo that, so now it goes back to orginal document
        this.docStore.undo(uri);
        //the get should now return the orgianl document
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
        String b = "b";
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

    @Test
    void heapTests()throws Exception{
        URI uri1 = generateRandomURI();
        String txt1 = generateRandomString();
        Document doc1 = new DocumentImpl(uri1,txt1);
        byte[] bytes1 = txt1.getBytes();

        URI uri2 = generateRandomURI();
        String txt2 = generateRandomString();
        Document doc2 = new DocumentImpl(uri2,txt2);
        byte[] bytes2 = txt2.getBytes();

        URI uri3 = generateRandomURI();
        String txt3 = generateRandomString();
        Document doc3 = new DocumentImpl(uri3,txt3);
        byte[] bytes3 = txt3.getBytes();

        URI uri4 = generateRandomURI();
        String txt4 = generateRandomString();
        Document doc4 = new DocumentImpl(uri4,txt4);
        byte[] bytes4 = txt4.getBytes();

        this.docStore.put(new ByteArrayInputStream(bytes1),uri1,TXT);
        this.docStore.put(new ByteArrayInputStream(bytes2),uri2,TXT);
        this.docStore.put(new ByteArrayInputStream(bytes3),uri3,TXT);
        this.docStore.put(new ByteArrayInputStream(bytes4),uri4,TXT);
        String b = "breakpoint";
        this.docStore.get(uri1);
        b = "b";
        this.docStore.setMaxDocumentBytes(20);
        b = "b";
        this.docStore.setMaxDocumentCount(1);
        b = "b";
    }

    @Test
    void testingOverFlowLogicOnPut1a()throws Exception{//tests when there is a limit on doc limit, before any docs are put in, for txt docs
        this.docStore.setMaxDocumentCount(8);
        ArrayList<Document> listOfAllCreatedDocs = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            URI tempUri = generateRandomURI();
            String tempTxt = generateRandomString(50);
            Document temp = new DocumentImpl(tempUri,tempTxt);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempTxt.getBytes()),tempUri,TXT);
        }
        String b = "breakpoint";
        for(int i = 0; i  < 10; i++){
            if(i < 2){
                assertNull(this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }else {
                assertEquals(listOfAllCreatedDocs.get(i), this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }
        }
    }
    @Test
    void testingOverFlowLogicOnPut1b()throws Exception{//tests when there is a limit on doc limit, before any docs are put in, for binary
        this.docStore.setMaxDocumentCount(8);
        ArrayList<Document> listOfAllCreatedDocs = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            URI tempUri = generateRandomURI();
            byte[] tempByte = generateRandomByteArray(50);
            Document temp = new DocumentImpl(tempUri,tempByte);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempByte),tempUri,BINARY);
        }
        String b = "breakpoint";
        for(int i = 0; i  < 10; i++){
            if(i < 2){
                assertNull(this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }else {
                assertEquals(listOfAllCreatedDocs.get(i), this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }
        }

    }

    @Test
    void testingOverFlowLogicOnPut2a()throws Exception{//tests when there is a limit on doc limit, after docs are already in,for TXT doc
        ArrayList<Document> listOfAllCreatedDocs = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            URI tempUri = generateRandomURI();
            String tempTxt = generateRandomString(50);
            Document temp = new DocumentImpl(tempUri,tempTxt);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempTxt.getBytes()),tempUri,TXT);
        }
        String b = "breakpoint";
        this.docStore.setMaxDocumentCount(8);
        b = "b";
        for(int i = 0; i  < 10; i++){
            if(i < 2){
                assertNull(this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }else {
                assertEquals(listOfAllCreatedDocs.get(i), this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }
        }

    }

    @Test
    void testingOverFlowLogicOnPut2b()throws Exception{//tests when there is a limit on doc dimit after docs are already in, for Binary
        ArrayList<Document> listOfAllCreatedDocs = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            URI tempUri = generateRandomURI();
            byte[] tempByte = generateRandomByteArray(50);
            Document temp = new DocumentImpl(tempUri,tempByte);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempByte),tempUri,BINARY);
        }
        String b = "breakpoint";
        this.docStore.setMaxDocumentCount(8);
        b = "b";
        for(int i = 0; i  < 10; i++){
            if(i < 2){
                assertNull(this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }else {
                assertEquals(listOfAllCreatedDocs.get(i), this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }
        }
    }



    @Test
    void testingOverFlowLogicOnPut3a()throws Exception{//tests when there is a limit on byte limit, before any docs are put in, for txt docs
        this.docStore.setMaxDocumentBytes(400);
        ArrayList<Document> listOfAllCreatedDocs = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            URI tempUri = generateRandomURI();
            String tempTxt = generateRandomString(50);
            Document temp = new DocumentImpl(tempUri,tempTxt);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempTxt.getBytes()),tempUri,TXT);
        }
        String b = "breakpoint";
        for(int i = 0; i  < 10; i++){
            if(i < 2){
                assertNull(this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }else {
                assertEquals(listOfAllCreatedDocs.get(i), this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }
        }
    }

    @Test
    void testingOverFlowLogicOnPut3b()throws Exception{//tests when there is a limit on byte limit, before any docs are put in, for binary
        this.docStore.setMaxDocumentBytes(400);
        ArrayList<Document> listOfAllCreatedDocs = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            URI tempUri = generateRandomURI();
            byte[] tempByte = generateRandomByteArray(50);
            Document temp = new DocumentImpl(tempUri,tempByte);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempByte),tempUri,BINARY);
        }
        String b = "breakpoint";
        for(int i = 0; i  < 10; i++){
            if(i < 2){
                assertNull(this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }else {
                assertEquals(listOfAllCreatedDocs.get(i), this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }
        }

    }

    @Test
    void testingOverFlowLogicOnPut4a()throws Exception{//tests when there is a limit on byte limit, after docs are already in,for TXT doc
        ArrayList<Document> listOfAllCreatedDocs = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            URI tempUri = generateRandomURI();
            String tempTxt = generateRandomString(50);
            Document temp = new DocumentImpl(tempUri,tempTxt);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempTxt.getBytes()),tempUri,TXT);
        }
        String b = "breakpoint";
        this.docStore.setMaxDocumentBytes(400);
        b = "b";
        for(int i = 0; i  < 10; i++){
            if(i < 2){
                assertNull(this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }else {
                assertEquals(listOfAllCreatedDocs.get(i), this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }
        }

    }

    @Test
    void testingOverFlowLogicOnPut4b()throws Exception{//tests when there is a limit on byte dimit after docs are already in, for Binary
        ArrayList<Document> listOfAllCreatedDocs = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            URI tempUri = generateRandomURI();
            byte[] tempByte = generateRandomByteArray(50);
            Document temp = new DocumentImpl(tempUri,tempByte);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempByte),tempUri,BINARY);
        }
        String b = "breakpoint";
        this.docStore.setMaxDocumentBytes(400);
        b = "b";
        for(int i = 0; i  < 10; i++){
            if(i < 2){
                assertNull(this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }else {
                assertEquals(listOfAllCreatedDocs.get(i), this.docStore.get(listOfAllCreatedDocs.get(i).getKey()));
            }
        }
    }

    @Test
    void testingOverFlowLogicOnPut5a()throws Exception{//tests when there is a limit on both, before any documents are put in, for TXT and Binary
        this.docStore.setMaxDocumentBytes(750);
        this.docStore.setMaxDocumentCount(16);
        ArrayList<Document> listOfAllCreatedDocs = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < 10; i++){
            URI tempUri = generateRandomURI();
            String tempTxt = generateRandomString(rand.nextInt(50));
            Document temp = new DocumentImpl(tempUri,tempTxt);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempTxt.getBytes()),tempUri,TXT);
        }
        for(int i = 0; i < 10; i++){
            URI tempUri = generateRandomURI();
            byte[] tempByte = generateRandomByteArray(rand.nextInt(50));
            Document temp = new DocumentImpl(tempUri,tempByte);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempByte),tempUri,BINARY);
        }
        String b = "breakpoint";

    }
    @Test
    void testingOverFlowLogicOnPut5b()throws Exception{//tests when there is a limit on both, after  documents are put in, for TXT and Binary
        ArrayList<Document> listOfAllCreatedDocs = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < 10; i++){
            URI tempUri = generateRandomURI();
            String tempTxt = generateRandomString(rand.nextInt(50));
            Document temp = new DocumentImpl(tempUri,tempTxt);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempTxt.getBytes()),tempUri,TXT);
        }
        for(int i = 0; i < 10; i++){
            URI tempUri = generateRandomURI();
            byte[] tempByte = generateRandomByteArray(rand.nextInt(50));
            Document temp = new DocumentImpl(tempUri,tempByte);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempByte),tempUri,BINARY);
        }
        this.docStore.setMaxDocumentBytes(750);
        this.docStore.setMaxDocumentCount(16);
        String b = "breakpoint";
    }

    @Test
    void puttingADocumentLargerThenLimitIn()throws Exception{
        byte[] bytes = generateRandomByteArray(100);
        Document temp = new DocumentImpl(generateRandomURI(),bytes);
        this.docStore.setMaxDocumentBytes(50);
        assertThrows(IllegalArgumentException.class, () -> this.docStore.put(new ByteArrayInputStream(bytes),temp.getKey(),BINARY));
    }
    @Test
    void testingOverFlowLogicOnundo1() throws Exception{//there is a limit after an undo is done, using regular undo() on GC
        ArrayList<Document> listOfAllCreatedDocs = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            URI tempUri = generateRandomURI();
            String tempTxt = generateRandomString(50);
            Document temp = new DocumentImpl(tempUri,tempTxt);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempTxt.getBytes()),tempUri,TXT);
        }
        for(int i = 0; i < 10; i++){
            URI tempUri = generateRandomURI();
            byte[] tempByte = generateRandomByteArray(50);
            Document temp = new DocumentImpl(tempUri,tempByte);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempByte),tempUri,BINARY);
        }
        ArrayList<Document> deletedDocs = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < 10; i++){
            Document deleted = listOfAllCreatedDocs.remove(random.nextInt((listOfAllCreatedDocs.size())));
            deletedDocs.add(deleted);
            this.docStore.delete(deleted.getKey());
        }
        this.docStore.setMaxDocumentCount(15);
        String b = "b";
        for(int i = 0; i < 10; i++){
            this.docStore.undo();
        }
        String b1 = "b";

        for(Document doc : deletedDocs){
            assertEquals(doc, this.docStore.get(doc.getKey()));
        }
    }

    @Test
    void testingOverFlowLogicOnundo1b() throws Exception {//there is a limit after an undo is done, using regular undo() on CmdSet
        ArrayList<Document> listOfAllCreatedDocs = new ArrayList<>();
        for(int i = 0; i < 5;i++){
            URI tempUri = generateRandomURI();
            String tempTxt = "Test " + generateRandomString(50);
            Document temp = new DocumentImpl(tempUri,tempTxt);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempTxt.getBytes()),tempUri,TXT);
        }
        for(int i = 0; i < 10;i++){
            URI tempUri = generateRandomURI();
            String tempTxt = generateRandomString(50);
            Document temp = new DocumentImpl(tempUri,tempTxt);
            listOfAllCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(tempTxt.getBytes()),tempUri,TXT);
        }
        ArrayList<URI> deletedDocs = new ArrayList<>(this.docStore.deleteAllWithPrefix("Test"));
        String b = "b";
        this.docStore.setMaxDocumentBytes(500);
        this.docStore.undo();
        for(URI doc : deletedDocs){
            assertNotNull(this.docStore.get(doc));
        }
        b = "b";
    }

    @Test
    void testDeleteFromCommandStackOnCmdSet()throws Exception{//when a doc is in a cmdset and then its removed as the result of overflow it should be removed from commandStack
        ArrayList<Document> allCreatedDoc = new ArrayList<>();
        URI testUri = generateRandomURI();
        String testTxt = "Test" + generateRandomString(50);
        Document testDoc = new DocumentImpl(testUri,testTxt);
        this.docStore.put(new ByteArrayInputStream(testTxt.getBytes()),testUri,TXT);//put in main testDoc
        allCreatedDoc.add(testDoc);
        for(int i = 0; i < 5; i++){
            Document temp = new DocumentImpl(generateRandomURI(),"Test" + generateRandomString(50));
            allCreatedDoc.add(temp);
            this.docStore.put(new ByteArrayInputStream(temp.getDocumentTxt().getBytes()),temp.getKey(),TXT);//put in docs with same prefix
        }
        for(int i = 0; i < 5; i++){//adding another set of docs that can be deleted and make a command set to make sure that command set stays
            Document temp = new DocumentImpl(generateRandomURI(),"Temp" + generateRandomString(50));
            allCreatedDoc.add(temp);
            this.docStore.put(new ByteArrayInputStream(temp.getDocumentTxt().getBytes()),temp.getKey(),TXT);//put in docs with same prefix
        }
        this.docStore.deleteAllWithPrefix("Temp");
        ArrayList<Document> fillerDocs = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            Document temp = new DocumentImpl(generateRandomURI(),generateRandomString(50));
            allCreatedDoc.add(temp);
            fillerDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(temp.getDocumentTxt().getBytes()),temp.getKey(),TXT);//put in other docs to fill it up
        }
        ArrayList<URI> deletedDocs = new ArrayList<>(this.docStore.deleteAllWithPrefix("Test"));//delete 6 docs including the testDoc
        String b = "b";
        this.docStore.put(new ByteArrayInputStream(testTxt.getBytes()),testUri,TXT);//put main testDoc back in so its now part of it
        b = "b";
        for(Document doc : fillerDocs){//move all this above the testDoc in the heap
            this.docStore.get(doc.getKey());
        }
        b = "b"; //check that testDoc is now last in heap
        this.docStore.setMaxDocumentCount(10);//so now testDoc is forced to be deleted from every where, including cmdSet
        b = "b";
        assertThrows(IllegalStateException.class, () -> this.docStore.undo(testUri));

    }

    @Test
    void settingLimitTo0()throws Exception{
        ArrayList<Document> allCreatedDocs = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            Document temp = new DocumentImpl(generateRandomURI(),generateRandomString(50));
            allCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(temp.getDocumentTxt().getBytes()),temp.getKey(),TXT);
        }
        this.docStore.setMaxDocumentCount(0);
        for(Document doc : allCreatedDocs){
            assertNull(this.docStore.get(doc.getKey()));
        }
    }

    @Test
    void settingLimitTo0b()throws Exception{
        ArrayList<Document> allCreatedDocs = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            Document temp = new DocumentImpl(generateRandomURI(),generateRandomString(50));
            allCreatedDocs.add(temp);
            this.docStore.put(new ByteArrayInputStream(temp.getDocumentTxt().getBytes()),temp.getKey(),TXT);
        }
        this.docStore.setMaxDocumentBytes(0);
        for(Document doc : allCreatedDocs){
            assertNull(this.docStore.get(doc.getKey()));
        }
    }

    @Test
    void stage3SearchTxtByPrefix()throws Exception{//does searching by prefix in doc store work when all docs are txt?
        URI uri1 = generateRandomURI();
        String txt1 = "This is a test doc. I am using this to test my code and see if it does what its supposed to";
        Document doc1 = new DocumentImpl(uri1,txt1);
        URI uri2 = generateRandomURI();
        String txt2 = "contains only test once, but the other contains it twice";
        Document doc2 = new DocumentImpl(uri2,txt2);
        this.docStore.put(new ByteArrayInputStream(txt1.getBytes()),uri1,TXT);
        this.docStore.put(new ByteArrayInputStream(txt2.getBytes()),uri2,TXT);

        assertEquals(Arrays.asList(doc1,doc2),this.docStore.searchByPrefix("test"));

    }
    @Test
    void stage4TestMaxDocBytesViaSearch()throws Exception{//test that a doc returned from a search has its lastUseTime updated and
        // therefore is NOT the first doc to be pushed out when we go over the max bytes limit
        URI uri1 = generateRandomURI();
        String txt1 = "This is a test doc. I am using this to test my code and see if it does what its supposed to";
        Document doc1 = new DocumentImpl(uri1,txt1);
        URI uri2 = generateRandomURI();
        String txt2 = "doesnt contain it at all but the other contains it twice";
        Document doc2 = new DocumentImpl(uri2,txt2);
        this.docStore.put(new ByteArrayInputStream(txt1.getBytes()),uri1,TXT);
        this.docStore.put(new ByteArrayInputStream(txt2.getBytes()),uri2,TXT);
        assertEquals(Arrays.asList(doc2),this.docStore.searchByPrefix("twice"));
        assertEquals(Arrays.asList(doc1),this.docStore.searchByPrefix("test"));
        this.docStore.setMaxDocumentBytes(91);
        assertEquals(Collections.emptyList(),this.docStore.searchByPrefix("twice"));
        assertEquals(Arrays.asList(doc1),this.docStore.searchByPrefix("test"));

    }

    @Test
    void stage3SearchBinaryByPrefix()throws Exception{ //does searching by prefix in doc store work when some docs are txt and some are bin?

    }

    @Test
    void stage4TestMaxDocCountViaSearch()throws Exception{//test that a doc returned from a search has its
        // lastUseTime updated and therefore is NOT the first doc to be pushed out when we go over the max docs limit
        URI uri1 = generateRandomURI();
        String txt1 = "This is a test doc. I am using this to test my code and see if it does what its supposed to";
        Document doc1 = new DocumentImpl(uri1,txt1);
        URI uri2 = generateRandomURI();
        String txt2 = "doesnt contain it at all but the other contains it twice";
        Document doc2 = new DocumentImpl(uri2,txt2);
        this.docStore.put(new ByteArrayInputStream(txt1.getBytes()),uri1,TXT);
        this.docStore.put(new ByteArrayInputStream(txt2.getBytes()),uri2,TXT);
        assertEquals(Arrays.asList(doc2),this.docStore.searchByPrefix("twice"));
        assertEquals(Arrays.asList(doc1),this.docStore.searchByPrefix("test"));
        this.docStore.setMaxDocumentCount(1);
        assertEquals(Collections.emptyList(),this.docStore.searchByPrefix("twice"));
        assertEquals(Arrays.asList(doc1),this.docStore.searchByPrefix("test"));

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
    private String generateRandomString(int length) {
        final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ";
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
