package com.jewellery.servlet;

import com.jewellery.model.Customer;
import com.jewellery.storage.CartManager;
import com.jewellery.storage.CustomerManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// CustomerServlet: handles list, add, edit and delete of customers
public class CustomerServlet extends HttpServlet {

    // GET requests: list, new, edit, delete
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
            request.getRequestDispatcher("/customer-form.jsp").forward(request, response);
        } else if (action.equals("edit")) {
            Customer customer = CustomerManager.getCustomerById(toInt(request.getParameter("id")));
            if (customer == null) {
                request.getSession().setAttribute("flashError", "Customer not found.");
                response.sendRedirect(request.getContextPath() + "/customers");
            } else {
                request.setAttribute("customer", customer);
                request.getRequestDispatcher("/customer-form.jsp").forward(request, response);
            }
        } else if (action.equals("delete")) {
            int deletedId = toInt(request.getParameter("id"));
            CustomerManager.deleteCustomer(deletedId);
            CartManager.clearCart(deletedId);   // his cart is deleted too
            request.getSession().setAttribute("cartCount", CartManager.totalPieces());
            request.getSession().setAttribute("flashSuccess", "Customer deleted successfully.");
            response.sendRedirect(request.getContextPath() + "/customers");
        } else {
            request.setAttribute("customers", CustomerManager.getAllCustomers());
            request.getRequestDispatcher("/customers.jsp").forward(request, response);
        }
    }

    // POST request: save (add or update) a customer
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!LoginServlet.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idText = clean(request.getParameter("id"));
        String name = clean(request.getParameter("name"));
        String phone = clean(request.getParameter("phone"));
        String address = clean(request.getParameter("address"));

        // Validation: name = letters only, phone = digits only, address = a normal address
        String error = "";
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            error = "Please fill in all the fields.";
        } else if (!name.matches("[\\p{L}][\\p{L} .'-]{1,49}")) {
            error = "Name must be 2 to 50 letters (only letters, spaces, . ' - are allowed).";
        } else if (!phone.matches("[0-9]{10,13}")) {
            error = "Phone must be 10 to 13 digits (numbers only, no letters or symbols).";
        } else if (!address.matches("[\\p{L}0-9 .#/'-]{5,100}")) {
            error = "Address must be 5 to 100 characters (letters, numbers, spaces and . # / - are allowed).";
        }

        if (!error.isEmpty()) {
            request.setAttribute("error", error);
            request.setAttribute("submitted", true);
            request.getRequestDispatcher("/customer-form.jsp").forward(request, response);
            return;
        }

        if (idText.isEmpty()) {
            CustomerManager.addCustomer(new Customer(0, name, phone, address));
            request.getSession().setAttribute("flashSuccess", "Customer added successfully!");
        } else {
            CustomerManager.updateCustomer(new Customer(toInt(idText), name, phone, address));
            request.getSession().setAttribute("flashSuccess", "Customer updated successfully!");
        }
        response.sendRedirect(request.getContextPath() + "/customers");
    }

    // Removes extra spaces and commas (commas would break our comma-separated file)
    private String clean(String text) {
        if (text == null) {
            return "";
        }
        return text.trim().replace(",", " ");
    }

    // Turns text into a number (returns -1 if the text is not a number)
    private int toInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
