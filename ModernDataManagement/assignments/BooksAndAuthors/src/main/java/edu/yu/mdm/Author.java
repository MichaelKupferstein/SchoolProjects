package edu.yu.mdm;

import java.util.Set;

/* Extends the AuthorInfo interface to define a value class that also
 * encapsulates information about books written by this author.  Instance
 * identity for implementations of this interface is defined by author id.
 *
 * @author Avraham Leff
 */

public interface Author extends AuthorInfo {
    /** Get the title information (must be at least one) for books written by
     * this author
     */
    Set<TitleInfo> getTitleInfos();
}