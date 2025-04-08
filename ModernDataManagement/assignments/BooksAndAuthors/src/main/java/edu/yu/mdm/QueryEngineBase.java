package edu.yu.mdm;

import java.util.List;
import java.util.Set;
import java.sql.SQLException;

/** All interactions between database clients are mediated through a
 * QueryEngine instance.  A QueryEngine defines a higher-level interface to the
 * database than the "raw" tables and SQL.
 *
 * Design REQUIREMENT: because clients can instantiate multiple instances of a
 * QueryEngine, implementations should NOT assume that state from a previous
 * method invocations is available for use by another methd invocation.
 *
 * Design REQUIREMENT: only the pre-defined tables (see the requirements doc)
 * may be used to persist information for this assignment.
 *
 * Design requirement: implementation MAY throw RuntimeException if parameters
 * are syntactically invalid (e.g., empty string), but MUST throws SQLException
 * if the database rejects an implementation-initiated command.
 *
 * Design REQUIREMENT:  all API engines execute on a per-transaction granularity.
 *
 * @author Avraham Leff
 */
public abstract class QueryEngineBase {

    /** Constructor.
     *
     * The client configures the QueryEngine instance to use the specified
     * parameters when connecting to the backing database.  In other words:
     * clients (and ONLY the clients) provide sufficient state for the
     * implementation to construct a java.sql.Connection instance on a locally
     * installed Postgres server.  The constructor MUST attempt to connect to the
     * database specified by the client, and throws an SQLException if the
     * attempt fails.
     *
     * All interactions with the database that are subsequently performed by this
     * instance must use this configuration information.
     *
     * It's the client's responsibility to ensure that the parameters are
     * "valid": i.e., that a local Postgres database exists for which the
     * supplied credentials are valid.
     *
     * @param dbname Name of the database
     * @param username The database user on whose behalf the connection is being
     * made.
     * @param password The database user's password.
     * @see https://jdbc.postgresql.org/documentation/use
     * @throws SQLException if the client's parameters "don't work": i.e., a
     * connection cannot be established to the database.
     */
    public QueryEngineBase(String dbname, String username, String password)
            throws SQLException
    {
        // no-op implementation
    }

    /** Return all AuthorsInfos currently in the database.
     */
    public abstract Set<AuthorInfo> getAllAuthorInfos();

    /** Retrieve the author uniquely identified with the specified last name and
     * first name.
     *
     * @param lastName Last name of the author.
     * @param firstName First name of the author.
     * @return Author if she exists in the database, null otherwise
     * @throws IllegalArgumentException if the combination of the last name and
     * first name are not unique or if something is wrong with the parameters
     */
    public abstract Author authorByName(String lastName, String firstName);

    /** Retrieve all Books with the specified title.  If no books match the
     * title, return the empty set.  Books are ordered by ISBN, and authors of a
     * given book are ordered alphabetically by last name, then first name.
     *
     * @param title Book's title.
     */
    public abstract  List<Book> booksByTitle(final String title);

    /** Create an in-memory instance of AuthorInfo.
     *
     * @throws IllegalArgumentException if something is wrong with the parameters
     */
    public abstract  AuthorInfo createAuthorInfo(int authorID, String firstName, String lastName);

    /** Create an in-memory instance of TitleInfo instance
     */
    public abstract TitleInfo
    createTitleInfo(final String isbn, final String title,
                    final String copyright, final int editionNumber);

    /** Persists information about a new author and her titles, but ONLY if
     * author doesn't already exist.
     *
     * @param firstName author's first name
     * @param lastName author's last name
     * @param titleInfos information about titles written by an author, set size
     * must be greater or equal to one.
     * @see createAuthorInfo
     * @see createdTitleInfo
     * @throws IllegalArgumentException if the combination of the last name and
     * first name already exist in the database or if something is wrong with the parameters
     * @fixme method for updating existing author information, probably should have upsert semantics
     */
    public abstract void
    insertInfo(String firstName, String lastName, Set<TitleInfo> titleInfos) throws SQLException;

} // abstract class