package com.jewellery.storage;

import com.jewellery.model.CartItem;
import com.jewellery.model.Item;
import java.util.ArrayList;

// CartManager class: keeps the carts of ALL customers in one ArrayList and saves them to data/carts.txt.
// Each customer has an own cart (the lines that have his customerId). Putting a piece in a cart does NOT
// reduce the stock: the stock reduces only at checkout (CartServlet makes the sales).
public class CartManager {
    private static final String FILE_PATH = FileHelper.dataFile("carts.txt");
    private static ArrayList<CartItem> lines = new ArrayList<CartItem>();

    // Loads the carts from the file (no file yet = no carts yet)
    public static void loadFromFile() {
        lines.clear();
        if (!FileHelper.fileExists(FILE_PATH)) {
            return;
        }
        ArrayList<String> fileLines = FileHelper.readLines(FILE_PATH);
        for (int i = 0; i < fileLines.size(); i++) {
            CartItem line = CartItem.fromLine(fileLines.get(i));
            if (line != null) {
                lines.add(line);
            }
        }
    }

    // Saves all carts to the file
    public static void saveToFile() {
        ArrayList<String> fileLines = new ArrayList<String>();
        for (int i = 0; i < lines.size(); i++) {
            fileLines.add(lines.get(i).toLine());
        }
        FileHelper.writeLines(FILE_PATH, fileLines);
    }

    // Returns the cart (all lines) of one customer
    public static ArrayList<CartItem> getCart(int customerId) {
        ArrayList<CartItem> cart = new ArrayList<CartItem>();
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).getCustomerId() == customerId) {
                cart.add(lines.get(i));
            }
        }
        return cart;
    }

    // Number of pieces in the cart of one customer
    public static int countPieces(int customerId) {
        int pieces = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).getCustomerId() == customerId) {
                pieces += lines.get(i).getQuantity();
            }
        }
        return pieces;
    }

    // Number of pieces in all carts together (shown in the sidebar)
    public static int totalPieces() {
        int pieces = 0;
        for (int i = 0; i < lines.size(); i++) {
            pieces += lines.get(i).getQuantity();
        }
        return pieces;
    }

    // The ids of the customers who have something in their cart (every id once)
    public static ArrayList<Integer> getCustomerIdsWithCarts() {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (int i = 0; i < lines.size(); i++) {
            Integer id = lines.get(i).getCustomerId();
            if (!ids.contains(id)) {
                ids.add(id);
            }
        }
        return ids;
    }

    // Puts pieces of an item in a customer's cart. Returns a message ("Sorry..." means it failed).
    public static String addItem(int customerId, int itemId, int amount) {
        if (CustomerManager.getCustomerById(customerId) == null) {
            return "Sorry, please choose a customer first.";
        }
        Item item = ItemManager.getItemById(itemId);
        if (item == null) {
            return "Sorry, this piece was not found.";
        }
        if (item.getQuantity() <= 0) {
            return "Sorry, " + item.getName() + " is out of stock.";
        }
        for (int i = 0; i < lines.size(); i++) {
            CartItem line = lines.get(i);
            if (line.getCustomerId() == customerId && line.getItemId() == itemId) {
                int wanted = line.getQuantity() + amount;
                if (wanted > item.getQuantity()) {
                    return "Sorry, only " + item.getQuantity() + " piece(s) of " + item.getName() + " in stock.";
                }
                line.setQuantity(wanted);
                saveToFile();
                return item.getName() + " added to the cart.";
            }
        }
        lines.add(new CartItem(customerId, itemId, amount));
        saveToFile();
        return item.getName() + " added to the cart.";
    }

    // Sets the quantity of one line (must be between 1 and the stock). Returns a message.
    public static String updateQuantity(int customerId, int itemId, int quantity) {
        Item item = ItemManager.getItemById(itemId);
        if (item == null) {
            return "Sorry, this piece was not found.";
        }
        if (quantity < 1) {
            return "Sorry, quantity must be at least 1.";
        }
        if (quantity > item.getQuantity()) {
            return "Sorry, only " + item.getQuantity() + " piece(s) of " + item.getName() + " in stock.";
        }
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).getCustomerId() == customerId && lines.get(i).getItemId() == itemId) {
                lines.get(i).setQuantity(quantity);
            }
        }
        saveToFile();
        return "Cart updated.";
    }

    // Removes one piece (line) from a customer's cart
    public static void removeItem(int customerId, int itemId) {
        for (int i = lines.size() - 1; i >= 0; i--) {
            if (lines.get(i).getCustomerId() == customerId && lines.get(i).getItemId() == itemId) {
                lines.remove(i);
            }
        }
        saveToFile();
    }

    // Empties the cart of one customer (also used when the customer is deleted)
    public static void clearCart(int customerId) {
        for (int i = lines.size() - 1; i >= 0; i--) {
            if (lines.get(i).getCustomerId() == customerId) {
                lines.remove(i);
            }
        }
        saveToFile();
    }

    // Removes an item from every cart (used when the item is deleted)
    public static void removeItemEverywhere(int itemId) {
        for (int i = lines.size() - 1; i >= 0; i--) {
            if (lines.get(i).getItemId() == itemId) {
                lines.remove(i);
            }
        }
        saveToFile();
    }
}
