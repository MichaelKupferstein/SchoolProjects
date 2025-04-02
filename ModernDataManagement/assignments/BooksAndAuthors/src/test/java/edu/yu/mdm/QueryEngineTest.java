package edu.yu.mdm;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class QueryEngineTest {
    private static final String DBNAME = "booksandauthors";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "";

    private static QueryEngine queryEngine;

    @BeforeAll
    public static void setUp() throws SQLException {
        queryEngine = new QueryEngine(DBNAME, USERNAME, PASSWORD);
    }

    @Test
    public void testGetAllAuthorInfos() {
        Set<AuthorInfo> authors = queryEngine.getAllAuthorInfos();
        assertNotNull(authors);
        assertFalse(authors.isEmpty());
        assertEquals(21, authors.size());

        boolean foundJohnSmith = false;
        boolean foundJaneDoe = false;

        for (AuthorInfo author : authors) {
            if (author.getFirstName().equals("John") && author.getLastName().equals("Smith")) {
                foundJohnSmith = true;
            }
            if (author.getFirstName().equals("Jane") && author.getLastName().equals("Doe")) {
                foundJaneDoe = true;
            }
        }

        assertTrue(foundJohnSmith, "John Smith should be in the authors list");
        assertTrue(foundJaneDoe, "Jane Doe should be in the authors list");
    }

    @Test
    public void testAuthorByName() {
        // test with and existing author
        Author johnSmith = queryEngine.authorByName("Smith", "John");
        assertNotNull(johnSmith);
        assertEquals("John", johnSmith.getFirstName());
        assertEquals("Smith", johnSmith.getLastName());

        // check existing author's titles
        Set<TitleInfo> titles = johnSmith.getTitleInfos();
        assertNotNull(titles);
        assertFalse(titles.isEmpty());
        assertEquals(2, titles.size());

        //test with non existing author
        Author nonExistent = queryEngine.authorByName("Nonexistent", "Author");
        assertNull(nonExistent);

        // make sure that it throws
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.authorByName(null, "John");});
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.authorByName("Smith", "");});
    }

    @Test
    public void testBooksByTitle() {
        //test existing title with multiple books
        List<Book> javaBooks = queryEngine.booksByTitle("Java Programming");
        assertNotNull(javaBooks);
        assertFalse(javaBooks.isEmpty());
        assertEquals(2, javaBooks.size());

        // Check that books are ordered by ISBN
        assertTrue(javaBooks.get(0).getTitleInfo().getISBN().compareTo(javaBooks.get(1).getTitleInfo().getISBN()) < 0);

        //should have two editions
        boolean foundEdition1 = false;
        boolean foundEdition2 = false;

        for (Book book : javaBooks) {
            if (book.getTitleInfo().getEditionNumber() == 1) {
                foundEdition1 = true;
            }
            if (book.getTitleInfo().getEditionNumber() == 2) {
                foundEdition2 = true;
            }
        }

        assertTrue(foundEdition1, "Should find Java Programming 1st Edition");
        assertTrue(foundEdition2, "Should find Java Programming 2nd Edition");

        //non-existent title
        List<Book> nonExistentBooks = queryEngine.booksByTitle("Nonexistent Book");
        assertNotNull(nonExistentBooks);
        assertTrue(nonExistentBooks.isEmpty());

        // Test invalid parameters thorws
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.booksByTitle(null);});
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.booksByTitle("");});
    }

    @Test
    public void testCreateAuthorInfo() {
        AuthorInfo author = queryEngine.createAuthorInfo(100, "Test", "Author");
        assertNotNull(author);
        assertEquals(100, author.getAuthorID());
        assertEquals("Test", author.getFirstName());
        assertEquals("Author", author.getLastName());

        // Test invalid parameters throws
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.createAuthorInfo(101, null, "Author");});
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.createAuthorInfo(102, "Test", "");});
    }

    @Test
    public void testCreateTitleInfo() {
        TitleInfo title = queryEngine.createTitleInfo("978-1234567890", "Test Book", "2023", 1);
        assertNotNull(title);
        assertEquals("978-1234567890", title.getISBN());
        assertEquals("Test Book", title.getTitle());
        assertEquals("2023", title.getCopyright());
        assertEquals(1, title.getEditionNumber());

        // Test invalid parameters throws
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.createTitleInfo(null, "Test Book", "2023", 1);});
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.createTitleInfo("978-1234567890", "", "2023", 1);});
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.createTitleInfo("978-1234567890", "Test Book", null, 1);});
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.createTitleInfo("978-1234567890", "Test Book", "2023", 0);});
    }

    @Test
    public void testInsertInfo() throws SQLException {
        // Create a new author with books
        String firstName = "Michael";
        String lastName = "Koop";

        Set<TitleInfo> titles = new HashSet<>();
        titles.add(queryEngine.createTitleInfo("111-1111111111", "New Test Book 1", "2023", 1));
        titles.add(queryEngine.createTitleInfo("222-2222222222", "New Test Book 2", "2023", 1));

        // Insert
        queryEngine.insertInfo(firstName, lastName, titles);

        // Verify insertion
        Author newAuthor = queryEngine.authorByName(lastName, firstName);
        assertNotNull(newAuthor);
        assertEquals(firstName, newAuthor.getFirstName());
        assertEquals(lastName, newAuthor.getLastName());

        Set<TitleInfo> retrievedTitles = newAuthor.getTitleInfos();
        assertNotNull(retrievedTitles);
        assertEquals(2, retrievedTitles.size());

        // Test inserting duplicate author
        final Set<TitleInfo> moreTitles = new HashSet<>();
        moreTitles.add(queryEngine.createTitleInfo("978-" + (System.currentTimeMillis() + 2), "Another Book", "2023", 1));
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.insertInfo(firstName, lastName, moreTitles);});

        // Test invalid parameters
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.insertInfo(null, lastName, titles);});
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.insertInfo(firstName, "", titles);});
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.insertInfo(firstName, lastName, null);});
        assertThrows(IllegalArgumentException.class, () -> {queryEngine.insertInfo(firstName, lastName, new HashSet<>());});
    }
}