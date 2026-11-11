package com.jewellery.servlet;

import com.jewellery.model.Item;
import com.jewellery.model.PriceQuote;
import com.jewellery.model.Rate;
import com.jewellery.storage.ItemManager;
import com.jewellery.storage.RateManager;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// CalculatorServlet: the Price Calculator page.
// The admin enters weight, metal and making charges; the page uses the latest gold/silver/diamond rates
// (from the Rates page) and shows a full price breakdown. The maths is done by the PriceQuote class.
public class CalculatorServlet extends HttpServlet {

    // The metals in the drop-down
    private static final String[] METALS = {"Gold 24K", "Gold 22K", "Gold 21K", "Gold 18K", "Silver"};

    // Opens the empty calculator (or fills weight and metal from an item: /calculator?itemId=3)
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!LoginServlet.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Item item = ItemManager.getItemById(toInt(request.getParameter("itemId")));
        Rate rate = RateManager.getLatest();
        if (item != null) {
            String metal = metalOf(item.getMetalType());
            request.setAttribute("f_metal", metal);
            request.setAttribute("f_weight", plain(item.getWeight()));
            request.setAttribute("f_rate", plain(rateFor(metal, rate)));
            request.setAttribute("selectedItem", item);
        } else {
            request.setAttribute("f_metal", "Gold 22K");
            request.setAttribute("f_rate", plain(rateFor("Gold 22K", rate)));
        }
        request.setAttribute("f_diamondRate", plain(rate == null ? 0 : rate.getDiamond()));
        request.setAttribute("f_makingType", PriceQuote.MAKING_PERCENT);
        showPage(request, response, rate);
    }

    // Calculates the price from the form
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!LoginServlet.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String metal = text(request.getParameter("metal"));
        String weight = text(request.getParameter("weight"));
        String rate = text(request.getParameter("ratePerTola"));
        String makingType = text(request.getParameter("makingType"));
        String makingValue = text(request.getParameter("makingValue"));
        String carats = text(request.getParameter("carats"));
        String diamondRate = text(request.getParameter("diamondRate"));
        String other = text(request.getParameter("otherCharges"));
        String discount = text(request.getParameter("discount"));

        // keep the typed values so the form is filled again
        request.setAttribute("f_metal", metal);
        request.setAttribute("f_weight", weight);
        request.setAttribute("f_rate", rate);
        request.setAttribute("f_makingType", makingType);
        request.setAttribute("f_makingValue", makingValue);
        request.setAttribute("f_carats", carats);
        request.setAttribute("f_diamondRate", diamondRate);
        request.setAttribute("f_other", other);
        request.setAttribute("f_discount", discount);
        Item item = ItemManager.getItemById(toInt(request.getParameter("itemId")));
        request.setAttribute("selectedItem", item);

        String error = validate(metal, weight, rate, makingType, makingValue, carats, diamondRate, other, discount);
        if (!error.isEmpty()) {
            request.setAttribute("error", error);
        } else {
            PriceQuote quote = new PriceQuote(metal, Double.parseDouble(weight), PriceQuote.purityOf(metal),
                    Double.parseDouble(rate), makingType, Double.parseDouble(makingValue),
                    Double.parseDouble(carats), Double.parseDouble(diamondRate),
                    Double.parseDouble(other), Double.parseDouble(discount));
            request.setAttribute("quote", quote);
        }
        showPage(request, response, RateManager.getLatest());
    }

    // Sends the lists for the drop-downs and the latest rates, then opens calculator.jsp
    private void showPage(HttpServletRequest request, HttpServletResponse response, Rate rate)
            throws ServletException, IOException {
        request.setAttribute("metals", Arrays.asList(METALS));
        request.setAttribute("items", ItemManager.getAllItems());
        request.setAttribute("latestRate", rate);
        request.getRequestDispatcher("/calculator.jsp").forward(request, response);
    }

    // Checks all the numbers. Returns "" if fine, otherwise the error message.
    private String validate(String metal, String weight, String rate, String makingType, String makingValue,
                            String carats, String diamondRate, String other, String discount) {
        if (PriceQuote.purityOf(metal) < 0) {
            return "Please choose a metal.";
        }
        String number = "[0-9]{1,9}(\\.[0-9]{1,2})?";
        if (!weight.matches(number) || !rate.matches(number) || !makingValue.matches(number)
                || !carats.matches(number) || !diamondRate.matches(number) || !other.matches(number)
                || !discount.matches("[0-9]{1,3}(\\.[0-9]{1,2})?")) {
            return "All the boxes must have numbers only (up to 2 decimals). Use 0 when there is nothing to add.";
        }
        if (Double.parseDouble(weight) <= 0 || Double.parseDouble(weight) > 10000) {
            return "Weight must be between 0.01 and 10000 grams.";
        }
        if (Double.parseDouble(rate) <= 0) {
            return "The rate must be more than 0. Set today's rates on the Rates page first.";
        }
        if (!makingType.equals(PriceQuote.MAKING_PERCENT) && !makingType.equals(PriceQuote.MAKING_PER_GRAM)
                && !makingType.equals(PriceQuote.MAKING_FLAT)) {
            return "Please choose how the making charges are counted.";
        }
        if (makingType.equals(PriceQuote.MAKING_PERCENT) && Double.parseDouble(makingValue) > 100) {
            return "Making charges in percent cannot be more than 100.";
        }
        if (Double.parseDouble(discount) > 100) {
            return "Discount cannot be more than 100 percent.";
        }
        return "";
    }

    // Finds the calculator metal for an item's metal type, e.g. "Rose Gold 18K" -> "Gold 18K"
    private String metalOf(String metalType) {
        String m = metalType == null ? "" : metalType.toLowerCase();
        if (m.contains("silver")) {
            return "Silver";
        } else if (m.contains("24")) {
            return "Gold 24K";
        } else if (m.contains("21")) {
            return "Gold 21K";
        } else if (m.contains("18")) {
            return "Gold 18K";
        }
        return "Gold 22K";
    }

    // The latest rate that belongs to a metal (silver rate for silver, gold rate for gold)
    private double rateFor(String metal, Rate rate) {
        if (rate == null) {
            return 0;
        }
        return metal.equals("Silver") ? rate.getSilver() : rate.getGold();
    }

    // Writes a number without 1.0E7 style and with at most 2 decimals
    private String plain(double value) {
        return new java.math.BigDecimal(value).setScale(2, java.math.RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private String text(String value) {
        return value == null ? "" : value.trim();
    }

    private int toInt(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            return -1;
        }
    }
}
