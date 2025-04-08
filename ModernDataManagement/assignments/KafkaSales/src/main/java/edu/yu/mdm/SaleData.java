package edu.yu.mdm;

import java.io.Serializable;

public class SaleData implements Serializable {
    private String productName;
    private int quantity;
    private double price;
    private long timestamp;
    private String cusomerName;

    public SaleData(String productName, int quantity, double price, long timestamp, String customerName) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
        this.cusomerName = customerName;
    }

    public String getProductName() {
        return productName;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getPrice() {
        return price;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public String getCustomerName() {
        return cusomerName;
    }

}
