package edu.yu.mdm;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.*;

public class SalesConsumer {

    private Consumer<String, byte[]> consumer;
    private Map<Integer, Set<String>> customersPartitons = new HashMap<>();
    private Map<Integer, Double> salesPartions = new HashMap<>();

    public SalesConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "sales-group");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList("sales"));

        for(int i = 0; i < 4; i++) {
            customersPartitons.put(i, new HashSet<>());
            salesPartions.put(i, 0.0);
        }
    }

    public void consumeSaleData(){
        try{
            int emptyPolls = 0;
            while(emptyPolls < 5){
                ConsumerRecords<String, byte[]> records = consumer.poll(100);
                if(records.isEmpty()){
                    emptyPolls++;
                    continue;
                }
                emptyPolls = 0;
                for(ConsumerRecord<String, byte[]> record : records){
                    SaleData saleData = deserializeData(record.value());
                    customersPartitons.get(record.partition()).add(saleData.getCustomerName());
                    salesPartions.put(record.partition(), salesPartions.get(record.partition()) + (saleData.getPrice() * saleData.getQuantity()));

                    System.out.printf("Sale for %s with product %s for a total sale of %.2f on partition %d%n", saleData.getCustomerName(),
                            saleData.getProductName(), saleData.getPrice() * saleData.getQuantity(), record.partition());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            consumer.close();
        }
    }


    private SaleData deserializeData(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream in = new ObjectInputStream(bais);
        return (SaleData) in.readObject();
    }

    public void printStatistics() {
        System.out.println("\nCustomers by Partition:");
        customersPartitons.forEach((partition, customers) ->
                System.out.printf("Partition %d: %s%n", partition, String.join(", ", customers)));

        System.out.println("\nTotal Sales by Partition:");
        salesPartions.forEach((partition, total) -> System.out.printf("Partition %d: $%.2f%n", partition, total));
    }
}
