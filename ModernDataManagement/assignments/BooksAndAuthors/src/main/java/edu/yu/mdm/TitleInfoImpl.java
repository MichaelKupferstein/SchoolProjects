package edu.yu.mdm;

import java.util.Objects;

public class TitleInfoImpl implements TitleInfo{

    private String isbn;
    private String title;
    private int editionNumber;
    private String copyright;

    public TitleInfoImpl(String isbn, String title, int editionNumber, String copyright) {
        this.isbn = isbn;
        this.title = title;
        this.editionNumber = editionNumber;
        this.copyright = copyright;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getISBN() {
        return this.isbn;
    }

    @Override
    public int getEditionNumber() {
        return this.editionNumber;
    }

    @Override
    public String getCopyright() {
        return this.copyright;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TitleInfoImpl titleInfo = (TitleInfoImpl) o;
        return isbn.equals(titleInfo.isbn);
    }

    @Override
    public int hashCode() {
        return isbn.hashCode();
    }
}
