package edu.yu.mdm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookImpl implements Book {

    private TitleInfo titleInfo;
    private List<AuthorInfo> authorInfos;

    public BookImpl(TitleInfo titleInfo, List<AuthorInfo> authorInfos) {
        this.titleInfo = titleInfo;
        this.authorInfos = new ArrayList<>(authorInfos);
    }

    @Override
    public TitleInfo getTitleInfo() {
        return titleInfo;
    }

    @Override
    public List<AuthorInfo> getAuthorInfos() {
        return new ArrayList<>(authorInfos);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookImpl book = (BookImpl) o;
        return titleInfo.equals(titleInfo);
    }

    @Override
    public int hashCode() {
        return titleInfo.hashCode();
    }
}
