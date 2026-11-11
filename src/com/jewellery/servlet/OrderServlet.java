package com.jewellery.servlet;

import com.jewellery.model.Customer;
import com.jewellery.model.ServiceOrder;
import com.jewellery.storage.CustomerManager;
import com.jewellery.storage.OrderManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// OrderServlet: "Orders & Repairs" = customer order tracking.
// List (with status filter), add, edit, delete, and a quick status change (Received -> In Progress -> Ready -> Delivered).
public class OrderServlet extends HttpServlet {

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
            openForm(request, response);
        } else if (action.equals("edit")) {
            ServiceOrder order = OrderManager.getOrderById(toInt(request.getParameter("id")));
            if (order == null) {
                request.getSession().setAttribute("flashError", "Order not found.");
                response.sendRedirect(request.getContextPath() + "/orders");
            } else {
                request.setAttribute("order", order);
                openForm(request, response);
            }
        } else if (action.equals("delete")) {
            OrderManager.deleteOrder(toInt(request.getParameter("id")));
            request.getSession().setAttribute("flashSuccess", "Order deleted successfully.");
            response.sendRedirect(request.getContextPath() + "/orders");
        } else {
            showList(request, response);
        }
    }

    // POST requests: save (add or update) an order, or change only the status
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!LoginServlet.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if ("status".equals(request.getParameter("action"))) {
            changeStatus(request, response);
            return;
        }

        String idText = clean(request.getParameter("id"));
        int customerId = toInt(request.getParameter("customerId"));
        String type = clean(request.getParameter("type"));
        String description = clean(request.getParameter("description"));
        String received = clean(request.getParameter("receivedDate"));
        String due = clean(request.getParameter("dueDate"));
        String totalText = clean(request.getParameter("totalPrice"));
        String advanceText = clean(request.getParameter("advance"));
        String status = clean(request.getParameter("status"));

        String error = validate(customerId, type, description, received, due, totalText, advanceText, status);
        if (!error.isEmpty()) {
            // show the form again with the error message and the typed values
            request.setAttribute("error", error);
            request.setAttribute("submitted", true);
            openForm(request, response);
            return;
        }

        Customer customer = CustomerManager.getCustomerById(customerId);
        ServiceOrder order = new ServiceOrder(0, customerId, customer.getName(), type, description,
                received, due, Double.parseDouble(totalText), Double.parseDouble(advanceText), status);

        if (idText.isEmpty()) {
            OrderManager.addOrder(order);
            request.getSession().setAttribute("flashSuccess", "Order saved successfully.");
        } else {
            order.setId(toInt(idText));
            OrderManager.updateOrder(order);
            request.getSession().setAttribute("flashSuccess", "Order updated successfully.");
        }
        response.sendRedirect(request.getContextPath() + "/orders");
    }

    // Quick status change from the list
    private void changeStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String status = clean(request.getParameter("status"));
        String back = request.getContextPath() + "/orders";
        String filter = request.getParameter("filter");
        if (filter != null && Arrays.asList(ServiceOrder.STATUSES).contains(filter)) {
            back += "?status=" + filter.replace(" ", "%20");
        }

        if (!Arrays.asList(ServiceOrder.STATUSES).contains(status)) {
            request.getSession().setAttribute("flashError", "Unknown status.");
        } else {
            ServiceOrder order = OrderManager.updateStatus(toInt(request.getParameter("id")), status);
            if (order == null) {
                request.getSession().setAttribute("flashError", "Order not found.");
            } else if (status.equals("Delivered") && order.getBalance() > 0) {
                request.getSession().setAttribute("flashSuccess", "Status changed to Delivered. Remember: Rs. "
                        + Math.round(order.getBalance()) + " is still unpaid.");
            } else {
                request.getSession().setAttribute("flashSuccess", "Status changed to " + status + ".");
            }
        }
        response.sendRedirect(back);
    }

    // Shows the table of orders (all, or one status) with the filter buttons and their counts
    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String filter = request.getParameter("status");
        if (filter == null || !Arrays.asList(ServiceOrder.STATUSES).contains(filter)) {
            filter = "All";
        }
        Map<String, Integer> counts = new LinkedHashMap<String, Integer>();
        counts.put("All", OrderManager.getAllOrders().size());
        for (int i = 0; i < ServiceOrder.STATUSES.length; i++) {
            counts.put(ServiceOrder.STATUSES[i], OrderManager.countByStatus(ServiceOrder.STATUSES[i]));
        }
        request.setAttribute("orders", OrderManager.getByStatus(filter));
        request.setAttribute("filter", filter);
        request.setAttribute("counts", counts);
        request.setAttribute("statuses", Arrays.asList(ServiceOrder.STATUSES));
        request.getRequestDispatcher("/orders.jsp").forward(request, response);
    }

    // Opens the add/edit form (the lists for the drop-downs are sent along)
    private void openForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("customers", CustomerManager.getAllCustomers());
        request.setAttribute("statuses", Arrays.asList(ServiceOrder.STATUSES));
        request.setAttribute("types", Arrays.asList(ServiceOrder.TYPES));
        request.setAttribute("today", LocalDate.now().toString());
        request.getRequestDispatcher("/order-form.jsp").forward(request, response);
    }

    // Checks all the values. Returns "" if everything is fine, otherwise the error message.
    private String validate(int customerId, String type, String description, String received, String due,
                            String totalText, String advanceText, String status) {
        if (CustomerManager.getCustomerById(customerId) == null) {
            return "Please select a customer.";
        }
        if (!Arrays.asList(ServiceOrder.TYPES).contains(type)) {
            return "Please choose Custom Order or Repair.";
        }
        if (!description.matches("[\\p{L}0-9][\\p{L}0-9 .#/'()&-]{4,149}")) {
            return "Description must be 5 to 150 characters (letters, numbers, spaces and . # / ' ( ) & - are allowed).";
        }
        LocalDate receivedDate;
        LocalDate dueDate;
        try {
            receivedDate = LocalDate.parse(received);
            dueDate = LocalDate.parse(due);
        } catch (DateTimeParseException e) {
            return "Please choose a valid received date and due date.";
        }
        if (dueDate.isBefore(receivedDate)) {
            return "The due date cannot be before the received date.";
        }
        if (!totalText.matches("[0-9]{1,9}(\\.[0-9]{1,2})?") || !advanceText.matches("[0-9]{1,9}(\\.[0-9]{1,2})?")) {
            return "Price and advance must be numbers only (up to 2 decimals).";
        }
        double total = Double.parseDouble(totalText);
        double advance = Double.parseDouble(advanceText);
        if (total <= 0 || total > 100000000) {
            return "Price must be between 1 and 100,000,000.";
        }
        if (advance > total) {
            return "The advance cannot be more than the total price.";
        }
        if (!Arrays.asList(ServiceOrder.STATUSES).contains(status)) {
            return "Please choose a status.";
        }
        return "";
    }

    // Removes extra spaces and commas (commas would break the comma-separated file)
    private String clean(String text) {
        if (text == null) {
            return "";
        }
        return text.replace(",", " ").replaceAll("\\s+", " ").trim();
    }

    // Turns text into a number (returns -1 if the text is not a number)
    private int toInt(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            return -1;
        }
    }
}
