package edu.yu.mdm;

import java.util.HashSet;
import java.util.Set;

public class AuthorImpl extends AuthorInfoImpl implements Author{

    private Set<TitleInfo> titleInfos;

    public AuthorImpl(int authorID, String firstName, String lastName, Set<TitleInfo> titleInfos) {
        super(authorID, firstName, lastName);
        this.titleInfos = titleInfos;
    }

    @Override
    public Set<TitleInfo> getTitleInfos() {
        return new HashSet<>(titleInfos);
    }
}
