package edu.yu.mdm;

import java.sql.*;
import java.util.*;

public class QueryEngine extends QueryEngineBase{

    private final String dbName;
    private final String usename;
    private final String password;
    private final String url;

    public QueryEngine(String dbName, String username, String password) throws SQLException {
        super(dbName, username, password);
        this.dbName = dbName;
        this.usename = username;
        this.password = password;
        this.url = "jdbc:postgresql://localhost:5432/" + dbName;

        try(Connection conn = getConnection()){
            if(conn == null) throw new SQLException("Connection failed");
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, usename, password);
    }



    @Override
    public Set<AuthorInfo> getAllAuthorInfos() {
        Set<AuthorInfo> res = new HashSet<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT authorID, firstName, lastName FROM authors")) {

            while (rs.next()) {
                int authorID = rs.getInt("authorID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");

                res.add(new AuthorInfoImpl(authorID, firstName, lastName));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all authors", e);
        }

        return res;
    }

    @Override
    public Author authorByName(String lastName, String firstName) {
        if(lastName == null || firstName == null || lastName.isEmpty() || firstName.isEmpty()){
            throw new IllegalArgumentException("Last name and first name cannot be null or empty");
        }

        try (Connection conn = getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT authorID FROM authors WHERE lastName = ? AND firstName = ?")) {
                stmt.setString(1, lastName);
                stmt.setString(2, firstName);
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    return null;
                }
                int authorID = rs.getInt("authorID");

                if (rs.next()) {
                    throw new IllegalArgumentException("Author name is not unique");
                }


                Set<TitleInfo> titleInfos = new HashSet<>();
                try (PreparedStatement stmt2 = conn.prepareStatement("SELECT t.isbn, t.title, t.editionNumber, t.copyright FROM titles t JOIN authorISBN a ON t.isbn = a.isbn WHERE a.authorID = ?")) {

                    stmt2.setInt(1, authorID);
                    ResultSet rs2 = stmt2.executeQuery();

                    while (rs2.next()) {
                        String isbn = rs2.getString("isbn");
                        String title = rs2.getString("title");
                        int editionNumber = rs2.getInt("editionNumber");
                        String copyright = rs2.getString("copyright");

                        titleInfos.add(new TitleInfoImpl(isbn, title, editionNumber, copyright));
                    }
                }
                return new AuthorImpl(authorID, firstName, lastName, titleInfos);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting author by name", e);
        }
    }

    @Override
    public List<Book> booksByTitle(String title) {
        if(title == null || title.isEmpty()){
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        List<Book> books = new ArrayList<>();

        try(Connection conn = getConnection()){
            Map<String,TitleInfo> isbnToTitleInfo = new HashMap<>();

            try (PreparedStatement stmt = conn.prepareStatement("SELECT isbn, title, editionNumber, copyright FROM titles WHERE title = ? ORDER BY isbn")) {
                stmt.setString(1, title);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String isbn = rs.getString("isbn");
                    int editionNumber = rs.getInt("editionNumber");
                    String copyright = rs.getString("copyright");

                    TitleInfo titleInfo = new TitleInfoImpl(isbn, title, editionNumber, copyright);
                    isbnToTitleInfo.put(isbn, titleInfo);
                }
            }

            for (String isbn : isbnToTitleInfo.keySet()) {
                TitleInfo titleInfo = isbnToTitleInfo.get(isbn);
                List<AuthorInfo> authors = new ArrayList<>();

                try (PreparedStatement stmt = conn.prepareStatement("SELECT a.authorID, a.firstName, a.lastName FROM authors a JOIN authorISBN ai ON a.authorID = ai.authorID WHERE ai.isbn = ? ORDER BY a.lastName, a.firstName")) {

                    stmt.setString(1, isbn);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        int authorID = rs.getInt("authorID");
                        String firstName = rs.getString("firstName");
                        String lastName = rs.getString("lastName");

                        authors.add(new AuthorInfoImpl(authorID, firstName, lastName));
                    }
                }

                if (!authors.isEmpty()) {
                    books.add(new BookImpl(titleInfo, authors));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Error getting books by title", e);
        }

        return books;
    }

    @Override
    public AuthorInfo createAuthorInfo(int authorID, String firstName, String lastName) {
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("First name and last name cannot be null or empty");
        }
        return new AuthorInfoImpl(authorID, firstName, lastName);
    }

    @Override
    public TitleInfo createTitleInfo(String isbn, String title, String copyright, int editionNumber) {
        if (isbn == null || isbn.isEmpty() || title == null || title.isEmpty() || copyright == null || copyright.isEmpty() || editionNumber <= 0) {
            throw new IllegalArgumentException("Invalid parameters for TitleInfo");
        }
        return new TitleInfoImpl(isbn, title, editionNumber, copyright);
    }

    @Override
    public void insertInfo(String firstName, String lastName, Set<TitleInfo> titleInfos) throws SQLException {
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() || titleInfos == null || titleInfos.isEmpty()) {
            throw new IllegalArgumentException("First name, last name, and title infos cannot be null or empty");
        }

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // check if author exists
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM authors WHERE firstName = ? AND lastName = ?")) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);

                ResultSet rs = stmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    throw new IllegalArgumentException("Author with name: " + firstName + " " + lastName + " already exists");
                }
            }

            // insert author
            int authorID;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT MAX(authorID) FROM authors")) {
                ResultSet rs = stmt.executeQuery();
                rs.next();
                authorID = rs.getInt(1);
                if (rs.wasNull()) {
                    authorID = 1; // if tables is empty
                } else {
                    authorID = authorID + 1;
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO authors (authorID, firstName, lastName) VALUES (?, ?, ?)")) {
                stmt.setInt(1, authorID);
                stmt.setString(2, firstName);
                stmt.setString(3, lastName);

                stmt.executeUpdate();
            }


            // inssert titles and isbn
            for (TitleInfo titleInfo : titleInfos) {
                boolean titleExists;
                try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM titles WHERE isbn = ?")) {
                    stmt.setString(1, titleInfo.getISBN());
                    ResultSet rs = stmt.executeQuery();
                    rs.next();
                    titleExists = rs.getInt(1) > 0;
                }

                if (!titleExists) {
                    try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO titles (isbn, title, editionNumber, copyright) VALUES (?, ?, ?, ?)")) {
                        stmt.setString(1, titleInfo.getISBN());
                        stmt.setString(2, titleInfo.getTitle());
                        stmt.setInt(3, titleInfo.getEditionNumber());
                        stmt.setString(4, titleInfo.getCopyright());

                        stmt.executeUpdate();
                    }
                }

                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO authorISBN (authorID, isbn) VALUES (?, ?)")) {
                    stmt.setInt(1, authorID);
                    stmt.setString(2, titleInfo.getISBN());

                    stmt.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Error rolling back transaction", ex);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}
