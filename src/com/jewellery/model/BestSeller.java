package com.jewellery.model;

// BestSeller class: how many pieces of one item were sold, and how much money it made
public class BestSeller {
    private int itemId;
    private String itemName;
    private String category;
    private int unitsSold;
    private double revenue;
    private int stockLeft;   // -1 means the item was deleted from the items list

    public BestSeller(int itemId, String itemName, String category, int stockLeft) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.category = category;
        this.stockLeft = stockLeft;
        this.unitsSold = 0;
        this.revenue = 0;
    }

    // Adds one sale of this item
    public void addSale(int quantity, double amount) {
        unitsSold = unitsSold + quantity;
        revenue = revenue + amount;
    }

    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getCategory() { return category; }
    public int getUnitsSold() { return unitsSold; }
    public double getRevenue() { return revenue; }
    public int getStockLeft() { return stockLeft; }

    // true if the item is still in the items list
    public boolean isInStore() {
        return stockLeft >= 0;
    }

    // true if 5 or fewer pieces are left
    public boolean isLowStock() {
        return stockLeft >= 0 && stockLeft <= 5;
    }
}
