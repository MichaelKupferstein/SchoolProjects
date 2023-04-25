package edu.yu.cs.com1320.project.stage4.impl;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Function;
import static java.lang.System.nanoTime;

public class DocumentStoreImpl implements DocumentStore{
    private HashTableImpl<URI,Document> hashTable;
    private StackImpl<Undoable> commandStack;
    private TrieImpl<Document> trie;
    private MinHeapImpl<Document> heap;
    private int docLimit, byteLimit, docCount, byteCount;

    public DocumentStoreImpl() {
        this.hashTable = new HashTableImpl<>();
        this.commandStack = new StackImpl<>();
        this.trie = new TrieImpl<>();
        this.heap = new MinHeapImpl<>();
        this.docCount = 0;
        this.byteCount = 0;
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
            Document temp = new DocumentImpl(uri, new String(bytes));
            return logicBlock(uri,temp);
        }
        return 0;
    }

    private int logicBlock(URI uri, Document doc){
        Document oldOrNull = this.hashTable.get(uri);
        checkLimitLogic(doc,oldOrNull);//might not belong here
        GenericCommand<URI> tempCommand = createGenericCom(uri,this.hashTable.get(uri),doc); //creates GC with the uri, if its a new document then the undo function is a delete
        //so this.hashTable.get will return null and do it accordingly, but if its a replace then this.hashTable.get will return the old document, the undo function
        //on a replace should result in the orginal document being put back and the new one getting deleted. The method also takes in doc which is the new document that
        //was just created. CONT to method for logic
        heapLogic(oldOrNull,doc);
        Document v = this.hashTable.put(uri,doc);
        this.commandStack.push(tempCommand);
        addWordsToTrie(doc, v);
        if(v == null){ //meaning its new
            this.byteCount += getDocumentLength(doc);
            this.docCount++;
        }else{ //meaning its old and a replace
            this.byteCount -= getDocumentLength(v);
            this.byteCount += getDocumentLength(doc);
        }
        return returnValue(v);
    }
    private void checkLimitLogic(Document doc,Document oldOrNull){
        if(this.byteLimit == 0 && this.docLimit == 0) return; //meaning they were never initilized
        if(this.docLimit != 0) {//if docLimit was initilized
            if(oldOrNull == null) {//meaing its new, bc if its a replace the docCount doesnt change
                if (this.docCount + 1 > this.docLimit) {//if adding this doc will cause an overflow on docLimt
                    Document garbage = this.heap.remove();//remove only one bc we only need one extra space
                    deleteFromEverywhere(garbage);//delete it from everywhere
                }
            }
        }
        if(this.byteLimit != 0) { // if byteLimit was initilized
            if (getDocumentLength(doc) > this.byteLimit) { // if its larger then the limit
                throw new IllegalArgumentException("Document larger then limit, Document length: " + getDocumentLength(doc) + " limit: " + this.byteLimit);
            }
            while(getDocumentLength(doc) + this.byteCount > this.byteLimit){//if adding will cause an overflow
                Document garbage = this.heap.remove();
                deleteFromEverywhere(garbage);
            }
        }

    }
    private GenericCommand<URI> createGenericCom(URI uri, Document replaceOrNull, Document doc ){
                                                //uri      //either null or old       //new doc with same uri
                                                        //null means this undo should delete the document with this uri, if its not null, then its the old value and
                                                            //so an undo shoudl return it back to that, doc is the new one that is replacing it
        if(replaceOrNull == null){
            Function<URI, Boolean> funcIfUndoDeletes = (tempUri) ->{
                deleteFromHeap(uri);
                this.hashTable.put(uri,null);
                for(String word : doc.getWords()){
                    this.trie.delete(word, doc);
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
                this.hashTable.put(uri,replaceOrNull);
                for(String word : doc.getWords()){
                    this.trie.delete(word,replaceOrNull);
                }
                this.byteCount -= getDocumentLength(doc);
                for(String word : replaceOrNull.getWords()){
                    this.trie.put(word,replaceOrNull);
                }
                this.byteCount += getDocumentLength(replaceOrNull);
                this.heap.insert(replaceOrNull);
                return true;
            };
            return new GenericCommand<URI>(uri,funcIfReplace);
        }
    }

    private void deleteFromHeap(URI uri){
        Document temp = this.hashTable.get(uri);
        if(temp != null){
            temp.setLastUseTime(-100);
            this.heap.reHeapify(temp);
            this.heap.remove();
        }
    }
    private void heapLogic(Document oldOrNull, Document newDoc){
        newDoc.setLastUseTime(nanoTime());
        //if its null then its new
        if(oldOrNull == null){
            this.heap.insert(newDoc);
            this.heap.reHeapify(newDoc);
        }else{
            //its a replace
            deleteFromHeap(oldOrNull.getKey());
            this.heap.insert(newDoc);
            this.heap.reHeapify(newDoc);
        }
    }


    private void addWordsToTrie(Document doc, Document v){
        if(returnValue(v) != 0){
            //it already existed and this is a replace then delete its value from all its old words
            Set<String> words = doc.getWords();
            for(String word : words){
                this.trie.delete(word,doc);
            }
        }
        //if its new add it all to the trie if its old still add all the words.
        Set<String> words = doc.getWords();
        for(String word : words){
            this.trie.put(word,doc);
        }
    }

    private int callDelete(URI uri){
        if(this.hashTable.containsKey(uri)){
            Document temp = this.hashTable.get(uri);
            delete(uri);
            return temp.hashCode();
        }
        return 0;
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
        if(this.hashTable.containsKey(uri)) {
            Document t = this.hashTable.get(uri);
            t.setLastUseTime(nanoTime());
            this.heap.reHeapify(t);
            return t;
        }
        return null;
    }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean delete(URI uri) {
        if(this.hashTable.containsKey(uri)){
            //used containsKey so it is known already that it exists in the HT, so put null with it so it deletes.
            Document tempDoc = this.hashTable.get(uri);
            Function<URI, Boolean> func = (tempUri) ->{
                this.hashTable.put(uri,tempDoc);
                addToTrie(uri);
                this.heap.insert(tempDoc);
                return true;
            };
            GenericCommand<URI> tempCommand = new GenericCommand<>(uri, func);
            this.commandStack.push(tempCommand);
            deleteFromTrie(uri);
            deleteFromHeap(uri);
            this.hashTable.put(uri,null);
            return true;
        }
        //returns false if the document doesn't exist
        return false;
    }

    private void addToTrie(URI uri){
        Document doc = this.hashTable.get(uri);
        Set<String> words = doc.getWords();
        for(String word : words){
            this.trie.put(word,doc);
        }
    }
    private void deleteFromTrie(URI uri){
        Document doc = this.hashTable.get(uri);
        Set<String> words = doc.getWords();
        for(String word : words){
            this.trie.delete(word,doc);
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
            Iterator<?> iterator = ((CommandSet<?>) temp).iterator();
            while(iterator.hasNext()){
                Object o = iterator.next();
                GenericCommand<URI> tempGC = (GenericCommand<URI>) o;
                Document tempDoc = this.hashTable.get(tempGC.getTarget());
                if(tempDoc != null){
                    tempDoc.setLastUseTime(nanoTime());
                }
            }
        }else{
            GenericCommand<URI> tempGC = (GenericCommand<URI>) temp;
            Document tempDoc = this.hashTable.get(tempGC.getTarget());
            if(tempDoc != null){
                tempDoc.setLastUseTime(nanoTime());
            }
        }
        temp.undo();
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
                    tempCommand.undo();
                    Document temp = this.hashTable.get(uri);
                    if(temp != null){
                        temp.setLastUseTime(nanoTime());
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
    }

    private Boolean undoOnCommandSet(CommandSet cmdSet, URI uri){
        if(!cmdSet.containsTarget(uri)){
            return false;
        }
        Boolean results = cmdSet.undo(uri);
        Document temp = this.hashTable.get(uri);
        if(temp != null){
            temp.setLastUseTime(nanoTime());
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
        List<Document> tempList = this.trie.getAllSorted(keyword,new docComp(keyword).reversed());
        setListOfDocsNanoTime(tempList);
        return tempList;
    }

    private void setListOfDocsNanoTime(List<Document> docs){
        for(Document doc : docs){
            doc.setLastUseTime(nanoTime());
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
        List<Document> tempList = this.trie.getAllWithPrefixSorted(keywordPrefix,tempComp.reversed());
        setListOfDocsNanoTime(tempList);
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
        Set<Document> docs = this.trie.deleteAll(keyword);
        CommandSet<URI> tempCommandSet = new CommandSet<>();
        Set<URI> uris = new HashSet<>();
        for(Document doc : docs){
            GenericCommand<URI> tempGenericCom = new GenericCommand<>(doc.getKey(),createTrieDeleteFunction((DocumentImpl) doc));
            tempCommandSet.addCommand(tempGenericCom);
            uris.add(doc.getKey());
            deleteFromTrie(doc.getKey());
            deleteFromHeap(doc.getKey());
            this.hashTable.put(doc.getKey(),null);

        }
        this.commandStack.push(tempCommandSet);
        return uris;
    }

    private Function<URI,Boolean> createTrieDeleteFunction(DocumentImpl doc){
        Function<URI,Boolean> result = (tempUri) ->{
            Set<String> words = doc.getWords();
            for(String word : words){
                this.trie.put(word,doc);
            }
            this.hashTable.put(doc.getKey(),doc);
            this.heap.insert(doc);
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
        Set<Document> docs = this.trie.deleteAllWithPrefix(keywordPrefix);
        Set<URI> uris = new HashSet<>();
        CommandSet<URI> tempCommandSet = new CommandSet<>();
        for(Document doc : docs){
            Set<String> words = doc.getWords();
            Set<String> wordsWithPrefix = new HashSet<>();
            for(String word : words){
                if(word.startsWith(keywordPrefix)) {wordsWithPrefix.add(word);}
            }
            GenericCommand<URI> tempGenericCommand = new GenericCommand<>(doc.getKey(),createTrieDeleteFunction((DocumentImpl) doc));
            tempCommandSet.addCommand(tempGenericCommand);
            uris.add(doc.getKey());
            deleteFromTrie(doc.getKey());
            deleteFromHeap(doc.getKey());
            this.hashTable.put(doc.getKey(),null);
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
        if(limit <= 0) throw new IllegalArgumentException("Limit must be greater than 0");
        this.docLimit = limit;
        if(this.docCount > limit){
            while(this.docCount > limit){
                Document garbage = this.heap.remove();
                deleteFromEverywhere(garbage);
            }
        }
    }

    private void deleteFromEverywhere(Document garbage){
        deleteFromTrie(garbage.getKey());
        deleteFromCommandStack(garbage);
        this.hashTable.put(garbage.getKey(),null);
        this.docCount--;
        this.byteCount -= getDocumentLength(garbage);
    }

    private void deleteFromCommandStack(Document doc){
        StackImpl<Undoable> tempStack = new StackImpl<>();
        boolean found = false;
        while(this.commandStack.size() != 0){
            Undoable tempCommand = this.commandStack.pop();
            if(tempCommand instanceof CommandSet<?>){
                CommandSet<URI> tempAsCmdSet = (CommandSet<URI>) tempCommand;
                if(tempAsCmdSet.containsTarget(doc.getKey())) {
                    deleteFromCommandSet(tempAsCmdSet, doc.getKey());
                    if (tempAsCmdSet.size() >= 1) {
                        this.commandStack.push(tempCommand);
                    }
                    found = true;
                    break;
                }
            }else{
                GenericCommand<URI> tempAsGC = (GenericCommand<URI>) tempCommand;
                if(tempAsGC.getTarget().equals(doc.getKey())){
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
    }

    private void deleteFromCommandSet(CommandSet<URI> cmdSet, URI uri){
            Iterator<GenericCommand<URI>> iterator = cmdSet.iterator();
            while(iterator.hasNext()){
                GenericCommand<URI> temp = iterator.next();
                if(temp.getTarget().equals(uri)){
                    iterator.remove();
                }
            }
    }
    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     *
     * @param limit
     */
    @Override
    public void setMaxDocumentBytes(int limit) {
        if(limit <= 0) throw new IllegalArgumentException("Limit must be greater than 0");
        this.byteLimit = limit;
        if(this.byteCount > limit){
            while(this.byteCount > limit){
                Document garbage = this.heap.remove();
                deleteFromEverywhere(garbage);
            }
        }
    }

    private int getDocumentLength(Document doc){
        if(doc == null) return 0;
        if(doc.getWords() == null){
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
}
