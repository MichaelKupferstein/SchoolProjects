package edu.yu.cs.com1320.project.stage5.impl;
import edu.yu.cs.com1320.project.stage5.Document;

import java.net.URI;
import java.util.*;

public class DocumentImpl implements Document{

    private URI uri;
    private String txt;
    private byte[] binaryData;
    private Map<String,Integer> words;
    private long timeInNano;
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

    public DocumentImpl(URI uri, String documentContent, Map<String, Integer> wordMap) {
        if(uri == null || documentContent == null){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.txt = documentContent;
        if(wordMap != null) {
            this.words = wordMap;
            this.addWords();
        }

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
        return this.timeInNano;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        this.timeInNano = timeInNanoseconds;
    }

    @Override
    public Map<String, Integer> getWordMap() {
        return Map.copyOf(this.words);
    }

    @Override
    public void setWordMap(Map<String, Integer> wordMap) {
        this.words = wordMap;
    }

    @Override
    public int compareTo(Document o) {
        return Long.compare(timeInNano,o.getLastUseTime());
    }

}
