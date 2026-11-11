package com.jewellery.model;

// CartItem class: one line of a customer's cart. It remembers WHICH customer the cart belongs to,
// WHICH item (its id) and HOW MANY pieces. Every customer has an own cart: all the lines with the same
// customerId together are that customer's cart (saved in data/carts.txt, see CartManager).
public class CartItem {

    private int customerId;  // owner of this cart
    private int itemId;      // id of the jewellery item
    private int quantity;    // how many pieces

    public CartItem(int customerId, int itemId, int quantity) {
        this.customerId = customerId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Converts this line into one comma-separated line for the text file
    public String toLine() {
        return customerId + "," + itemId + "," + quantity;
    }

    // Creates a CartItem from one line of the text file (returns null if the line is bad)
    public static CartItem fromLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 3) {
            return null;
        }
        try {
            return new CartItem(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()),
                    Integer.parseInt(parts[2].trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
