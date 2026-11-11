package com.jewellery.storage;

import com.jewellery.model.Customer;
import com.jewellery.model.Item;
import com.jewellery.model.Sale;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// SaleManager class: keeps all sales in an ArrayList and saves them to data/sales.txt
public class SaleManager {
    private static final String FILE_PATH = FileHelper.dataFile("sales.txt");
    private static ArrayList<Sale> sales = new ArrayList<Sale>();

    // Loads sales from the file. If the file is missing, creates an empty one.
    public static void loadFromFile() {
        sales.clear();
        if (!FileHelper.fileExists(FILE_PATH)) {
            saveToFile();
            return;
        }
        ArrayList<String> lines = FileHelper.readLines(FILE_PATH);
        for (int i = 0; i < lines.size(); i++) {
            Sale sale = Sale.fromLine(lines.get(i));
            if (sale != null) {
                sales.add(sale);
            }
        }
    }

    // Saves all sales to the file
    public static void saveToFile() {
        ArrayList<String> lines = new ArrayList<String>();
        for (int i = 0; i < sales.size(); i++) {
            lines.add(sales.get(i).toLine());
        }
        FileHelper.writeLines(FILE_PATH, lines);
    }

    // Returns all sales
    public static ArrayList<Sale> getAllSales() {
        return sales;
    }

    // Finds one sale by id (returns null if not found)
    public static Sale getSaleById(int id) {
        for (int i = 0; i < sales.size(); i++) {
            if (sales.get(i).getId() == id) {
                return sales.get(i);
            }
        }
        return null;
    }

    // Gives the next free sale (bill) number
    private static int getNextId() {
        int max = 0;
        for (int i = 0; i < sales.size(); i++) {
            if (sales.get(i).getId() > max) {
                max = sales.get(i).getId();
            }
        }
        return max + 1;
    }

    // Returns today's date and time as text, for example 2026-10-20 14:30
    private static String getNow() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(new Date());
    }

    // Makes a sale. Returns an empty text if everything is fine, otherwise an error message.
    public static String makeSale(int customerId, int itemId, int quantity) {
        Customer customer = CustomerManager.getCustomerById(customerId);
        if (customer == null) {
            return "Please select a valid customer.";
        }
        Item item = ItemManager.getItemById(itemId);
        if (item == null) {
            return "Please select a valid item.";
        }
        if (quantity <= 0) {
            return "Quantity must be at least 1.";
        }
        if (quantity > item.getQuantity()) {
            return "Not enough stock. Only " + item.getQuantity() + " piece(s) left.";
        }

        double total = item.getPrice() * quantity;
        Sale sale = new Sale(getNextId(), customer.getId(), customer.getName(),
                item.getId(), item.getName(), quantity, item.getPrice(), total, getNow());
        sales.add(sale);

        ItemManager.reduceStock(itemId, quantity);   // reduce the stock automatically
        saveToFile();
        return "";
    }
}
