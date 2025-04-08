package edu.yu.mdm;

import java.util.Objects;

public class AuthorInfoImpl implements AuthorInfo {

    private int authorID;
    private String firstName;
    private String lastName;

    public AuthorInfoImpl(int authorID, String firstName, String lastName) {
        this.authorID = authorID;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public int getAuthorID() {
        return this.authorID;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AuthorInfoImpl that = (AuthorInfoImpl) o;
        return authorID == that.authorID;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(authorID);
    }
}
