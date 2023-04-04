package edu.yu.cs.com1320.project.stage3.impl;
import edu.yu.cs.com1320.project.stage3.Document;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;

public class DocumentImpl implements Document{

    private URI uri;
    private String txt;
    private byte[] binaryData;
    public DocumentImpl(URI uri, String txt) {
        if(uri == null || txt == null){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.txt = txt;
    }
    public DocumentImpl(URI uri, byte[] binaryData){
        if(uri == null || binaryData == null){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = binaryData;
    }

    /**
     * @return content of text document
     */
    @Override
    public String getDocumentTxt() {
        return this.txt;
    }

    /**
     * @return content of binary data document
     */
    @Override
    public byte[] getDocumentBinaryData() {
        return this.binaryData;
    }

    /**
     * @return URI which uniquely identifies this document
     */
    @Override
    public URI getKey() {
        return this.uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentImpl document = (DocumentImpl) o;
        return this.hashCode() == document.hashCode();
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (this.txt != null ? this.txt.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return result;
    }

    /**
     * how many times does the given word appear in the document?
     *
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    @Override
    public int wordCount(String word) {
        //if its binary
        if(this.txt == null) return 0;
        return 1;
    }

    /**
     * @return all the words that appear in the document
     */
    @Override
    public Set<String> getWords() {
        return null;
    }
}
