package com.jewellery.servlet;

import com.jewellery.storage.CartManager;
import com.jewellery.storage.CustomerManager;
import com.jewellery.storage.DataFileException;
import com.jewellery.storage.FileHelper;
import com.jewellery.storage.ItemManager;
import com.jewellery.storage.OrderManager;
import com.jewellery.storage.RateManager;
import com.jewellery.storage.SaleManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// LoginServlet: shows the login page and checks the password
public class LoginServlet extends HttpServlet {

    // The one admin password (exact match needed). On a public website the owner sets his own password
    // in the ADMIN_PASSWORD variable; if there is none, the school-project default admin123 is used.
    private static final String ADMIN_PASSWORD = findPassword();

    private static String findPassword() {
        String fromHost = System.getenv("ADMIN_PASSWORD");
        if (fromHost != null && !fromHost.isEmpty()) {
            return fromHost;
        }
        return "admin123";
    }

    // Runs once when the app starts: loads all data files into the ArrayLists
    // If a file cannot be read, the problem is written to the server log and the app still starts.
    public void init() {
        try {
            ItemManager.loadFromFile();
            CustomerManager.loadFromFile();
            SaleManager.loadFromFile();
            RateManager.loadFromFile();
            OrderManager.loadFromFile();
            CartManager.loadFromFile();
            log("Data loaded from folder: " + FileHelper.getDataFolder());
        } catch (DataFileException e) {
            log("Could not load the data files: " + e.getMessage(), e);
        }
    }

    // Used by all other servlets to check if the admin is logged in
    public static boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("admin") != null;
    }

    // Show the login page (or go to the dashboard if already logged in)
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    // Check the password from the login form. The page opens ONLY when the password is exactly right:
    // no extra letters, no missing letters, no spaces (capital and small letters matter).
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String password = request.getParameter("password");

        if (password == null || password.isEmpty()) {
            request.setAttribute("error", "Please enter the password.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else if (password.length() > 30) {
            request.setAttribute("error", "The password is too long.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else if (ADMIN_PASSWORD.equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("admin", "Admin");
            session.setAttribute("cartCount", CartManager.totalPieces());   // pieces in all customers' carts
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("error", "Wrong password. Please try again.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
