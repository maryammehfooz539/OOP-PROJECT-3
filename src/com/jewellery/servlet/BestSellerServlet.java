package com.jewellery.servlet;

import com.jewellery.model.BestSeller;
import com.jewellery.model.Item;
import com.jewellery.model.Sale;
import com.jewellery.storage.ItemManager;
import com.jewellery.storage.SaleManager;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// BestSellerServlet: finds which items sold the most and opens the Best Sellers page
public class BestSellerServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!LoginServlet.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        ArrayList<BestSeller> all = buildList(SaleManager.getAllSales());

        // totals of all sales
        int totalUnits = 0;
        double totalRevenue = 0;
        for (int i = 0; i < all.size(); i++) {
            totalUnits = totalUnits + all.get(i).getUnitsSold();
            totalRevenue = totalRevenue + all.get(i).getRevenue();
        }

        // only the top 10 go to the chart and the table
        ArrayList<BestSeller> top = new ArrayList<BestSeller>();
        for (int i = 0; i < all.size() && i < 10; i++) {
            top.add(all.get(i));
        }

        request.setAttribute("bestSellers", top);
        request.setAttribute("topItem", top.isEmpty() ? null : top.get(0));
        request.setAttribute("totalUnits", totalUnits);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("chartLabels", namesToJson(top));
        request.setAttribute("chartUnits", unitsToJson(top));
        request.setAttribute("chartRevenue", revenueToJson(top));
        request.getRequestDispatcher("/best-sellers.jsp").forward(request, response);
    }

    // Adds up the sales of each item, then sorts them: most units sold first
    static ArrayList<BestSeller> buildList(ArrayList<Sale> sales) {
        ArrayList<BestSeller> list = new ArrayList<BestSeller>();

        for (int i = 0; i < sales.size(); i++) {
            Sale sale = sales.get(i);

            BestSeller found = null;
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).getItemId() == sale.getItemId()) {
                    found = list.get(j);
                }
            }

            if (found == null) {
                Item item = ItemManager.getItemById(sale.getItemId());
                if (item == null) {
                    found = new BestSeller(sale.getItemId(), sale.getItemName(), "-", -1);
                } else {
                    found = new BestSeller(item.getId(), item.getName(), item.getCategory(), item.getQuantity());
                }
                list.add(found);
            }
            found.addSale(sale.getQuantity(), sale.getTotal());
        }

        // simple bubble sort (most units first, then most money first)
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - 1 - i; j++) {
                BestSeller a = list.get(j);
                BestSeller b = list.get(j + 1);
                boolean swap = false;
                if (b.getUnitsSold() > a.getUnitsSold()) {
                    swap = true;
                } else if (b.getUnitsSold() == a.getUnitsSold() && b.getRevenue() > a.getRevenue()) {
                    swap = true;
                }
                if (swap) {
                    list.set(j, b);
                    list.set(j + 1, a);
                }
            }
        }
        return list;
    }

    // Makes a safe JSON text like ["Ring","Necklace"] for the chart labels
    static String namesToJson(ArrayList<BestSeller> list) {
        String json = "[";
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                json = json + ",";
            }
            json = json + "\"" + safeText(list.get(i).getItemName()) + "\"";
        }
        return json + "]";
    }

    // Makes a JSON text of the units sold, like [5,3,2]
    static String unitsToJson(ArrayList<BestSeller> list) {
        String json = "[";
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                json = json + ",";
            }
            json = json + list.get(i).getUnitsSold();
        }
        return json + "]";
    }

    // Makes a JSON text of the money earned, like [650000,120000]
    static String revenueToJson(ArrayList<BestSeller> list) {
        String json = "[";
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                json = json + ",";
            }
            json = json + Math.round(list.get(i).getRevenue());
        }
        return json + "]";
    }

    // Removes characters that could break the JavaScript text
    private static String safeText(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("<", "\\u003c").replace(">", "\\u003e").replace("&", "\\u0026");
    }
}
