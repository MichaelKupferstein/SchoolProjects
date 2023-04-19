package edu.yu.cs.com1320.project.stage4.impl;
import edu.yu.cs.com1320.project.stage4.Document;

import java.net.URI;
import java.util.*;

public class DocumentImpl implements Document{

    private URI uri;
    private String txt;
    private byte[] binaryData;
    private Map<String,Integer> words;
    public DocumentImpl(URI uri, String txt) {
        if(uri == null || txt == null){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.txt = txt;
        this.words = new HashMap<>();
        this.addWords();
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
        if(!this.words.containsKey(word)) return 0;
        return this.words.get(word);
    }

    /**
     * @return all the words that appear in the document
     */
    @Override
    public Set<String> getWords() {
        if(this.txt == null){
            return Collections.emptySet();
        }
        return this.words.keySet();
    }

    private void addWords(){
        String cleaned = this.txt.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("[\r\n]", " ");
        String[] words = cleaned.split(" ");
        for(String word : words){
            if(word.equals(" ") || word.equals("")){
                continue;
            }
            if(this.words.containsKey(word)){
                int wordCount = this.words.get(word);
                this.words.put(word,wordCount + 1);
            }else{
                this.words.put(word,1);
            }
        }
    }

    /**
     * return the last time this document was used, via put/get or via a search result
     * (for stage 4 of project)
     */
    @Override
    public long getLastUseTime() {
        return 0;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds) {

    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure {@link Integer#signum
     * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
     * all {@code x} and {@code y}.  (This implies that {@code
     * x.compareTo(y)} must throw an exception if and only if {@code
     * y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code
     * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
     * == signum(y.compareTo(z))}, for all {@code z}.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     * @apiNote It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     */
    @Override
    public int compareTo(Document o) {
        return 0;
    }
}
