package com.jewellery.storage;

import com.jewellery.model.Item;
import java.util.ArrayList;

// ItemManager class: keeps all items in an ArrayList and saves them to data/items.txt
public class ItemManager {
    private static final String FILE_PATH = FileHelper.dataFile("items.txt");
    private static ArrayList<Item> items = new ArrayList<Item>();

    // Loads items from the file. If the file is missing, creates it with sample data.
    public static void loadFromFile() {
        items.clear();
        if (!FileHelper.fileExists(FILE_PATH)) {
            addSampleData();
            saveToFile();
            return;
        }
        ArrayList<String> lines = FileHelper.readLines(FILE_PATH);
        for (int i = 0; i < lines.size(); i++) {
            Item item = Item.fromLine(lines.get(i));
            if (item != null) {
                items.add(item);
            }
        }
    }

    // Saves all items to the file
    public static void saveToFile() {
        ArrayList<String> lines = new ArrayList<String>();
        for (int i = 0; i < items.size(); i++) {
            lines.add(items.get(i).toLine());
        }
        FileHelper.writeLines(FILE_PATH, lines);
    }

    // Adds 23 sample jewellery pieces (used only the first time). Each one has a picture that is
    // stored INSIDE the project (images/collection/), so the pictures show on every computer.
    // These pieces also appear on the Collection page, grouped by category.
    private static void addSampleData() {
        items.add(new Item(1, "Gold Solitaire Ring", "Ring", "Gold 22K", 5.5, 85000, 6, "images/collection/item-1.svg"));
        items.add(new Item(2, "Rose Gold Band Ring", "Ring", "Rose Gold 18K", 4.2, 45000, 8, "images/collection/item-2.svg"));
        items.add(new Item(3, "Diamond Engagement Ring", "Ring", "Platinum", 4.8, 350000, 3, "images/collection/item-3.svg"));
        items.add(new Item(4, "Royal Gold Necklace", "Necklace", "Gold 22K", 25.5, 650000, 4, "images/collection/item-4.svg"));
        items.add(new Item(5, "Pearl Choker Necklace", "Necklace", "Gold 18K", 14.0, 120000, 5, "images/collection/item-5.svg"));
        items.add(new Item(6, "Layered Rose Gold Necklace", "Necklace", "Rose Gold 18K", 10.5, 95000, 7, "images/collection/item-6.svg"));
        items.add(new Item(7, "Pearl Drop Earrings", "Earring", "Gold 18K", 6.2, 120000, 10, "images/collection/item-7.svg"));
        items.add(new Item(8, "Gold Jhumka Earrings", "Earring", "Gold 22K", 9.8, 150000, 6, "images/collection/item-8.svg"));
        items.add(new Item(9, "Diamond Stud Earrings", "Earring", "Gold 18K", 3.1, 180000, 4, "images/collection/item-9.svg"));
        items.add(new Item(10, "Rose Gold Bracelet", "Bracelet", "Rose Gold 18K", 12.4, 240000, 6, "images/collection/item-10.svg"));
        items.add(new Item(11, "Charm Bracelet", "Bracelet", "Silver 925", 15.0, 65000, 9, "images/collection/item-11.svg"));
        items.add(new Item(12, "Silver Anklet Pair", "Anklet", "Silver 925", 18.0, 22000, 15, "images/collection/item-12.svg"));
        items.add(new Item(13, "Gold Beaded Anklet", "Anklet", "Gold 18K", 8.5, 38000, 7, "images/collection/item-13.svg"));
        items.add(new Item(14, "Diamond Nose Pin", "Nose Pin", "Gold 18K", 0.9, 28000, 12, "images/collection/item-14.svg"));
        items.add(new Item(15, "Gold Stud Nose Pin", "Nose Pin", "Gold 22K", 0.6, 15000, 20, "images/collection/item-15.svg"));
        items.add(new Item(16, "Gold Arm Cuff", "Arm Cuff", "Gold 22K", 22.0, 110000, 4, "images/collection/item-16.svg"));
        items.add(new Item(17, "Antique Arm Cuff", "Arm Cuff", "Silver 925", 30.0, 75000, 5, "images/collection/item-17.svg"));
        items.add(new Item(18, "Bridal Gold Bangles", "Bangle", "Gold 22K", 48.0, 1250000, 2, "images/collection/item-18.svg"));
        items.add(new Item(19, "Silver Bangles Set", "Bangle", "Silver 925", 40.0, 30000, 10, "images/collection/item-19.svg"));
        items.add(new Item(20, "Bridal Kundan Set", "Bridal Set", "Gold 22K", 85.0, 1850000, 2, "images/collection/item-20.svg"));
        items.add(new Item(21, "Bridal Polki Set", "Bridal Set", "Gold 22K", 70.0, 1450000, 3, "images/collection/item-21.svg"));
        items.add(new Item(22, "Gold Maang Tikka", "Maang Tikka", "Gold 22K", 12.5, 185000, 5, "images/collection/item-22.svg"));
        items.add(new Item(23, "Kundan Maang Tikka", "Maang Tikka", "Gold 22K", 9.0, 125000, 6, "images/collection/item-23.svg"));
    }

    // Returns all items
    public static ArrayList<Item> getAllItems() {
        return items;
    }

    // Finds one item by its id (returns null if not found)
    public static Item getItemById(int id) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == id) {
                return items.get(i);
            }
        }
        return null;
    }

    // Gives the next free id (highest id + 1)
    private static int getNextId() {
        int max = 0;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() > max) {
                max = items.get(i).getId();
            }
        }
        return max + 1;
    }

    // Adds a new item and saves the file
    public static void addItem(Item item) {
        item.setId(getNextId());
        items.add(item);
        saveToFile();
    }

    // Replaces an old item (same id) with the edited item and saves the file
    public static void updateItem(Item item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == item.getId()) {
                items.set(i, item);
                saveToFile();
                return;
            }
        }
    }

    // Deletes an item by id and saves the file
    public static void deleteItem(int id) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == id) {
                items.remove(i);
                saveToFile();
                return;
            }
        }
    }

    // Returns the items whose name contains the search text (ignores capital letters)
    public static ArrayList<Item> searchItems(String text) {
        ArrayList<Item> result = new ArrayList<Item>();
        String search = text.trim().toLowerCase();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().toLowerCase().contains(search)) {
                result.add(items.get(i));
            }
        }
        return result;
    }

    // Reduces the stock of an item after a sale and saves the file
    public static void reduceStock(int id, int amount) {
        Item item = getItemById(id);
        if (item != null) {
            item.setQuantity(item.getQuantity() - amount);
            saveToFile();
        }
    }

    // Returns only the items that are low in stock
    public static ArrayList<Item> getLowStockItems() {
        ArrayList<Item> result = new ArrayList<Item>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isLowStock()) {
                result.add(items.get(i));
            }
        }
        return result;
    }

    // Counts the items that are low in stock
    public static int countLowStock() {
        int count = 0;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isLowStock()) {
                count++;
            }
        }
        return count;
    }
}
