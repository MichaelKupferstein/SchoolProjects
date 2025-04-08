package edu.yu.mdm;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        DataGenerator dataGenerator = new DataGenerator();
        List<SaleData> data = dataGenerator.generateData(500);

        SalesProducer producer = new SalesProducer();
        for(SaleData saleData : data) {
            producer.send(saleData);
            Thread.sleep(10);
        }
        producer.close();

        SalesConsumer consumer = new SalesConsumer();
        consumer.consumeSaleData();
        consumer.printStatistics();
    }
}
