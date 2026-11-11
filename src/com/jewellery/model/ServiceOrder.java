package com.jewellery.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// ServiceOrder class: one custom order or one repair job of a customer, so the admin can track it.
// Example: "Resize ring from size 7 to 9" (Repair) or "Gold bridal set made to order" (Custom Order).
// The money part: totalPrice (agreed price), advance (already paid) and balance (still to pay).
// The status moves through: Received -> In Progress -> Ready -> Delivered (or Cancelled).
public class ServiceOrder {

    // The allowed values (also used by the form drop-downs and by the validation)
    public static final String[] STATUSES = {"Received", "In Progress", "Ready", "Delivered", "Cancelled"};
    public static final String[] TYPES = {"Custom Order", "Repair"};

    private int id;
    private int customerId;        // which customer (id from customers.txt)
    private String customerName;   // name is saved too, so old orders still show it if the customer is deleted
    private String type;           // "Custom Order" or "Repair"
    private String description;    // what has to be done
    private String receivedDate;   // like 2026-10-25
    private String dueDate;        // promised delivery date
    private double totalPrice;     // agreed price (Rs.)
    private double advance;        // money already paid (Rs.)
    private String status;         // one of STATUSES

    public ServiceOrder() {
    }

    public ServiceOrder(int id, int customerId, String customerName, String type, String description,
                        String receivedDate, String dueDate, double totalPrice, double advance, String status) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.type = type;
        this.description = description;
        this.receivedDate = receivedDate;
        this.dueDate = dueDate;
        this.totalPrice = totalPrice;
        this.advance = advance;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReceivedDate() { return receivedDate; }
    public void setReceivedDate(String receivedDate) { this.receivedDate = receivedDate; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public double getAdvance() { return advance; }
    public void setAdvance(double advance) { this.advance = advance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Money the customer still has to pay
    public double getBalance() {
        return totalPrice - advance;
    }

    // How far the job is (0 to 100), used for the progress bar
    public int getProgress() {
        if ("Received".equals(status)) {
            return 25;
        } else if ("In Progress".equals(status)) {
            return 50;
        } else if ("Ready".equals(status)) {
            return 75;
        } else if ("Delivered".equals(status)) {
            return 100;
        }
        return 0;   // Cancelled
    }

    // Bootstrap colour name for the status badge and the progress bar
    public String getStatusColor() {
        if ("Received".equals(status)) {
            return "info";
        } else if ("In Progress".equals(status)) {
            return "warning";
        } else if ("Ready".equals(status)) {
            return "success";
        } else if ("Delivered".equals(status)) {
            return "primary";
        }
        return "secondary";   // Cancelled
    }

    // True when the job is finished (Delivered or Cancelled)
    public boolean isFinished() {
        return "Delivered".equals(status) || "Cancelled".equals(status);
    }

    // True when the job is still open (not Ready, not finished) and the promised date has passed
    public boolean isOverdue() {
        if (isFinished() || "Ready".equals(status)) {
            return false;
        }
        try {
            return LocalDate.parse(dueDate).isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // Converts this order into one comma-separated line for the text file
    public String toLine() {
        return id + "," + customerId + "," + customerName + "," + type + "," + description + ","
                + receivedDate + "," + dueDate + "," + totalPrice + "," + advance + "," + status;
    }

    // Creates a ServiceOrder from one line of the text file (returns null if the line is bad)
    public static ServiceOrder fromLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 10) {
            return null;
        }
        try {
            int id = Integer.parseInt(parts[0].trim());
            int customerId = Integer.parseInt(parts[1].trim());
            double total = Double.parseDouble(parts[7].trim());
            double advance = Double.parseDouble(parts[8].trim());
            return new ServiceOrder(id, customerId, parts[2].trim(), parts[3].trim(), parts[4].trim(),
                    parts[5].trim(), parts[6].trim(), total, advance, parts[9].trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
