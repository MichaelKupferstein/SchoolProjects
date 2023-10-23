package edu.yu.introtoalgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordLayout extends WordLayoutBase{

    private Grid grid;
    private List<String> words;
    private Map<String,List<LocationBase>> wordLocations;
    private int[][] template;

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
        if(nRows < 0 || nColumns < 0 || words == null || words.isEmpty() || words.contains(null)){
            throw new IllegalArgumentException("Invalid parameters");
        }

        this.grid = new Grid(nRows, nColumns);
        this.words = words;
        this.wordLocations = new HashMap<>();
        this.template = new int[nRows][nColumns];



        for(String word : words){
            if(word.length() > nColumns || word.length() > nRows){
                throw new IllegalArgumentException("Contains word that is too long");
            }
            wordLocations.put(word, addWord(word));
        }

//        if(basicRows){
//            createBasicRows();
//        } else if (basiColumns){
//            createBasicColumns();
//        } else {
//            //harder stuff
//        }

    }



    private List<LocationBase> addWord(String word){
        WordCords wordCords = new WordCords(word);
        int row = 0, column = 0;

        return wordCords.getCords();
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
        if(!words.contains(word)){
            throw new IllegalArgumentException("Word not in list");
        }
        return null;
    }

    /**
     * Returns the Grid after it has been filled in with all words
     *
     * @returns Grid instance.
     */
    @Override
    public Grid getGrid() {
        return this.grid;
    }

    private class WordCords{
        private String word;
        private int[] wordRows, wordColumns;
        private int count = 0;

        public WordCords(String word){
            this.word = word;
            this.wordRows = new int[word.length()];
            this.wordColumns = new int[word.length()];
        }
        private void addCord(int row, int column){
            wordRows[count] = row;
            wordColumns[count] = column;
            count++;
        }
        private List<LocationBase> getCords(){
            List<LocationBase> cords = new ArrayList<>();
            for(int i = 0; i < word.length(); i++){
                cords.add(new LocationBase(wordRows[i], wordColumns[i]));
            }
            return cords;
        }
    }
}
