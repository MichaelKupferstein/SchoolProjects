package edu.yu.cs.com1320.project.stage1.impl;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage1.DocumentStore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DocumentStoreImpl implements DocumentStore{
    HashTableImpl<URI,DocumentImpl> hashTable;
    public DocumentStoreImpl() {
        this.hashTable = new HashTableImpl<>();
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
            if (delete(uri) == false) return 0;
            return this.hashTable.put(uri,null).hashCode();

        }
        byte[] bytes = input.readAllBytes();
        input.close();
        if(format.equals(DocumentFormat.BINARY)){
            DocumentImpl temp = new DocumentImpl(uri,bytes);
            DocumentImpl v = this.hashTable.put(uri,temp);
            if(v == null){
                return 0;
            }else{
                return v.hashCode();
            }
        }else if(format.equals(DocumentFormat.TXT)){
            DocumentImpl temp = new DocumentImpl(uri, new String(bytes));
            DocumentImpl v = this.hashTable.put(uri,temp);
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
        return this.hashTable.get(uri);
    }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean delete(URI uri) {
        if(this.hashTable.get(uri) == null) return false;
        return true;
    }

}
