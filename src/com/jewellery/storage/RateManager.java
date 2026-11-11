package com.jewellery.storage;

import com.jewellery.model.Rate;
import java.util.ArrayList;

// RateManager class: keeps the daily rates in an ArrayList and saves them to data/rates.txt
public class RateManager {
    private static final String FILE_PATH = FileHelper.dataFile("rates.txt");
    private static final int MAX_DAYS = 30;   // we keep only the latest 30 days
    private static ArrayList<Rate> rates = new ArrayList<Rate>();

    // Loads rates from the file. If the file is missing, creates it with sample data.
    public static void loadFromFile() {
        rates.clear();
        if (!FileHelper.fileExists(FILE_PATH)) {
            addSampleData();
            saveToFile();
            return;
        }
        ArrayList<String> lines = FileHelper.readLines(FILE_PATH);
        for (int i = 0; i < lines.size(); i++) {
            Rate rate = Rate.fromLine(lines.get(i));
            if (rate != null) {
                rates.add(rate);
            }
        }
        sortByDate();
    }

    // Saves all rates to the file
    public static void saveToFile() {
        ArrayList<String> lines = new ArrayList<String>();
        for (int i = 0; i < rates.size(); i++) {
            lines.add(rates.get(i).toLine());
        }
        FileHelper.writeLines(FILE_PATH, lines);
    }

    // Adds 7 days of SAMPLE rates (the admin should enter the real market rates)
    private static void addSampleData() {
        rates.add(new Rate("2026-10-15", 476000, 7800, 450000));
        rates.add(new Rate("2026-10-16", 478500, 7850, 450000));
        rates.add(new Rate("2026-10-17", 477200, 7790, 452000));
        rates.add(new Rate("2026-10-18", 481000, 7900, 455000));
        rates.add(new Rate("2026-10-19", 483500, 7960, 455000));
        rates.add(new Rate("2026-10-20", 482000, 7920, 458000));
        rates.add(new Rate("2026-10-21", 484200, 8010, 460000));
    }

    // Returns all rates, oldest first (the chart needs this order)
    public static ArrayList<Rate> getAllRates() {
        return rates;
    }

    // Returns the newest rate (null if there are no rates)
    public static Rate getLatest() {
        if (rates.size() == 0) {
            return null;
        }
        return rates.get(rates.size() - 1);
    }

    // Returns the rate of the day before the newest one (null if there is none)
    public static Rate getPrevious() {
        if (rates.size() < 2) {
            return null;
        }
        return rates.get(rates.size() - 2);
    }

    // Saves the rates of a day. If that date already exists, it is updated.
    public static void addOrUpdateRate(Rate newRate) {
        boolean found = false;
        for (int i = 0; i < rates.size(); i++) {
            if (rates.get(i).getDate().equals(newRate.getDate())) {
                rates.set(i, newRate);
                found = true;
            }
        }
        if (!found) {
            rates.add(newRate);
        }
        sortByDate();

        while (rates.size() > MAX_DAYS) {
            rates.remove(0);   // remove the oldest day
        }
        saveToFile();
    }

    // Sorts the rates by date (oldest first) using a simple bubble sort
    private static void sortByDate() {
        for (int i = 0; i < rates.size() - 1; i++) {
            for (int j = 0; j < rates.size() - 1 - i; j++) {
                if (rates.get(j).getDate().compareTo(rates.get(j + 1).getDate()) > 0) {
                    Rate temp = rates.get(j);
                    rates.set(j, rates.get(j + 1));
                    rates.set(j + 1, temp);
                }
            }
        }
    }
}
