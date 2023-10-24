package edu.yu.introtoalgs;

import java.util.*;

public class WordLayout extends WordLayoutBase{

    private Grid grid;
    private List<String> words;
    private Map<String,List<LocationBase>> wordLocations;
    private int[][] template;
    private int row, column;

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
        if(nRows < 0 || nColumns < 0 || words == null || words.isEmpty()){
            throw new IllegalArgumentException("Invalid parameters");
        }
        //row and col stuff
        this.row = nRows;
        this.column = nColumns;
        //grid stuff
        this.grid = new Grid(nRows, nColumns);
        //word stuff
        this.words = new ArrayList<>(words);
        Collections.sort(this.words, Comparator.comparingInt(String::length).reversed());
        this.wordLocations = new HashMap<>();

        //plus one bc first row and col will be counter of hw many zeros there are
        this.template = createTemplate(nRows+1, nColumns+1);

        int letterCount = 0;

        for(String word : this.words){
            if(word.length() > nColumns || word.length() > nRows){
                throw new IllegalArgumentException("Contains word that is too long");
            }
            letterCount += word.length();
            if(letterCount > nRows*nColumns){
                throw new IllegalArgumentException("Too many letters");
            }
            this.wordLocations.put(word, addWord(word));
        }



    }

    private int[][] createTemplate(int nRows, int nColumns){
        int[][] template = new int[nRows][nColumns];
        for(int i = 1; i < nColumns; i++){
            template[0][i] = nRows-1;
        }
        for(int i = 1; i < nRows; i++){
            template[i][0] = nColumns-1;
        }
        return template;
    }




    private List<LocationBase> addWord(String word){
        WordCords wordCords = new WordCords(word);

        //check the rows first
        for(int i = 1; i < row+1; i++){
            //if the counter is greater than or equal to the word then it can be added
            if(template[i][0] >= word.length()){
                int startingColum = addToRow(i,word);
                for(int j = 0; j < word.length(); j++){
                    wordCords.addCord(i-1,startingColum-1);
                    template[0][startingColum]--;
                    startingColum++;
                }
                template[i][0] -= word.length();
                break;
            }
        }


        //check the columns

        return wordCords.getCords();
    }
    private int addToRow(int row, String word){
        //check the row and find the columns that it can be added to in this row, 0 means its open and 1 means its taken
        for(int i = 1; i < column; i++){
            if(checkColumn(row,i,word)){
                //add the word to the row
                for(int j = 0; j < word.length(); j++){
                    template[row][i++] = 1;
                    //add the word to the grid at the same indieces -1 bc the grid starts at 0
                    grid.grid[row-1][i-2] = word.charAt(j);
                }

                return i-word.length();
            }
        }
        //return the starting column
        return 0; //for now
    }
    private boolean checkColumn(int row, int column, String word){
        for(int i = 0; i < word.length(); i++){
            if(template[row][column++] != 0){
                return false;
            }
        }
        return true;
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
        return this.wordLocations.get(word);
    }

    /**
     * Returns the Grid after it has been filled in with all words
     *
     * @returns Grid instance.
     */
    @Override
    public Grid getGrid() {
        for (int i = 0; i < row+1; i++) {
            for (int j = 0; j < column+1; j++) {
                System.out.print(template[i][j] + " ");
            }
            System.out.println(); // Move to the next line after each row
        }

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

    public static void main(String[] args){
        //hypothetical grid
        //Grid is 5x5
        //in the code it would add one to each
//        int rows = 6;
//        int columns = 6;
//        int[][] twoDArray = createTemplate(rows,columns);
//
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                System.out.print(twoDArray[i][j] + " ");
//            }
//            System.out.println(); // Move to the next line after each row
//        }
        WordLayout wordLayout = new WordLayout(3,8, List.of("cat","hat","fat"));
        System.out.println(wordLayout.getGrid().toString());
        System.out.println(wordLayout.locations("cat"));
        System.out.println(wordLayout.locations("hat"));
        System.out.println(wordLayout.locations("fat"));


    }

}
