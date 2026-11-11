package com.jewellery.storage;

import com.jewellery.model.ServiceOrder;
import java.time.LocalDate;
import java.util.ArrayList;

// OrderManager class: keeps all custom orders and repairs in an ArrayList and saves them to data/orders.txt
public class OrderManager {
    private static final String FILE_PATH = FileHelper.dataFile("orders.txt");
    private static ArrayList<ServiceOrder> orders = new ArrayList<ServiceOrder>();

    // Loads the orders from the file. If the file is missing, creates it with sample data.
    public static void loadFromFile() {
        orders.clear();
        if (!FileHelper.fileExists(FILE_PATH)) {
            addSampleData();
            saveToFile();
            return;
        }
        ArrayList<String> lines = FileHelper.readLines(FILE_PATH);
        for (int i = 0; i < lines.size(); i++) {
            ServiceOrder order = ServiceOrder.fromLine(lines.get(i));
            if (order != null) {
                orders.add(order);
            }
        }
    }

    // Saves all orders to the file
    public static void saveToFile() {
        ArrayList<String> lines = new ArrayList<String>();
        for (int i = 0; i < orders.size(); i++) {
            lines.add(orders.get(i).toLine());
        }
        FileHelper.writeLines(FILE_PATH, lines);
    }

    // Adds 3 sample orders (used only the first time). Dates are counted from today.
    private static void addSampleData() {
        LocalDate today = LocalDate.now();
        orders.add(new ServiceOrder(1, 1, "Ayesha Khan", "Repair", "Resize ring from size 7 to size 9",
                today.minusDays(2).toString(), today.plusDays(3).toString(), 3500, 1000, "In Progress"));
        orders.add(new ServiceOrder(2, 2, "Sara Ahmed", "Custom Order", "Gold bridal set made to order with kundan work",
                today.minusDays(10).toString(), today.plusDays(20).toString(), 450000, 150000, "Received"));
        orders.add(new ServiceOrder(3, 4, "Fatima Noor", "Repair", "Fix broken clasp of gold necklace",
                today.minusDays(5).toString(), today.minusDays(1).toString(), 2000, 2000, "Ready"));
    }

    // Returns all orders
    public static ArrayList<ServiceOrder> getAllOrders() {
        return orders;
    }

    // Returns only the orders with the given status (an empty or "All" status returns everything)
    public static ArrayList<ServiceOrder> getByStatus(String status) {
        if (status == null || status.isEmpty() || status.equals("All")) {
            return orders;
        }
        ArrayList<ServiceOrder> result = new ArrayList<ServiceOrder>();
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getStatus().equals(status)) {
                result.add(orders.get(i));
            }
        }
        return result;
    }

    // Counts the orders that have the given status
    public static int countByStatus(String status) {
        return getByStatus(status).size();
    }

    // Counts the jobs that are still open (Received, In Progress or Ready): shown on the dashboard
    public static int countOpen() {
        int count = 0;
        for (int i = 0; i < orders.size(); i++) {
            if (!orders.get(i).isFinished()) {
                count++;
            }
        }
        return count;
    }

    // Counts the open jobs whose promised date has passed
    public static int countOverdue() {
        int count = 0;
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).isOverdue()) {
                count++;
            }
        }
        return count;
    }

    // Finds one order by id (returns null if not found)
    public static ServiceOrder getOrderById(int id) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId() == id) {
                return orders.get(i);
            }
        }
        return null;
    }

    // Gives the next free id (highest id + 1)
    private static int getNextId() {
        int max = 0;
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId() > max) {
                max = orders.get(i).getId();
            }
        }
        return max + 1;
    }

    // Adds a new order and saves the file
    public static void addOrder(ServiceOrder order) {
        order.setId(getNextId());
        orders.add(order);
        saveToFile();
    }

    // Replaces an old order (same id) with the edited order and saves the file
    public static void updateOrder(ServiceOrder order) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId() == order.getId()) {
                orders.set(i, order);
                saveToFile();
                return;
            }
        }
    }

    // Changes only the status of an order (used by the quick drop-down in the list). Returns the order or null.
    public static ServiceOrder updateStatus(int id, String status) {
        ServiceOrder order = getOrderById(id);
        if (order != null) {
            order.setStatus(status);
            saveToFile();
        }
        return order;
    }

    // Deletes an order by id and saves the file
    public static void deleteOrder(int id) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId() == id) {
                orders.remove(i);
                saveToFile();
                return;
            }
        }
    }
}
