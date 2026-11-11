package com.jewellery.servlet;

import com.jewellery.model.Rate;
import com.jewellery.storage.LiveRateFetcher;
import com.jewellery.storage.RateManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// RateServlet: shows the gold / silver / diamond rates page and saves new daily rates
public class RateServlet extends HttpServlet {

    // Show the rates page (or send the live rates when the page asks with ?action=live)
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("live".equals(action)) {
            sendLiveRates(request, response);
            return;
        }
        if (!LoginServlet.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        showPage(request, response);
    }

    // Gets the live rates from the internet, saves them, and sends them to the page as JSON text.
    // The page asks for this every minute, so the cards and charts change by themselves.
    private void sendLiveRates(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (!LoginServlet.isLoggedIn(request)) {
            out.print("{\"ok\":false,\"message\":\"Please login again.\"}");
            return;
        }

        String message = LiveRateFetcher.updateRates();
        Rate latest = RateManager.getLatest();
        Rate previous = RateManager.getPrevious();
        if (latest == null) {
            out.print("{\"ok\":false,\"message\":\"No rates available.\"}");
            return;
        }

        double goldChange = 0;
        double silverChange = 0;
        double diamondChange = 0;
        if (previous != null) {
            goldChange = latest.getGold() - previous.getGold();
            silverChange = latest.getSilver() - previous.getSilver();
            diamondChange = latest.getDiamond() - previous.getDiamond();
        }
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        out.print("{\"ok\":" + message.isEmpty()
                + ",\"message\":\"" + message + "\""
                + ",\"date\":\"" + latest.getDate() + "\""
                + ",\"time\":\"" + time + "\""
                + ",\"gold\":" + latest.getGold()
                + ",\"silver\":" + latest.getSilver()
                + ",\"diamond\":" + latest.getDiamond()
                + ",\"goldChange\":" + goldChange
                + ",\"silverChange\":" + silverChange
                + ",\"diamondChange\":" + diamondChange + "}");
    }

    // Save the rates typed in the form
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!LoginServlet.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String date = clean(request.getParameter("date"));
        String goldText = clean(request.getParameter("gold"));
        String silverText = clean(request.getParameter("silver"));
        String diamondText = clean(request.getParameter("diamond"));

        String error = validate(date, goldText, silverText, diamondText);
        if (!error.isEmpty()) {
            request.setAttribute("error", error);
            request.setAttribute("submitted", true);
            showPage(request, response);
            return;
        }

        Rate rate = new Rate(date, Double.parseDouble(goldText),
                Double.parseDouble(silverText), Double.parseDouble(diamondText));
        RateManager.addOrUpdateRate(rate);
        request.getSession().setAttribute("flashSuccess", "Rates saved. The charts are updated!");
        response.sendRedirect(request.getContextPath() + "/rates");
    }

    // Collects the data for the page and opens rates.jsp
    private void showPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ArrayList<Rate> rates = RateManager.getAllRates();

        // newest first, for the history table
        ArrayList<Rate> history = new ArrayList<Rate>();
        for (int i = rates.size() - 1; i >= 0; i--) {
            history.add(rates.get(i));
        }

        Rate latest = RateManager.getLatest();
        Rate previous = RateManager.getPrevious();
        double goldChange = 0;
        double silverChange = 0;
        double diamondChange = 0;
        if (latest != null && previous != null) {
            goldChange = latest.getGold() - previous.getGold();
            silverChange = latest.getSilver() - previous.getSilver();
            diamondChange = latest.getDiamond() - previous.getDiamond();
        }

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        request.setAttribute("rates", rates);
        request.setAttribute("history", history);
        request.setAttribute("latest", latest);
        request.setAttribute("goldChange", goldChange);
        request.setAttribute("silverChange", silverChange);
        request.setAttribute("diamondChange", diamondChange);
        request.setAttribute("today", today);
        request.getRequestDispatcher("/rates.jsp").forward(request, response);
    }

    // Checks the form fields. Returns an empty text if all is fine, otherwise an error message.
    private String validate(String date, String goldText, String silverText, String diamondText) {
        if (date.isEmpty() || goldText.isEmpty() || silverText.isEmpty() || diamondText.isEmpty()) {
            return "Please fill in all the fields.";
        }
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return "Please choose a valid date.";
        }
        try {
            java.time.LocalDate.parse(date);   // rejects dates like 2026-13-45
        } catch (java.time.format.DateTimeParseException e) {
            return "Please choose a valid date.";
        }
        try {
            double gold = Double.parseDouble(goldText);
            double silver = Double.parseDouble(silverText);
            double diamond = Double.parseDouble(diamondText);
            if (gold <= 0 || silver <= 0 || diamond <= 0) {
                return "Rates must be greater than 0.";
            }
            if (gold > 100000000 || silver > 100000000 || diamond > 100000000) {
                return "Rates are too big. Please check the numbers.";
            }
        } catch (NumberFormatException e) {
            return "Rates must be numbers.";
        }
        return "";
    }

    // Removes extra spaces and commas (commas would break our comma-separated file)
    private String clean(String text) {
        if (text == null) {
            return "";
        }
        return text.trim().replace(",", "");
    }
}
