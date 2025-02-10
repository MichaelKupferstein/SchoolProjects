package edu.yu.mdm;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Properties;

public class SalesProducer {

    private Producer<String, byte[]> producer;
    private String topic = "sales";
    private int partition = 0;

    public SalesProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        producer = new KafkaProducer<>(properties);
    }

    public void send(SaleData saleData) {
        try{
            byte[] data = serializeData(saleData);
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, getPartition(saleData.getCustomerName()), saleData.getCustomerName(), data);
            producer.send(record, (metaData, exception) -> {
                if(exception == null){
                    System.out.printf("Produced record at partition_%d:offset_%d at %td %tB %tY %tH:%tM:%tL%n",
                            metaData.partition(), metaData.offset(), saleData.getTimestamp(), saleData.getTimestamp(), saleData.getTimestamp(),
                            saleData.getTimestamp(), saleData.getTimestamp(), saleData.getTimestamp()
                    );
                } else {
                    System.err.println("Error producing record: " + exception);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private int getPartition(String customerName) {
        if("KVETCHER".equals(customerName)) {
            return 0;
        }
        partition = (partition %3) +1;
        return partition;
    }

    private byte[] serializeData(SaleData saleData) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(saleData);
        return baos.toByteArray();
    }

    public void close() {
        producer.close();
    }
}
