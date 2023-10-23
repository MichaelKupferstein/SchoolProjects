package edu.yu.introtoalgs;

import java.util.List;

public class WordLayout extends WordLayoutBase{

    /**
     * Creates a grid with the specified number of rows and columns such that
     * every one of the supplied words are successfully layed out on the grid.
     * Conceptually, a Grid instance is created (with random letters assigned to
     * all Grid locations), and then overlayed with the list of words to create a
     * valid layout.  The rules for a valid layout are specified in the
     * requirements document.
     *
     * @param nRows    number of rows in 0..n-1 representation, must be a
     *                 non-negative integer.
     * @param nColumns number of columns in 0..n-1 representation, must be a
     *                 non-negative integer.
     * @param words    a non-null, non-empty list of words.  Client maintains ownership.
     * @throws IllegalArgumentException if it's impossible to layout the words in
     *                                  the specified grid or if the supplied parameters violate the specified
     *                                  requirements.
     */
    public WordLayout(int nRows, int nColumns, List<String> words) {
        super(nRows, nColumns, words);


    }

    /**
     * Returns the grid locations that specify how a word is layed out on the
     * grid.  The locations must be sorted in ascending row coordinate, breaking
     * ties if necessary, by sorting in ascending column coordinate.
     *
     * @param word
     * @return List of locations that specify how the word is layed out on the
     * grid.
     * @throws IllegalArgumentException if the word is not an element of the List
     *                                  supplied in the constructor.
     */
    @Override
    public List<LocationBase> locations(String word) {
        return null;
    }

    /**
     * Returns the Grid after it has been filled in with all words
     *
     * @returns Grid instance.
     */
    @Override
    public Grid getGrid() {
        return null;
    }
}
