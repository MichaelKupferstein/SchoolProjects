package edu.yu.mdm;

import java.util.List;

/* Defines an interface for a value class that encapsulates information about a
 * single Book.  Instance identity for implementatins of this interface is
 * defined by the identity of getTitleInfo().
 *
 * @author Avraham Leff
 */
public interface Book {

    /** Get the title information of this Book.
     */
    TitleInfo getTitleInfo();

    /** Get information about all authors who wrote the book.  Must be least one
     * author.  If there are multiple authors, they must be ordered per query
     * semantics.
     */
    List<AuthorInfo> getAuthorInfos();
}