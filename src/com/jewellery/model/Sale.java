package com.jewellery.model;

// Sale class: stores one sale (one bill)
public class Sale {
    private int id;
    private int customerId;
    private String customerName;
    private int itemId;
    private String itemName;
    private int quantity;
    private double unitPrice;
    private double total;
    private String date;

    public Sale() {
    }

    public Sale(int id, int customerId, String customerName, int itemId, String itemName,
                int quantity, double unitPrice, double total, String date) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.total = total;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    // Converts this sale into one comma-separated line for the text file
    public String toLine() {
        return id + "," + customerId + "," + customerName + "," + itemId + ","
                + itemName + "," + quantity + "," + unitPrice + "," + total + "," + date;
    }

    // Creates a Sale from one line of the text file (returns null if the line is bad)
    public static Sale fromLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 9) {
            return null;
        }
        try {
            int id = Integer.parseInt(parts[0].trim());
            int customerId = Integer.parseInt(parts[1].trim());
            int itemId = Integer.parseInt(parts[3].trim());
            int quantity = Integer.parseInt(parts[5].trim());
            double unitPrice = Double.parseDouble(parts[6].trim());
            double total = Double.parseDouble(parts[7].trim());
            return new Sale(id, customerId, parts[2].trim(), itemId, parts[4].trim(),
                    quantity, unitPrice, total, parts[8].trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
