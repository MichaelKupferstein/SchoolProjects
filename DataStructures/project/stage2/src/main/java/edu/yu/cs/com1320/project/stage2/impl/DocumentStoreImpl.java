package edu.yu.cs.com1320.project.stage2.impl;
import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage2.Document;
import edu.yu.cs.com1320.project.stage2.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore{
    private HashTableImpl<URI,DocumentImpl> hashTable;
    private StackImpl<Command> commandStack;

    public DocumentStoreImpl() {
        this.hashTable = new HashTableImpl<>();
        this.commandStack = new StackImpl<>();
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
            if(this.hashTable.containsKey(uri)){
                DocumentImpl temp = this.hashTable.get(uri);
                delete(uri);
                return temp.hashCode();
            }
            return 0;
        }
        byte[] bytes = input.readAllBytes();
        input.close();
        Function<URI, Boolean> func = (tempUri) -> {
            this.hashTable.put(uri,null);
            return true;
        };
        Command tempCommand = new Command(uri, func);
        if(format.equals(DocumentFormat.BINARY)){
            DocumentImpl temp = new DocumentImpl(uri,bytes);
            DocumentImpl v = this.hashTable.put(uri,temp);
            this.commandStack.push(tempCommand);
            if(v == null){
                return 0;
            }else{
                return v.hashCode();
            }
        }else if(format.equals(DocumentFormat.TXT)){
            DocumentImpl temp = new DocumentImpl(uri, new String(bytes));
            DocumentImpl v = this.hashTable.put(uri,temp);
            this.commandStack.push(tempCommand);
            if(v == null){
                return 0;
            }else{
                return v.hashCode();
            }
        }
        return 0;
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
            Command tempCommand = new Command(uri, func);
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
        StackImpl<Command> tempStack = new StackImpl<>();
        boolean found = false;
        while(this.commandStack.size() != 0){
            Command tempCommand = this.commandStack.pop();
            if(tempCommand.getUri().equals(uri)){
                tempCommand.undo();
                found = true;
                break;
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

}
