package com.jewellery.model;

// CartLine class: one row of the cart page = the full Item object + the quantity + the line total.
// It is built fresh every time the cart page opens, so the price and stock are always up to date.
public class CartLine {

    private Item item;       // the jewellery item (has name, price, picture, stock)
    private int quantity;    // pieces in the cart

    public CartLine(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() { return item; }

    public int getQuantity() { return quantity; }

    // price of this row = item price x quantity
    public double getTotal() {
        return item.getPrice() * quantity;
    }
}
