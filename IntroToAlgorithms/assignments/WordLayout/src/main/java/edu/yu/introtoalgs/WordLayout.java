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
        this.words.sort(Comparator.comparingInt(String::length).reversed());
        this.wordLocations = new HashMap<>();

        //plus one bc first row and col will be counter of hw many zeros there are
        this.template = createTemplate(nRows+1, nColumns+1);

        int letterCount = 0;

        for(String word : this.words){
            if(word.length() > nColumns && word.length() > nRows) throw new IllegalArgumentException("Contains word that is too long");
            if(word.contains(" ")) throw new IllegalArgumentException("Words cannot contain spaces");

            letterCount += word.length();

            if(letterCount > nRows*nColumns) throw new IllegalArgumentException("Too many letters, total letter count must be less than or equal to: " + nRows*nColumns);

            if(this.wordLocations.put(word, addWord(word))!=null) throw new IllegalArgumentException("Duplicate word: " + word);
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
            if(template[i][0] >= word.length() && checkFullRow(i,word.length())){
                int startingColum = addToRow(i,word);
                if(startingColum == -1) break;
                for(int j = 0; j < word.length(); j++){
                    wordCords.addCord(i-1,startingColum-1);
                    template[0][startingColum]--;
                    startingColum++;
                }
                template[i][0] -= word.length();
                return wordCords.getCords();
            }
        }

        //check the columns
        for(int i = 1; i < column+1;i++){
            //if the counter is greater than or equal to the word then it can be added
            if(template[0][i] >= word.length() && checkFullCol(i,word.length())){
                int startingRow = addToColumn(i,word);
                if(startingRow ==-1) break;
                for (int j = 0; j < word.length(); j++) {
                    wordCords.addCord(startingRow - 1, i - 1);
                    template[startingRow][0]--;
                    startingRow++;
                }
                template[0][i] -= word.length();
                return wordCords.getCords();
            }
        }
        //not really gonna ever happen bc we count letters and make sure words arent too long, just needed as a precuation
        throw new IllegalArgumentException("Word " + word + " cannot be added to the grid");
    }


    private int addToRow(int row, String word){
        //check the row and find the columns that it can be added to in this row, 0 means its open and 1 means its taken
        for(int i = 1; i < column; i++){
            if(checkColumns(row,i,word)){
                //add the word to the row
                for(int j = 0; j < word.length(); j++){
                    template[row][i++] = 1;
                    //add the word to the grid at the same indices -1 bc the grid starts at 0
                    grid.grid[row-1][i-2] = word.charAt(j);
                }

                return i-word.length();
            }
        }
        //usually not gonna get here bc of the way we check the rows
        return -1; //for now
    }

    private int addToColumn(int column, String word){
        //check the column and find the rows that it can be added to in this column, 0 means its open and 1 means its taken
        for(int i = 1; i < row; i++){
            if(checkRows(i,column,word)){
                //add the word to the column
                for(int j = 0; j < word.length(); j++){
                    template[i++][column] = 1;
                    //add the word to the grid at the same indices -1 bc the grid starts at 0
                    grid.grid[i-2][column-1] = word.charAt(j);
                }
                return i-word.length();
            }
        }
        //usually not gonna get here bc of the way we check the columns
        return -1; //for now
    }

    private boolean checkColumns(int row, int column, String word){
        for(int i = 0; i < word.length(); i++){
            if(template[row][column++] != 0){
                return false;
            }
        }
        return true;
    }

    private boolean checkRows(int row, int column, String word){
        for(int i = 0; i < word.length(); i++){
            if(template[row++][column] != 0){
                return false;
            }
        }
        return true;
    }

    private boolean checkFullCol(int column, int wordSize){
        int count = 0;
        for(int i = 1; i < row+1; i++){
            if(template[i][column] == 0){
                count++;
            }else{
                count = 0;
            }
            if(count == wordSize){
                return true;
            }
        }
        return false;
    }

    private boolean checkFullRow(int row, int wordSize){
        int count = 0;
        for(int i = 1; i < column+1; i++){
            if(template[row][i] == 0){
                count++;
            }else{
                count = 0;
            }
            if(count == wordSize){
                return true;
            }
        }
        return false;
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
//        for (int i = 0; i < row+1; i++) {
//            for (int j = 0; j < column+1; j++) {
//                System.out.print(template[i][j] + " ");
//            }
//            System.out.println(); // Move to the next line after each row
//        }

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
