package com.jewellery.storage;

import com.jewellery.model.Rate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

// REFERENCES (outside sources used by this class - not our own code):
//  - Gold and silver prices (USD per ounce): free public web API https://api.gold-api.com
//  - Dollar to Pakistani Rupee rate: free public web API https://open.er-api.com (ExchangeRate-API)
//  - Unit conversions: 1 troy ounce = 31.1034768 g, 1 tola = 11.6638038 g (standard values)
//  The code that calls these services (HttpURLConnection) was written for this project.
//
// LiveRateFetcher class: gets live gold and silver prices from the internet,
// changes them into Pakistani Rupees per tola, and saves them as today's rate.
public class LiveRateFetcher {
    private static final String GOLD_URL = "https://api.gold-api.com/price/XAU";
    private static final String SILVER_URL = "https://api.gold-api.com/price/XAG";
    private static final String DOLLAR_URL = "https://open.er-api.com/v6/latest/USD";

    private static final double GRAMS_PER_OUNCE = 31.1034768;
    private static final double GRAMS_PER_TOLA = 11.6638038;
    private static final long WAIT_TIME = 60 * 1000;   // ask the internet at most once a minute

    private static long lastTryTime = 0;
    private static String lastMessage = "";

    // Gets the live rates and saves them. Returns "" if all is fine, otherwise a message.
    public static synchronized String updateRates() {
        long now = System.currentTimeMillis();
        if (lastTryTime != 0 && now - lastTryTime < WAIT_TIME) {
            return lastMessage;   // we asked very recently, so use the last answer
        }
        lastTryTime = now;

        try {
            double goldDollars = findNumber(downloadText(GOLD_URL), "price");     // dollars per ounce
            double silverDollars = findNumber(downloadText(SILVER_URL), "price");
            double dollarToRupee = findNumber(downloadText(DOLLAR_URL), "PKR");

            if (goldDollars <= 0 || silverDollars <= 0 || dollarToRupee <= 0) {
                lastMessage = "Live rates could not be read. Showing saved rates.";
                return lastMessage;
            }

            double goldPerTola = dollarsPerOunceToRupeesPerTola(goldDollars, dollarToRupee);
            double silverPerTola = dollarsPerOunceToRupeesPerTola(silverDollars, dollarToRupee);

            // there is no free live price for diamonds, so we keep the last saved diamond rate
            double diamond = 0;
            Rate latest = RateManager.getLatest();
            if (latest != null) {
                diamond = latest.getDiamond();
            }
            if (diamond <= 0) {
                diamond = 460000;
            }

            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            RateManager.addOrUpdateRate(new Rate(today, Math.round(goldPerTola),
                    Math.round(silverPerTola), diamond));
            lastMessage = "";
        } catch (IOException e) {
            lastMessage = "Could not connect to the internet. Showing saved rates.";
        }
        return lastMessage;
    }

    // Changes "dollars per ounce" into "rupees per tola"
    static double dollarsPerOunceToRupeesPerTola(double dollarsPerOunce, double dollarToRupee) {
        double rupeesPerOunce = dollarsPerOunce * dollarToRupee;
        double rupeesPerGram = rupeesPerOunce / GRAMS_PER_OUNCE;
        return rupeesPerGram * GRAMS_PER_TOLA;
    }

    // Downloads the text of a web address
    private static String downloadText(String address) throws IOException {
        URL url = URI.create(address).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("User-Agent", "JewelleryStudentProject");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder text = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            text.append(line);
            line = reader.readLine();
        }
        reader.close();
        return text.toString();
    }

    // Finds a number after "key": in a JSON text. Returns -1 if it is not found.
    // Example: text = {"price":4140.25}  key = price  ->  4140.25
    static double findNumber(String text, String key) {
        int keyPosition = text.indexOf("\"" + key + "\"");
        if (keyPosition < 0) {
            return -1;
        }
        int colonPosition = text.indexOf(":", keyPosition);
        if (colonPosition < 0) {
            return -1;
        }

        int start = colonPosition + 1;
        while (start < text.length() && text.charAt(start) == ' ') {
            start++;
        }
        int end = start;
        while (end < text.length() && "0123456789.-+eE".indexOf(text.charAt(end)) >= 0) {
            end++;
        }

        try {
            return Double.parseDouble(text.substring(start, end));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
