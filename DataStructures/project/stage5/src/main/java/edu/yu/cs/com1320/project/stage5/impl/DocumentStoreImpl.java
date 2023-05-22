package edu.yu.cs.com1320.project.stage5.impl;
import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Function;
import static java.lang.System.nanoTime;

public class DocumentStoreImpl implements DocumentStore{
    private BTreeImpl<URI,Document> bTree;
    private StackImpl<Undoable> commandStack;
    private TrieImpl<URI> trie;
    private MinHeapImpl<DocNode> heap;
    private int docLimit, byteLimit, docCount, byteCount;
    private DocumentPersistenceManager dp;


    public DocumentStoreImpl() {
        this.bTree = new BTreeImpl<>();
        this.dp = new DocumentPersistenceManager(null);
        this.bTree.setPersistenceManager(this.dp); //can be anything, but if its null then defualt is user.dir
        this.commandStack = new StackImpl<>();
        this.trie = new TrieImpl<>();
        this.heap = new MinHeapImpl<>();
        this.docCount = 0;
        this.byteCount = 0;
        this.docLimit = -1;
        this.byteLimit = -1;
    }
    public DocumentStoreImpl(File baseDir){
        this.bTree = new BTreeImpl<>();
        this.dp = new DocumentPersistenceManager(baseDir);
        this.bTree.setPersistenceManager(this.dp);
        this.commandStack = new StackImpl<>();
        this.trie = new TrieImpl<>();
        this.heap = new MinHeapImpl<>();
        this.docCount = 0;
        this.byteCount = 0;
        this.docLimit = -1;
        this.byteLimit = -1;
    }

    /**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream
     * is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws IOException if there is an issue reading input
     * @throws IllegalArgumentException if uri or format are null
     */
    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if(uri == null || format == null) {
            throw new IllegalArgumentException();
        }
        if(input == null){
            return callDelete(uri);
        }
        byte[] bytes = input.readAllBytes();
        input.close();

