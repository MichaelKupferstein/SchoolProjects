package edu.yu.mdm;

/* Defines an interface for a value class that encapsulates information about a
 * single book.  Instance identity for implementations of this class is defined
 * by getISBN().
 *
 * @author Avraham Leff
 */

public interface TitleInfo {
    String getTitle();
    String getISBN();
    int getEditionNumber();
    String getCopyright();
}