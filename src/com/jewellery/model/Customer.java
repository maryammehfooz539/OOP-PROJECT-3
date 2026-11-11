package com.jewellery.model;

// Customer class: stores the details of one customer
public class Customer {
    private int id;
    private String name;
    private String phone;
    private String address;

    public Customer() {
    }

    public Customer(int id, String name, String phone, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // Converts this customer into one comma-separated line for the text file
    public String toLine() {
        return id + "," + name + "," + phone + "," + address;
    }

    // Creates a Customer from one line of the text file (returns null if the line is bad)
    public static Customer fromLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) {
            return null;
        }
        try {
            int id = Integer.parseInt(parts[0].trim());
            return new Customer(id, parts[1].trim(), parts[2].trim(), parts[3].trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
