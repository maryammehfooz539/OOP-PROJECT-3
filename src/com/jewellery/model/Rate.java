package com.jewellery.model;

// Rate class: stores the gold, silver and diamond rates of one day
public class Rate {
    private String date;      // like 2026-10-21
    private double gold;      // 24K gold, price per tola (Rs.)
    private double silver;    // silver, price per tola (Rs.)
    private double diamond;   // diamond, price per carat (Rs.)

    public Rate() {
    }

    public Rate(String date, double gold, double silver, double diamond) {
        this.date = date;
        this.gold = gold;
        this.silver = silver;
        this.diamond = diamond;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getGold() { return gold; }
    public void setGold(double gold) { this.gold = gold; }

    public double getSilver() { return silver; }
    public void setSilver(double silver) { this.silver = silver; }

    public double getDiamond() { return diamond; }
    public void setDiamond(double diamond) { this.diamond = diamond; }

    // Converts this rate into one comma-separated line for the text file
    public String toLine() {
        return date + "," + gold + "," + silver + "," + diamond;
    }

    // Creates a Rate from one line of the text file (returns null if the line is bad)
    public static Rate fromLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) {
            return null;
        }
        try {
            double gold = Double.parseDouble(parts[1].trim());
            double silver = Double.parseDouble(parts[2].trim());
            double diamond = Double.parseDouble(parts[3].trim());
            return new Rate(parts[0].trim(), gold, silver, diamond);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
