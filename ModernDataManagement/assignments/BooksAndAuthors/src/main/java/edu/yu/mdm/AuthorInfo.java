package edu.yu.mdm;

/* Defines an interface for a value class that encapsulates author information.
 * In contrast to the Author interface, this interface does not include the
 * books written by the author.  Instance identify for implementations of this
 * interface is defined by getAuthorID().
 *
 * @author Avraham Leff
 */

public interface AuthorInfo {
    int getAuthorID();
    String getFirstName();
    String getLastName();
}