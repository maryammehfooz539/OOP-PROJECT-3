package com.jewellery.storage;

import com.jewellery.model.Customer;
import java.util.ArrayList;

// CustomerManager class: keeps all customers in an ArrayList and saves them to data/customers.txt
public class CustomerManager {
    private static final String FILE_PATH = FileHelper.dataFile("customers.txt");
    private static ArrayList<Customer> customers = new ArrayList<Customer>();

    // Loads customers from the file. If the file is missing, creates it with sample data.
    public static void loadFromFile() {
        customers.clear();
        if (!FileHelper.fileExists(FILE_PATH)) {
            addSampleData();
            saveToFile();
            return;
        }
        ArrayList<String> lines = FileHelper.readLines(FILE_PATH);
        for (int i = 0; i < lines.size(); i++) {
            Customer customer = Customer.fromLine(lines.get(i));
            if (customer != null) {
                customers.add(customer);
            }
        }
    }

    // Saves all customers to the file
    public static void saveToFile() {
        ArrayList<String> lines = new ArrayList<String>();
        for (int i = 0; i < customers.size(); i++) {
            lines.add(customers.get(i).toLine());
        }
        FileHelper.writeLines(FILE_PATH, lines);
    }

    // Adds 4 sample customers (used only the first time)
    private static void addSampleData() {
        customers.add(new Customer(1, "Ayesha Khan", "03001234567", "House 12 Gulshan-e-Iqbal Karachi"));
        customers.add(new Customer(2, "Sara Ahmed", "03217654321", "Flat 4 Block B Clifton Karachi"));
        customers.add(new Customer(3, "Hina Malik", "03331112233", "Street 7 Model Town Lahore"));
        customers.add(new Customer(4, "Fatima Noor", "03459988776", "House 21 F-8 Islamabad"));
    }

    // Returns all customers
    public static ArrayList<Customer> getAllCustomers() {
        return customers;
    }

    // Finds one customer by id (returns null if not found)
    public static Customer getCustomerById(int id) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() == id) {
                return customers.get(i);
            }
        }
        return null;
    }

    // Gives the next free id (highest id + 1)
    private static int getNextId() {
        int max = 0;
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() > max) {
                max = customers.get(i).getId();
            }
        }
        return max + 1;
    }

    // Adds a new customer and saves the file
    public static void addCustomer(Customer customer) {
        customer.setId(getNextId());
        customers.add(customer);
        saveToFile();
    }

    // Replaces an old customer (same id) with the edited customer and saves the file
    public static void updateCustomer(Customer customer) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() == customer.getId()) {
                customers.set(i, customer);
                saveToFile();
                return;
            }
        }
    }

    // Deletes a customer by id and saves the file
    public static void deleteCustomer(int id) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() == id) {
                customers.remove(i);
                saveToFile();
                return;
            }
        }
    }
}