        if(format.equals(DocumentFormat.BINARY)){
            Document temp = new DocumentImpl(uri,bytes);
            return logicBlock(uri,temp);
        }else if(format.equals(DocumentFormat.TXT)){
            Document temp = new DocumentImpl(uri, new String(bytes),null);
            return logicBlock(uri,temp);
        }
        return 0;
    }

    private int logicBlock(URI uri, Document doc){
        boolean isJson = false;
        Document oldOrNull = this.bTree.get(uri);
        if(oldOrNull instanceof JsonDocument) isJson = true;
        int isLarge = checkLimitLogic(doc);//might not belong here
        GenericCommand<URI> tempCommand = createGenericCom(uri,this.bTree.get(uri),doc); //creates GC with the uri, if its a new document then the undo function is a delete
        //so this.bTree.get will return null and do it accordingly, but if its a replace then this.bTree.get will return the old document, the undo function
        //on a replace should result in the orginal document being put back and the new one getting deleted. The method also takes in doc which is the new document that
        //was just created. CONT to method for logic

        heapLogic(oldOrNull,doc);
        Document v = this.bTree.put(uri,doc);
        this.commandStack.push(tempCommand);
        addWordsToTrie(doc, v);
        if(v == null){ //meaning its new
            this.byteCount += getDocumentLength(doc);
            this.docCount++;
        }else{ //meaning its old and a replace
            this.byteCount -= getDocumentLength(v);
            this.byteCount += getDocumentLength(doc);

        }
        if(isJson){
            try {this.dp.delete(uri);}
            catch (IOException e) {throw new RuntimeException(e);}
            docCount++;
        }
        if(isLarge == -1) overFlowLogicOnGet();
        return returnValue(v);
    }
    private int checkLimitLogic(Document doc){
        Document oldOrNull = this.bTree.get(doc.getKey());
        //if(doc instanceof JsonDocument) return;
        if(this.byteLimit == -1 && this.docLimit == -1) return 1; //meaning they were never initilized
        if(this.docLimit != -1) {//if docLimit was initilized
            if(oldOrNull == null || oldOrNull instanceof JsonDocument) {//meaing its new, bc if its a replace the docCount doesnt change
                if (this.docCount + 1 > this.docLimit) {//if adding this doc will cause an overflow on docLimt
                    Document garbage = this.bTree.get(this.heap.remove().getUri());//remove only one bc we only need one extra space
                    deleteFromEverywhere(garbage);//delete it from everywhere
                }
            }
        }
        if(this.byteLimit != -1) { // if byteLimit was initilized
            if (getDocumentLength(doc) > this.byteLimit) { // if its larger then the limit
                return -1;
            }
            while(getDocumentLength(doc) + this.byteCount > this.byteLimit){//if adding will cause an overflow
                Document garbage = this.bTree.get(this.heap.remove().getUri());
                deleteFromEverywhere(garbage);
            }
        }
        return 1;
    }
    private GenericCommand<URI> createGenericCom(URI uri, Document replaceOrNull, Document doc ){
                                                //uri      //either null or old       //new doc with same uri
                                                        //null means this undo should delete the document with this uri, if its not null, then its the old value and
                                                            //so an undo shoudl return it back to that, doc is the new one that is replacing it
        if(replaceOrNull != null && replaceOrNull instanceof JsonDocument){
            Function<URI,Boolean> funcIfUndoReplacesJson = (tempUri) -> {
                this.bTree.put(uri,replaceOrNull);
                return true;
            };
            return new GenericCommand<>(uri,funcIfUndoReplacesJson);
        }
        if(replaceOrNull == null){
            Function<URI, Boolean> funcIfUndoDeletes = (tempUri) ->{
                deleteFromHeap(uri);
                this.bTree.put(uri,null);
                for(String word : doc.getWords()){
                    this.trie.delete(word, doc.getKey());
                }
                this.docCount--;
                this.byteCount -= getDocumentLength(doc);
                return true;
            };
            return new GenericCommand<URI>(uri,funcIfUndoDeletes);
        }else{
            Function<URI,Boolean> funcIfReplace = (tempUri) ->{
                //the undo function should revert to the orginal document
                deleteFromHeap(uri);
                this.bTree.put(uri,replaceOrNull);
                for(String word : doc.getWords()){
                    this.trie.delete(word,replaceOrNull.getKey());
                }
                this.byteCount -= getDocumentLength(doc);
                for(String word : replaceOrNull.getWords()){
                    this.trie.put(word,replaceOrNull.getKey());
                }
                this.byteCount += getDocumentLength(replaceOrNull);
                this.heap.insert(new DocNode(replaceOrNull.getKey(),replaceOrNull.getLastUseTime()));
                return true;
            };
            return new GenericCommand<URI>(uri,funcIfReplace);
        }
    }

    private void deleteFromHeap(URI uri){
        Document temp = this.bTree.get(uri);
        if(temp != null){
            temp.setLastUseTime(-100);
            DocNode garbageNode = new DocNode(uri,temp.getLastUseTime());
            this.heap.reHeapify(garbageNode);
            this.heap.remove();
        }
    }
    private void heapLogic(Document oldOrNull, Document newDoc){
        newDoc.setLastUseTime(nanoTime());
        DocNode tempDocNode = new DocNode(newDoc.getKey(),newDoc.getLastUseTime());
        //if its null then its new
        if(oldOrNull == null){
            this.heap.insert(tempDocNode);
            this.heap.reHeapify(tempDocNode);
        }else{
            //its a replace
            if(!(oldOrNull instanceof JsonDocument)) {
                deleteFromHeap(oldOrNull.getKey());
            }
            this.heap.insert(tempDocNode);
            this.heap.reHeapify(tempDocNode);
        }
    }


    private void addWordsToTrie(Document doc, Document v){
        if(returnValue(v) != 0){
            //it already existed and this is a replace then delete its value from all its old words
            Set<String> words = doc.getWords();
            for(String word : words){
                this.trie.delete(word,doc.getKey());
            }
        }
        //if its new add it all to the trie if its old still add all the words.
        Set<String> words = doc.getWords();
        for(String word : words){
            this.trie.put(word,doc.getKey());
        }
    }

    private int callDelete(URI uri){
        if(bTreeContainsKey(uri)){
            Document temp = this.bTree.get(uri);
            delete(uri);
            return temp.hashCode();
        }
        return 0;
    }

    public boolean bTreeContainsKey(URI key){
        if(this.bTree.get(key) != null){
            return true;
        }else{
            return false;
        }
    }

    private int returnValue(Document v){
        if(v == null){
            return 0;
        }else{
            return v.hashCode();
        }
    }

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public Document get(URI uri) {
        if(bTreeContainsKey(uri)) {
            Document t = this.bTree.get(uri);
            if(t instanceof JsonDocument){
                t = jsonDocLogic(uri);
            }
            t.setLastUseTime(nanoTime());
            DocNode tempDocNode = new DocNode(uri,t.getLastUseTime());
            this.heap.reHeapify(tempDocNode);
            overFlowLogicOnGet();
            return t;
        }
        return null;
    }

    private Document jsonDocLogic(URI uri){
        Document jsConvert = null;
        try {
            jsConvert = this.dp.deserialize(uri);
            this.dp.delete(uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(jsConvert != null) {
            this.bTree.put(uri, jsConvert);
            this.heap.insert(new DocNode(uri,jsConvert.getLastUseTime()));
            addToTrie(jsConvert.getKey());
            this.docCount++;
            this.byteCount += getDocumentLength(jsConvert);
            return jsConvert;

        }
        return null;
    }

    private void overFlowLogicOnGet(){
        if(this.docLimit != -1){//meaning it was initizlied
            setMaxDocumentCount(this.docLimit);
        }
        if(this.byteLimit != -1){//meaning it was inizlized
            setMaxDocumentBytes(this.byteLimit);
        }
    }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean delete(URI uri) {
        if(bTreeContainsKey(uri)){
            //used containsKey so it is known already that it exists in the HT, so put null with it so it deletes.
            Document tempDoc = this.bTree.get(uri);
            Function<URI, Boolean> func = (tempUri) ->{
                this.bTree.put(uri,tempDoc);
                addToTrie(uri);
                DocNode tempDocNode = new DocNode(uri,0);
                this.heap.insert(tempDocNode);
                return true;
            };
            GenericCommand<URI> tempCommand = new GenericCommand<>(uri, func);
            this.commandStack.push(tempCommand);
            deleteFromTrie(uri);
            deleteFromHeap(uri);
            this.bTree.put(uri,null);
            this.docCount--;
            this.byteCount -= getDocumentLength(tempDoc);
            return true;
        }
        //returns false if the document doesn't exist
        return false;
    }

    private void addToTrie(URI uri){
        Document doc = this.bTree.get(uri);
        Set<String> words = doc.getWords();
        for(String word : words){
            this.trie.put(word,doc.getKey());
        }
    }
    private void deleteFromTrie(URI uri){
        Document doc = this.bTree.get(uri);
        Set<String> words = doc.getWords();
        for(String word : words){
            this.trie.delete(word,doc.getKey());
        }
    }
    /**
     * undo the last put or delete command
     *
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    @Override
    public void undo() throws IllegalStateException {
        if(this.commandStack.size() == 0){
            throw new IllegalStateException();
        }
        Undoable temp = this.commandStack.pop();
        if(temp instanceof CommandSet<?>){
            CommandSet<URI> tempAsCmdSet = (CommandSet<URI>) temp;
            Iterator<GenericCommand<URI>> tempIterator = tempAsCmdSet.iterator();
            Set<GenericCommand<URI>> setOfCmds = new HashSet<>();
            while(tempIterator.hasNext()){
                setOfCmds.add(tempIterator.next());
            }
            tempAsCmdSet.undo();
            long nanoTime = nanoTime();
            for(GenericCommand<URI> gc : setOfCmds){
                Document tempDoc = this.bTree.get(gc.getTarget());
                tempDoc.setLastUseTime(nanoTime);
                DocNode tempDocNode = new DocNode(tempDoc.getKey(),tempDoc.getLastUseTime());
                this.heap.reHeapify(tempDocNode);
                this.docCount++;
                this.byteCount += getDocumentLength(tempDoc);
            }
        }else{
            GenericCommand<URI> tempGC = (GenericCommand<URI>) temp;
            Document nullOrold = this.bTree.get(tempGC.getTarget());
            tempGC.undo();
            Document tempDoc = this.bTree.get(tempGC.getTarget());
            if(tempDoc != null){
                tempDoc.setLastUseTime(nanoTime());
                DocNode tempDocNode = new DocNode(tempDoc.getKey(),tempDoc.getLastUseTime());
                this.heap.reHeapify(tempDocNode);
                if(nullOrold == null){//if its null that means in undoing a delete else its undoing a replace
                    this.docCount++;
                    this.byteCount += getDocumentLength(tempDoc);
                }
            }
            overloadingCheckAfterUndo();
            return;
        }
        temp.undo();
        overloadingCheckAfterUndo();
    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     *
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    @Override
    public void undo(URI uri) throws IllegalStateException {
        if(this.commandStack.size() == 0){
            throw new IllegalStateException();
        }
        StackImpl<Undoable> tempStack = new StackImpl<>();
        boolean found = false;
        while(this.commandStack.size() != 0){
            Undoable tempCommand = this.commandStack.pop();
            if(tempCommand instanceof CommandSet<?>){
                if(undoOnCommandSet((CommandSet) tempCommand, uri)){
                    if( ((CommandSet<?>) tempCommand).size() >= 1){
                        this.commandStack.push(tempCommand);
                    }
                    found = true;
                    break;
                }
            }else{
                GenericCommand<URI> tempComAsGen = (GenericCommand<URI>) tempCommand;
                if(tempComAsGen.getTarget().equals(uri)){
                    Document nullOrold = this.bTree.get(tempComAsGen.getTarget());
                    tempCommand.undo();
                    Document temp = this.bTree.get(uri);
                    if(temp != null){//meanings its either an undo on a delete or an undo on a reaplace
                        temp.setLastUseTime(nanoTime());
                        DocNode tempDocNode = new DocNode(temp.getKey(),temp.getLastUseTime());
                        this.heap.reHeapify(tempDocNode);
                        if(nullOrold == null){//if its null that means in undoing a delete else its undoing a replace
                            this.docCount++;
                            this.byteCount += getDocumentLength(temp);
                        }
                    }
                    found = true;
                    break;
                }
            }
            tempStack.push(tempCommand);
        }
        while(tempStack.size() != 0){
            this.commandStack.push(tempStack.pop());
        }
        if(found == false){
            throw new IllegalStateException();
        }
        overloadingCheckAfterUndo();
    }
    private void overloadingCheckAfterUndo(){
        if(this.byteLimit == -1 && this.docLimit == -1) return;
        if(this.docLimit != -1){
            while(this.docCount > this.docLimit){
                Document garbage = this.bTree.get(this.heap.remove().getUri());
                deleteFromEverywhere(garbage);
            }
        }
        if(this.byteLimit != -1){
            while(this.byteCount > this.byteLimit){
                Document garbage = this.bTree.get(this.heap.remove().getUri());
                deleteFromEverywhere(garbage);
            }
        }
    }

    private Boolean undoOnCommandSet(CommandSet cmdSet, URI uri){
        if(!cmdSet.containsTarget(uri)){
            return false;
        }
        Document nullOrold = this.bTree.get(uri);
        Boolean results = cmdSet.undo(uri);
        Document temp = this.bTree.get(uri);
        if(temp != null){
            temp.setLastUseTime(nanoTime());
            DocNode tempDocNode = new DocNode(temp.getKey(), temp.getLastUseTime());
            this.heap.reHeapify(tempDocNode);
            if(nullOrold == null){//if its null that means in undoing a delete else its undoing a replace
                this.docCount++;
                this.byteCount += getDocumentLength(temp);
            }
        }
        return results;
    }
    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     *
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> search(String keyword) {
        List<URI> tempURIList = this.trie.getAllSorted(keyword,Comparator.naturalOrder());
        List<Document> tempList = new ArrayList<>();
        for(URI uri : tempURIList){
            Document temp = this.bTree.get(uri);
            if(temp instanceof JsonDocument){
                temp = jsonDocLogic(uri);
            }
            tempList.add(temp);
        }
        tempList.sort(new docComp(keyword).reversed());
        setListOfDocsNanoTime(tempList);
        overFlowLogicOnGet();
        return tempList;
    }

    private void setListOfDocsNanoTime(List<Document> docs){
        long nanoTime = nanoTime();
        for(Document doc : docs){
            doc.setLastUseTime(nanoTime);
            DocNode tempDocNode = new DocNode(doc.getKey(),doc.getLastUseTime());
            this.heap.reHeapify(tempDocNode);
        }
    }
    /**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE SENSITIVE.
     *
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        Comparator<Document> tempComp = new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                return Integer.compare(prefCount(o1,keywordPrefix),prefCount(o2, keywordPrefix));
            }
            private int prefCount(Document doc, String pre){
                Set<String> words = doc.getWords();
                int prefixCount = 0;
                for(String word : words){
                    if(word.startsWith(pre)){
                        prefixCount += doc.wordCount(word);
                    }
                }
                return prefixCount;
            }
        };
        List<URI> tempURIList = this.trie.getAllWithPrefixSorted(keywordPrefix,Comparator.naturalOrder());
        List<Document> tempList = new ArrayList<>();
        for(URI uri : tempURIList){
            Document temp = this.bTree.get(uri);
            if(temp instanceof JsonDocument){
                temp = jsonDocLogic(uri);
            }
            tempList.add(temp);
        }
        tempList.sort(tempComp.reversed());
        setListOfDocsNanoTime(tempList);
        overFlowLogicOnGet();
        return tempList;
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * Search is CASE SENSITIVE.
     *
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAll(String keyword){
        Set<URI> docs = this.trie.deleteAll(keyword);
        CommandSet<URI> tempCommandSet = new CommandSet<>();
        Set<URI> uris = new HashSet<>();
        for(URI doc : docs){
            GenericCommand<URI> tempGenericCom = new GenericCommand<>(doc,createTrieDeleteFunction((DocumentImpl) this.bTree.get(doc)));
            tempCommandSet.addCommand(tempGenericCom);
            uris.add(doc);
            deleteFromTrie(doc);
            deleteFromHeap(doc);
            this.bTree.put(doc,null);
            this.docCount--;
            this.byteCount -= getDocumentLength(this.bTree.get(doc));

        }
        this.commandStack.push(tempCommandSet);
        return uris;
    }

    private Function<URI,Boolean> createTrieDeleteFunction(DocumentImpl doc){
        Function<URI,Boolean> result = (tempUri) ->{
            Set<String> words = doc.getWords();
            for(String word : words){
                this.trie.put(word,doc.getKey());
            }
            this.bTree.put(doc.getKey(),doc);
            this.heap.insert(new DocNode(doc.getKey(),doc.getLastUseTime()));
            return true;
        };
        return result;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE SENSITIVE.
     *
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Set<URI> docs = this.trie.deleteAllWithPrefix(keywordPrefix);
        Set<URI> uris = new HashSet<>();
        CommandSet<URI> tempCommandSet = new CommandSet<>();
        for(URI doc : docs){
            Set<String> words = this.bTree.get(doc).getWords();
            Set<String> wordsWithPrefix = new HashSet<>();
            for(String word : words){
                if(word.startsWith(keywordPrefix)) {wordsWithPrefix.add(word);}
            }
            GenericCommand<URI> tempGenericCommand = new GenericCommand<>(doc,createTrieDeleteFunction((DocumentImpl) this.bTree.get(doc)));
            tempCommandSet.addCommand(tempGenericCommand);
            uris.add(doc);
            deleteFromTrie(doc);
            deleteFromHeap(doc);
            this.bTree.put(doc,null);
            this.docCount--;
            this.byteCount -= getDocumentLength(this.bTree.get(doc));
        }
        this.commandStack.push(tempCommandSet);
        return uris;
    }

    /**
     * set maximum number of documents that may be stored
     *
     * @param limit
     */
    @Override
    public void setMaxDocumentCount(int limit) {
        if(limit < 0) throw new IllegalArgumentException("Limit must not be negative");
        this.docLimit = limit;
        if(this.docCount > limit){
            while(this.docCount > limit){
                Document garbage = this.bTree.get(this.heap.remove().getUri());
                deleteFromEverywhere(garbage);
            }
        }
    }

    private void deleteFromEverywhere(Document garbage){
        //deleteFromTrie(garbage.getKey());

        try {
            this.bTree.moveToDisk(garbage.getKey());
            this.bTree.put(garbage.getKey(),new JsonDocument(garbage.getKey()));
        }
        catch (Exception e) {throw new RuntimeException(e);}

        this.docCount--;
        this.byteCount -= getDocumentLength(garbage);
    }




    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     *
     * @param limit
     */
    @Override
    public void setMaxDocumentBytes(int limit) {
        if(limit < 0) throw new IllegalArgumentException("Limit must not be negative");
        this.byteLimit = limit;
        if(this.byteCount > limit){
            while(this.byteCount > limit){
                Document garbage = this.bTree.get(this.heap.remove().getUri());
                deleteFromEverywhere(garbage);
            }
        }
    }

    private int getDocumentLength(Document doc){
        if(doc == null) return 0;
        if(doc.getDocumentTxt() == null){
            return doc.getDocumentBinaryData().length;
        } else if (doc.getDocumentBinaryData() == null) {
            return doc.getDocumentTxt().getBytes().length;
        }
        return 0;
    }
    private class docComp implements Comparator<Document>{
        private String s;
        private docComp(String s){
            this.s = s;
        }
        @Override
        public int compare(Document doc1, Document doc2){
            return Integer.compare(doc1.wordCount(this.s), doc2.wordCount(this.s));
        }
    }

    private class DocNode implements Comparable<DocNode>{

        private URI uri;
        private long timeInNano;
        public DocNode(URI uri,long timeInNano){
            this.uri = uri;
            this.timeInNano = timeInNano;
        }
        private URI getUri() {
            return uri;
        }
        private void setUri(URI uri) {
            this.uri = uri;
        }

        private long getTimeInNano() {
            return timeInNano;
        }

        private void setTimeInNano(long timeInNano) {
            this.timeInNano = timeInNano;
        }

        @Override
        public int compareTo(DocNode o) {
            return Long.compare(timeInNano,o.getTimeInNano());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DocNode docNode)) return false;
            return Objects.equals(getUri(), docNode.getUri());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getUri());
        }
    }

    private class JsonDocument implements Document{

        private URI uri;

        public JsonDocument(URI uri){
            this.uri = uri;
        }

        @Override
        public URI getKey() {
            return this.uri;
        }
        @Override
        public String getDocumentTxt() {
            return null;
        }
        @Override
        public byte[] getDocumentBinaryData() {
            return new byte[0];
        }
        @Override
        public int wordCount(String word) {
            return 0;
        }
        @Override
        public Set<String> getWords() {
            return null;
        }
        @Override
        public long getLastUseTime() {
            return 0;
        }
        @Override
        public void setLastUseTime(long timeInNanoseconds) {

        }
        @Override
        public Map<String, Integer> getWordMap() {
            return null;
        }
        @Override
        public void setWordMap(Map<String, Integer> wordMap) {

        }
        @Override
        public int compareTo(Document o) {
            return 0;
        }
    }
}
