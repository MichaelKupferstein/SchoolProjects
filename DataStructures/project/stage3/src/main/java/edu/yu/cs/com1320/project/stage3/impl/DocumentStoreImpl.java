package edu.yu.cs.com1320.project.stage3.impl;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;

import javax.print.Doc;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore{
    private HashTableImpl<URI,DocumentImpl> hashTable;
    private StackImpl<Undoable> commandStack;
    private TrieImpl<Document> trie;

    public DocumentStoreImpl() {
        this.hashTable = new HashTableImpl<>();
        this.commandStack = new StackImpl<>();
        this.trie = new TrieImpl<>();
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
        Function<URI, Boolean> func = createFunction(uri,null);
        if(this.hashTable.containsKey(uri)){
            DocumentImpl tempDoc = this.hashTable.get(uri);
            func = createFunction(uri, tempDoc);
        }
        GenericCommand tempCommand = new GenericCommand(uri, func);
        if(format.equals(DocumentFormat.BINARY)){
            DocumentImpl temp = new DocumentImpl(uri,bytes);
            DocumentImpl v = this.hashTable.put(uri,temp);
            this.commandStack.push(tempCommand);
            addWordsToTrie(temp, v);
            return returnValue(v);
        }else if(format.equals(DocumentFormat.TXT)){
            DocumentImpl temp = new DocumentImpl(uri, new String(bytes));
            DocumentImpl v = this.hashTable.put(uri,temp);
            this.commandStack.push(tempCommand);
            addWordsToTrie(temp, v);
            return returnValue(v);
        }
        return 0;
    }

    private void addWordsToTrie(DocumentImpl doc, DocumentImpl v){
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
    private Function<URI,Boolean> createFunction(URI uri, DocumentImpl doc){
        Function<URI, Boolean> func = (tempUri) -> {
            this.hashTable.put(uri,doc);
            return true;
        };
        return func;
    }
    private int callDelete(URI uri){
        if(this.hashTable.containsKey(uri)){
            DocumentImpl temp = this.hashTable.get(uri);
            delete(uri);
            return temp.hashCode();
        }
        return 0;
    }

    private int returnValue(DocumentImpl v){
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
            return this.hashTable.get(uri);
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
            DocumentImpl tempDoc = this.hashTable.get(uri);
            Function<URI, Boolean> func = (tempUri) ->{
                this.hashTable.put(uri,tempDoc);
                return true;
            };
            GenericCommand tempCommand = new GenericCommand(uri, func);
            this.commandStack.push(tempCommand);
            this.hashTable.put(uri,null);
            return true;
        }
        //returns false if the document doesn't exist
        return false;
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
        this.commandStack.pop().undo();
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
                undoOnCommandSet((CommandSet) tempCommand, uri);
            }else{
                GenericCommand tempComAsGen = (GenericCommand) tempCommand;
                if(tempComAsGen.getTarget().equals(uri)){
                    tempCommand.undo();
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
        for(int i = 0; i < tempStack.size(); i++){
            this.commandStack.push(tempStack.pop());
        }
    }

    private void undoOnCommandSet(CommandSet cmdSet, URI uri){
        if(!cmdSet.containsTarget(uri)){
            return;
        }
        cmdSet.undo(uri);
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
        return this.trie.getAllSorted(keyword,new docComp(keyword));
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
        return this.trie.getAllWithPrefixSorted(keywordPrefix,tempComp);
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
        for(Document doc : docs){
            GenericCommand<URI> tempGenericCom = new GenericCommand<>(doc.getKey(),createTrieDeleteFunction(doc,keyword));
            tempCommandSet.addCommand(tempGenericCom);
        }
        Set<URI> uris = new HashSet<>();
        for(Document doc : docs){
            uris.add(doc.getKey());
        }
        return uris;
    }

    private Function<URI,Boolean> createTrieDeleteFunction(Document doc, String keyword){
        Function<URI,Boolean> result = (tempUri) ->{
            this.trie.put(keyword,doc);
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
        for(Document doc : docs){
            uris.add(doc.getKey());
        }
        return uris;
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
