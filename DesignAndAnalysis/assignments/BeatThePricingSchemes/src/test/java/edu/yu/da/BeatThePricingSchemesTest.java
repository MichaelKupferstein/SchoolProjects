package edu.yu.da;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BeatThePricingSchemesTest {

    @Test
    void testThrows(){
        assertThrows(IllegalArgumentException.class, () -> new BeatThePricingSchemes(0));
        assertThrows(IllegalArgumentException.class, () -> new BeatThePricingSchemes(-1));

        assertThrows(IllegalArgumentException.class, () -> new BeatThePricingSchemes(1).addPricingScheme(-1, 1));
        assertThrows(IllegalArgumentException.class, () -> new BeatThePricingSchemes(1).addPricingScheme(1, 0));
        assertThrows(IllegalArgumentException.class, () -> new BeatThePricingSchemes(1).addPricingScheme(1, 101));

        assertThrows(IllegalArgumentException.class, () -> new BeatThePricingSchemes(1).cheapestPrice(0));
        assertThrows(IllegalArgumentException.class, () -> new BeatThePricingSchemes(1).cheapestPrice(101));

        BeatThePricingSchemesBase btps = new BeatThePricingSchemes(1);
        for(int i = 0; i < 20; i++){
            btps.addPricingScheme(1, 1);
        }
        assertThrows(IllegalArgumentException.class, () -> btps.addPricingScheme(1, 1));



    }

    @Test
    void testFromDoc(){
        BeatThePricingSchemesBase btps = new BeatThePricingSchemes(2.0);
        btps.addPricingScheme(8.75, 5);
        assertEquals(8.0, btps.cheapestPrice(4));
        assertEquals(List.of(0,0,0,0), btps.optimalDecisions());
        assertEquals(10.75, btps.cheapestPrice(6));
        assertEquals(List.of(0,1), btps.optimalDecisions());
    }

    @Test
    void test1(){
        BeatThePricingSchemesBase btps = new BeatThePricingSchemes(1.0);
        btps.addPricingScheme(2.0, 2);
        assertEquals(3.0, btps.cheapestPrice(3));
        assertEquals(List.of(0,1), btps.optimalDecisions());
    }

    @Test
    void test2(){
        BeatThePricingSchemesBase btps = new BeatThePricingSchemes(1.0);
        btps.addPricingScheme(8.0, 10);
        assertEquals(8.0, btps.cheapestPrice(10));
        assertEquals(List.of(1), btps.optimalDecisions());
        assertEquals(8.0, btps.cheapestPrice(9));
        assertEquals(List.of(1), btps.optimalDecisions());
        assertEquals(8.0, btps.cheapestPrice(8));
        assertEquals(List.of(1), btps.optimalDecisions());
        assertEquals(7.0, btps.cheapestPrice(7));
        assertEquals(List.of(0,0,0,0,0,0,0), btps.optimalDecisions());
    }

    @Test
    void test3(){
        BeatThePricingSchemesBase btps = new BeatThePricingSchemes(1.5);//0

        btps.addPricingScheme(3.25, 3);//1
        btps.addPricingScheme(4.76, 5);//2
        btps.addPricingScheme(7.0, 8);//3
        btps.addPricingScheme(9.5, 12);//4

        assertEquals(1.5, btps.cheapestPrice(1));
        assertEquals(List.of(0), btps.optimalDecisions());
        assertEquals(3.0, btps.cheapestPrice(2));
        assertEquals(List.of(0,0), btps.optimalDecisions());
        assertEquals(3.25, btps.cheapestPrice(3));
        assertEquals(List.of(1), btps.optimalDecisions());
        assertEquals(4.75, btps.cheapestPrice(4));
        assertEquals(List.of(0,1), btps.optimalDecisions());
        assertEquals(4.76, btps.cheapestPrice(5));
        assertEquals(List.of(2), btps.optimalDecisions());
        assertEquals(6.26, btps.cheapestPrice(6));
        assertEquals(List.of(0,2), btps.optimalDecisions());
        assertEquals(7.0, btps.cheapestPrice(7));
        assertEquals(List.of(3), btps.optimalDecisions());
        assertEquals(7.0, btps.cheapestPrice(8));
        assertEquals(List.of(3), btps.optimalDecisions());
        assertEquals(8.5, btps.cheapestPrice(9));
        assertEquals(List.of(0,3), btps.optimalDecisions());
        assertEquals(9.5, btps.cheapestPrice(10));
        assertEquals(List.of(4), btps.optimalDecisions());
        assertEquals(9.5, btps.cheapestPrice(11));
        assertEquals(List.of(4), btps.optimalDecisions());
        assertEquals(9.5, btps.cheapestPrice(12));
        assertEquals(List.of(4), btps.optimalDecisions());



    }

}