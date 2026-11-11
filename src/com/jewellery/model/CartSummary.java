package com.jewellery.model;

// CartSummary class: one short line for the list of carts on the cart page
// (customer name + how many pieces are in the cart of that customer).
public class CartSummary {

    private int customerId;
    private String customerName;
    private int pieces;

    public CartSummary(int customerId, String customerName, int pieces) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.pieces = pieces;
    }

    public int getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public int getPieces() { return pieces; }
}
