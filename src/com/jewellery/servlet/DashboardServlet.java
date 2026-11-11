package com.jewellery.servlet;

import com.jewellery.storage.CustomerManager;
import com.jewellery.storage.ItemManager;
import com.jewellery.storage.OrderManager;
import com.jewellery.storage.SaleManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// DashboardServlet: counts items, customers, sales and low stock for the 4 cards (and open orders for the welcome box)
public class DashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!LoginServlet.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setAttribute("totalItems", ItemManager.getAllItems().size());
        request.setAttribute("totalCustomers", CustomerManager.getAllCustomers().size());
        request.setAttribute("totalSales", SaleManager.getAllSales().size());
        request.setAttribute("lowStock", ItemManager.countLowStock());
        request.setAttribute("openOrders", OrderManager.countOpen());       // custom orders / repairs not finished yet
        request.setAttribute("overdueOrders", OrderManager.countOverdue()); // of those, how many are late

        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}
