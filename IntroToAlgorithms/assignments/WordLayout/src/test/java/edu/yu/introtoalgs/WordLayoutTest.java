package edu.yu.introtoalgs;

import edu.yu.introtoalgs.WordLayout;
import edu.yu.introtoalgs.WordLayoutBase;
import edu.yu.introtoalgs.WordLayoutBase.Grid;
import edu.yu.introtoalgs.WordLayoutBase.LocationBase;

import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WordLayoutTest {
    private long startTime;
    private Logger logger = LogManager.getLogger(WordLayoutTest.class.getName());

    @BeforeEach
    public void beforeEach() {
        startTime = System.currentTimeMillis();
    }

    @AfterEach
    public void afterEach(TestInfo testInfo) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        String testName = testInfo.getTestMethod().get().getName();
        logger.info("Test {} took {} ms", testName, duration);
    }

    @BeforeAll
    public static void setup() {
        Configurator.initialize(null, "log4j2.xml");
    }

    @Test
    void TestFromDoc(TestInfo testInfo){
        final int nRows = 3;
        final int nColumns = 3;
        final List<String> words = List.of("CAT","DOG","BOB");
        logger.info("Using this list of words: {}", words);

        final WordLayoutBase layout = new WordLayout(nRows, nColumns, words);

        for(String word : words){
            final List<LocationBase> locations = layout.locations(word);
            logger.info("Locations for word {}: {}", word, locations);
        }
        final Grid grid = layout.getGrid();
        logger.info("The filled in grid: {}", grid);

    }

    @Test
    void largeGrid(TestInfo testInfo){
        final int nRows = 2;
        final int nColumns = 7000;
        final Set<String> wordSet = new HashSet<>(List.of(
                "apple", "orange", "banana", "grape", "kiwi", "pineapple", "peach", "pear", "plum", "lemon",
                "carrot", "potato", "broccoli", "cucumber", "tomato", "spinach", "lettuce", "onion", "garlic", "ginger",
                "dog", "cat", "bird", "fish", "rabbit", "hamster", "turtle", "snake", "elephant", "lion",
                "red", "blue", "green", "yellow", "orange", "purple", "pink", "brown", "black", "white",
                "happy", "sad", "angry", "excited", "calm", "nervous", "brave", "fearful", "confident", "shy",
                "sun", "moon", "star", "cloud", "rain", "snow", "wind", "thunder", "lightning", "storm",
                "ocean", "river", "lake", "stream", "waterfall", "mountain", "valley", "desert", "forest", "jungle",
                "book", "pen", "pencil", "paper", "notebook", "computer", "keyboard", "mouse", "monitor", "printer",
                "music", "movie", "game", "art", "dance", "poem", "story", "song", "photograph", "sculpture",
                "doctor", "nurse", "teacher", "engineer", "scientist", "artist", "actor", "musician", "writer", "chef",
                "train", "car", "bus", "bicycle", "motorcycle", "airplane", "ship", "rocket", "helicopter", "submarine",
                "love", "hate", "friendship", "family", "happiness", "sadness", "anger", "fear", "hope", "faith",
                "smile", "tear", "laughter", "scream", "whisper", "shout", "touch", "hug", "kiss", "dream",
                "tree", "flower", "grass", "leaf", "root", "branch", "petal", "bud", "thorn", "bark",
                "spider", "ant", "bee", "butterfly", "snail", "caterpillar", "dragonfly", "ladybug", "grasshopper", "mosquito",
                "football", "basketball", "soccer", "tennis", "golf", "baseball", "swimming", "cycling", "running", "hiking",
                "school", "college", "university", "classroom", "student", "teacher", "principal", "homework", "exam", "graduation",
                "phone", "tablet", "watch", "camera", "glasses", "hat", "shoes", "bag", "jacket", "dress",
                "door", "window", "wall", "floor", "ceiling", "roof", "stairs", "bed", "chair", "table",
                "sunflower", "rose", "lily", "tulip", "daisy", "daffodil", "carnation", "orchid", "hydrangea", "chrysanthemum",
                "dragon", "unicorn", "phoenix", "mermaid", "werewolf", "vampire", "witch", "wizard", "ghost", "zombie",
                "sword", "shield", "bow", "arrow", "spear", "axe", "hammer", "dagger", "wand", "staff",
                "fire", "water", "earth", "air", "light", "darkness", "magic", "time", "space", "dream",
                "laughter", "tears", "joy", "sorrow", "love", "hate", "fear", "hope", "despair", "courage",
                "freedom", "justice", "wisdom", "power", "truth", "beauty", "friendship", "loyalty", "betrayal", "forgiveness",
                "whale", "dolphin", "shark", "octopus", "seahorse", "coral", "seashell", "starfish", "jellyfish", "lobster",
                "robot", "alien", "spaceship", "astronaut", "planet", "galaxy", "comet", "asteroid", "blackhole", "nebula",
                "volcano", "tornado", "earthquake", "tsunami", "hurricane", "flood", "drought", "wildfire", "avalanche", "blizzard",
                "magic", "spell", "wand", "potion", "witch", "wizard", "sorcerer", "enchant", "curse", "alchemy",
                "knight", "dragon", "castle", "princess", "king", "queen", "knight", "wizard", "sword", "shield",
                "ghost", "haunt", "creepy", "cemetery", "pumpkin", "witch", "bat", "spider", "skeleton", "zombie",
                "jazz", "blues", "rock", "pop", "hiphop", "classical", "reggae", "country", "folk", "electronic",
                "painting", "sculpture", "pottery", "photography", "drawing", "collage", "printmaking", "origami", "calligraphy", "textile",
                "tea", "coffee", "wine", "beer", "juice", "soda", "water", "milk", "chocolate", "cake",
                "space", "time", "gravity", "energy", "atom", "molecule", "element", "chemical", "reaction", "experiment",
                "magnet", "electricity", "force", "motion", "friction", "velocity", "acceleration", "inertia", "gravity", "pressure",
                "history", "geography", "biology", "physics", "chemistry", "mathematics", "literature", "philosophy", "psychology", "sociology",
                "drama", "poetry", "comedy", "tragedy", "romance", "mystery", "fantasy", "sciencefiction", "horror", "adventure",
                "butterfly", "caterpillar", "chameleon", "lizard", "gecko", "iguana", "tortoise", "snail", "slug", "beetle",
                "plankton", "krill", "jellyfish", "clam", "urchin", "anemone", "gull", "pelican", "osprey", "hawk",
                "caterpillar", "chrysalis", "cocoon", "moth", "butterfly", "pupa", "larva", "pupa", "cicada", "ant",
                "antelope", "buffalo", "cheetah", "giraffe", "hippopotamus", "kangaroo", "koala", "leopard", "lion", "rhinoceros",
                "ambulance", "bicycle", "bus", "car", "firetruck", "helicopter", "motorcycle", "policecar", "sailboat", "submarine",
                "cello", "drum", "flute", "guitar", "harp", "piano", "saxophone", "trumpet", "violin", "xylophone",
                "beach", "cave", "desert", "forest", "island", "jungle", "mountain", "ocean", "river", "volcano",
                "alphabet", "dictionary", "library", "newspaper", "novel", "poem", "sentence", "story", "word", "writer",
                "astronaut", "cosmonaut", "pilot", "scientist", "engineer", "doctor", "nurse", "teacher", "artist", "musician",
                "my","name","is","mordechai","but","koop"
        ));
        List<String> words = new ArrayList<>(wordSet);


        logger.info("Using this list of words: (Too large to print)");

        final WordLayoutBase layout = new WordLayout(nRows, nColumns, words);
        int count = 0;
        words.sort(Comparator.comparingInt(String::length).reversed());
        //check that each word is in the right location. it should start from (0,1) and increase to length of the word for each word, until it reaches the end, so for example if the first word was "the" it would be (0,1),(0,2),(0,3)
        for(String word : words){
            final List<LocationBase> locations = layout.locations(word);
            //logger.info("Locations for word {}: {}", word, locations);
            WordCords cord = new WordCords(word);
            for(int i = 0; i < word.length();i++) {
                cord.addCord(0,count++);
            }
            assertEquals(locations.toString(),cord.getCords().toString());
        }


//        for(String word : words){
//            final List<LocationBase> locations = layout.locations(word);
//            logger.info("Locations for word {}: {}", word, locations);
//            count+=word.length();
//        }
        logger.info("Location for first word {}: {}",words.get(0),layout.locations(words.get(0)));
        logger.info("Location of last word {}: {}",words.get(words.size()-1),layout.locations(words.get(words.size()-1)));

        final Grid grid = layout.getGrid();

        logger.info("The filled in grid is very large");
    }

    @Test
    void throwCorrectly(){
        assertThrows(IllegalArgumentException.class, () -> new WordLayout(0, 2, List.of("the")));//row is too small
        assertThrows(IllegalArgumentException.class, () -> new WordLayout(2, 0, List.of("the")));//column is too small
        assertThrows(IllegalArgumentException.class, () -> new WordLayout(2, 2, List.of()));//words is empty
        assertThrows(IllegalArgumentException.class, () -> new WordLayout(2, 2, null));//words is null
        assertThrows(IllegalArgumentException.class, () -> new WordLayout(2, 2, List.of("the")));//A word in words is too long
        assertThrows(IllegalArgumentException.class, () -> new WordLayout(2, 2, List.of("the dog")));//A word in words contains a space
        assertThrows(IllegalArgumentException.class, () -> new WordLayout(3, 3, List.of("the", "the")));//A word in words is repeated
        assertThrows(IllegalArgumentException.class, () -> new WordLayout(3,3 , List.of("the", "cat", "ate","to")));//Too many characters in words
        assertThrows(IllegalArgumentException.class, () -> new WordLayout(3,3,List.of("the","cat")).locations("not"));//try to get a word that isn't in the list

    }

    @Test
    void testColums(){
        final int nRows = 5;
        final int nColumns = 3;
        final List<String> words = List.of("fives","fours","three");
        WordLayoutBase layout = new WordLayout(nRows, nColumns, words);

        logger.info("Using this list of words: {}", words);

        for(String word : words){
            final List<LocationBase> locations = layout.locations(word);
            logger.info("Locations for word {}: {}", word, locations);
        }
        final Grid grid = layout.getGrid();
        logger.info("The filled in grid: {}", grid);

    }

    @Test
    void normalWordSearch(){//sometimes throws an error, depends on the random lengths of the words
        final int nRows = 10;
        final int nColumns = 10;
        List<String> words = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < 10; i++){
            StringBuilder sb = new StringBuilder();
            int length = random.nextInt(10)+1;
            for(int j = 0; j < length; j++){
                sb.append((char)(random.nextInt(26)+97));
            }
            words.add(sb.toString());
        }
        logger.info("Using this list of words: {}", words);

        WordLayoutBase layout = new WordLayout(nRows, nColumns, words);

        for(String word : words){
            final List<LocationBase> locations = layout.locations(word);
            logger.info("Locations for word {}: {}", word, locations);
        }
        final Grid grid = layout.getGrid();
        logger.info("The filled in grid: {}", grid);

    }

    @Test
    void testWithEmptyWord(){
        final int row = 5;
        final int col = 5;
        List<String> words = List.of("");
        WordLayoutBase layout = new WordLayout(row, col, words);
        logger.info("Using this list of words: {}", words);
        for(String word : words){
            final List<LocationBase> locations = layout.locations(word);
            logger.info("Locations for word {}: {}", word, locations);
            assertEquals(locations.toString(),"[]");
        }
        final Grid grid = layout.getGrid();
        logger.info("The filled in grid: {}", grid);
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