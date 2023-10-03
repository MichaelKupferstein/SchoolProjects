package edu.yu.introtoalgs;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class BigOIt2Test {

    private BigOIt2 bigOIt2;

    @Test
    public void doublingRatioTest() {
        this.bigOIt2 = new BigOIt2();
        this.bigOIt2.doublingRatio("edu.yu.introtoalgs.ThreeSum", 10000);

    }



}