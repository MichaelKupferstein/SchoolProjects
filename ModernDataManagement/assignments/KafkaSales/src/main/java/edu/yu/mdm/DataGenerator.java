package edu.yu.mdm;

import java.util.*;

public class DataGenerator {

    private static final String[] PRODUCTS = {"YU Hat", "Iphone 21", "Limited Edition YU CS Sticker", "A free HW pass for MDM", "Automatic faliure on assigment",
            "Apple", "Tesla", "A NoSQL database", "Rubber Duck", "Automatic A in MDM"};
    private static final String[] CUSTOMERS = {"KVETCHER","Koop", "Mordechai", "Avrham", "Judah", "Akiva", "Sam", "Reuben"};

    private Random random = new Random();

    public List<SaleData> generateData(int count) {
        List<SaleData> data = new ArrayList<>();
        Set<String > names = new HashSet<>();
        for (int i = 0; i < count; i++) {
            String name = getName();
            names.add(name);
            data.add(new SaleData(PRODUCTS[random.nextInt(PRODUCTS.length)], random.nextInt(10) + 1,
                    random.nextDouble() * 1000, System.currentTimeMillis(), name));
        }
        while (names.size() < 4) {
            String name = getName();
            names.add(name);
            data.add(new SaleData(PRODUCTS[random.nextInt(PRODUCTS.length)], random.nextInt(10) + 1,
                    random.nextDouble() * 1000, System.currentTimeMillis(), name));
        }
        return data;
    }

    private String getName() {
        //return no more than 8 and atleast 4 unique names, between 20 aand 40 percent of the generated names be be named "KVETCHER"
        if (random.nextInt(100) < 20) {
            return "KVETCHER";
        }
        return CUSTOMERS[random.nextInt(CUSTOMERS.length)];

    }
}
