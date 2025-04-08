package edu.yu.introtoalgs;



import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BigOIt2Test {

    private BigOIt2 bigOIt2;

    @Test
    public void doublingRatioTestOnThreeSum() {
        this.bigOIt2 = new BigOIt2();
        double mode = this.bigOIt2.doublingRatio("edu.yu.introtoalgs.testAlgs.ThreeSum", 30000);
        assertEquals(8.0, mode, 0.1);
    }
    @Test
    public void doublingRatioTestReturnNaN() {//should return NaN bc there isnt enough time to get accurate data
        this.bigOIt2 = new BigOIt2();
        double mode = this.bigOIt2.doublingRatio("edu.yu.introtoalgs.testAlgs.ThreeSum", 500);
        assertEquals(Double.NaN, mode);
    }


    @Test
    public void doublingRatioTestOnFactorial(){
        this.bigOIt2 = new BigOIt2();
        double mode = this.bigOIt2.doublingRatio("edu.yu.introtoalgs.testAlgs.Factorial", 1000);
        assertEquals(2.0, mode, 0.2);

    }
    @Test
    public void doublingRatioTestOnFactorialReturnNaN(){
        this.bigOIt2 = new BigOIt2();
        double mode = this.bigOIt2.doublingRatio("edu.yu.introtoalgs.testAlgs.Factorial",10);
        assertEquals(Double.NaN, mode);

    }
    @Test
    public void doublingRatioTestOnConstantTime(){
        this.bigOIt2 = new BigOIt2();
        double mode = this.bigOIt2.doublingRatio("edu.yu.introtoalgs.testAlgs.ConstantTime", 5000);
        assertEquals(1.0, mode, 0.1);

    }





}