package edu.yu.da;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BeatThePricingSchemesTest {

    @Test
    void testFromDoc(){
        BeatThePricingSchemesBase btps = new BeatThePricingSchemes(2.0);
        btps.addPricingScheme(8.75, 5);
        assertEquals(8.0, btps.cheapestPrice(4));
        assertEquals(List.of(0,0,0,0), btps.optimalDecisions());
        assertEquals(10.75, btps.cheapestPrice(6));
        assertEquals(List.of(0,1), btps.optimalDecisions());
    }

}