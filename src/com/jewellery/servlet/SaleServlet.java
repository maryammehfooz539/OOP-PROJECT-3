package com.jewellery.servlet;

import com.jewellery.model.Customer;
import com.jewellery.model.Sale;
import com.jewellery.storage.CustomerManager;
import com.jewellery.storage.ItemManager;
import com.jewellery.storage.SaleManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// SaleServlet: handles the sales history, the new sale form, saving a sale and the bill page
public class SaleServlet extends HttpServlet {

    // GET requests: list, new, bill
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!LoginServlet.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        if (action.equals("new")) {
            showSaleForm(request, response);
        } else if (action.equals("bill")) {
            showBill(request, response);
        } else {
            request.setAttribute("sales", SaleManager.getAllSales());
            request.getRequestDispatcher("/sales.jsp").forward(request, response);
        }
    }

    // POST request: save a new sale
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!LoginServlet.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String error = "";
        int customerId = -1;
        int itemId = -1;
        int quantity = 0;
        try {
            customerId = Integer.parseInt(request.getParameter("customerId"));
            itemId = Integer.parseInt(request.getParameter("itemId"));
            quantity = Integer.parseInt(request.getParameter("quantity"));
        } catch (NumberFormatException e) {
            error = "Please select a customer and an item, and enter a whole number for quantity.";
        }

        if (error.isEmpty() && (quantity < 1 || quantity > 100000)) {
            error = "Quantity must be a whole number from 1 to 100000.";
        }

        if (error.isEmpty()) {
            error = SaleManager.makeSale(customerId, itemId, quantity);
        }

        if (!error.isEmpty()) {
            request.setAttribute("error", error);
            request.setAttribute("submitted", true);
            showSaleForm(request, response);
            return;
        }

        // success: go to the bill of the sale we just saved
        int lastIndex = SaleManager.getAllSales().size() - 1;
        int saleId = SaleManager.getAllSales().get(lastIndex).getId();
        request.getSession().setAttribute("flashSuccess", "Sale saved and stock updated!");
        response.sendRedirect(request.getContextPath() + "/sales?action=bill&id=" + saleId);
    }

    // Opens the sale form with the customers and items lists
    private void showSaleForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("customers", CustomerManager.getAllCustomers());
        request.setAttribute("items", ItemManager.getAllItems());
        request.getRequestDispatcher("/sale-form.jsp").forward(request, response);
    }

    // Opens the printable bill page for one sale
    private void showBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = -1;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            id = -1;
        }

        Sale sale = SaleManager.getSaleById(id);
        if (sale == null) {
            request.getSession().setAttribute("flashError", "Bill not found.");
            response.sendRedirect(request.getContextPath() + "/sales");
            return;
        }

        Customer customer = CustomerManager.getCustomerById(sale.getCustomerId());
        request.setAttribute("sale", sale);
        request.setAttribute("customer", customer);   // can be null if the customer was deleted
        request.getRequestDispatcher("/bill.jsp").forward(request, response);
    }
}
