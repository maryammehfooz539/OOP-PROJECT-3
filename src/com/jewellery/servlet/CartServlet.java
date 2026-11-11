package com.jewellery.servlet;

import com.jewellery.model.CartItem;
import com.jewellery.model.CartLine;
import com.jewellery.model.CartSummary;
import com.jewellery.model.Customer;
import com.jewellery.model.Item;
import com.jewellery.storage.CartManager;
import com.jewellery.storage.CustomerManager;
import com.jewellery.storage.ItemManager;
import com.jewellery.storage.SaleManager;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// CartServlet: every customer has an own cart, managed by the admin.
// The admin chooses a customer (on the Collection page or the Cart page); "Add to Cart" puts the piece in
// THAT customer's cart. On the cart page the admin changes quantities (+ / -), removes pieces and checks out:
// checkout saves one sale for every piece for that customer (stock reduces automatically) and empties the cart.
// The carts are saved in data/carts.txt (CartManager), so they are still there after the program is closed.
public class CartServlet extends HttpServlet {

    // Opens the cart page of one customer (/cart?customerId=2) or of the customer chosen last
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!LoginServlet.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int fromLink = toInt(request.getParameter("customerId"));
        if (CustomerManager.getCustomerById(fromLink) != null) {
            request.getSession().setAttribute("cartCustomerId", fromLink);   // remember the chosen customer
        }
        showCart(request, response);
    }

    // All changes come here: select (choose customer), add, update, remove, clear, checkout
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!LoginServlet.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        String back = request.getParameter("back");
        boolean fromCollection = "collection".equals(back);
        boolean fromCartView = "cartview".equals(back);   // the "cart of a customer" view on the Collection page

        // which customer's cart? the one sent by the form, or the customer chosen last
        int customerId = toInt(request.getParameter("customerId"));
        if (customerId < 0) {
            customerId = getActiveCustomerId(session);
        }

        String message = "";
        boolean ok = true;

        if (action.equals("select")) {
            // the admin chose a customer in the drop-down (an empty choice means "nobody")
            int chosen = toInt(request.getParameter("customerId"));
            if (CustomerManager.getCustomerById(chosen) == null) {
                session.removeAttribute("cartCustomerId");
            } else {
                session.setAttribute("cartCustomerId", chosen);
            }
            // on the Collection page, choosing a customer shows THAT customer's cart (choosing nobody shows everything)
            if (fromCollection && CustomerManager.getCustomerById(chosen) != null) {
                response.sendRedirect(request.getContextPath() + "/collection?view=cart");
            } else {
                response.sendRedirect(request.getContextPath() + (fromCollection ? "/collection" : "/cart"));
            }
            return;
        } else if (action.equals("add")) {
            message = CartManager.addItem(customerId, toInt(request.getParameter("itemId")), 1);
        } else if (action.equals("update")) {
            message = CartManager.updateQuantity(customerId, toInt(request.getParameter("itemId")),
                    toInt(request.getParameter("quantity")));
        } else if (action.equals("remove")) {
            CartManager.removeItem(customerId, toInt(request.getParameter("itemId")));
            message = "Piece removed from the cart.";
        } else if (action.equals("clear")) {
            CartManager.clearCart(customerId);
            message = "Cart is empty now.";
        } else if (action.equals("checkout")) {
            message = checkout(customerId);
            if (message.isEmpty()) {
                session.setAttribute("cartCount", CartManager.totalPieces());
                session.setAttribute("flashSuccess", "Order placed! Sales saved and stock updated.");
                response.sendRedirect(request.getContextPath() + "/sales");
                return;
            }
            ok = false;   // checkout failed: the message is an error
        } else {
            message = "Unknown cart action.";
            ok = false;
        }

        // errors start with "Sorry"; they are shown as red messages
        if (message.startsWith("Sorry")) {
            ok = false;
        }
        session.setAttribute("cartCount", CartManager.totalPieces());

        // the Collection page adds pieces in the background (fetch) and wants a tiny JSON answer
        if ("fetch".equals(request.getHeader("X-Requested-With"))) {
            response.setContentType("application/json;charset=UTF-8");
            String safe = message.replace("\"", "'");
            response.getWriter().write("{\"ok\":" + ok + ",\"count\":" + CartManager.totalPieces()
                    + ",\"mine\":" + CartManager.countPieces(customerId) + ",\"message\":\"" + safe + "\"}");
            return;
        }

        if (ok) {
            session.setAttribute("flashSuccess", message);
        } else {
            session.setAttribute("flashError", message);
        }
        String target = fromCartView ? "/collection?view=cart" : (fromCollection ? "/collection" : "/cart");
        response.sendRedirect(request.getContextPath() + target);
    }

    // Builds the cart page of the chosen customer and opens cart.jsp
    private void showCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        int customerId = getActiveCustomerId(session);
        Customer customer = CustomerManager.getCustomerById(customerId);

        ArrayList<CartLine> lines = new ArrayList<CartLine>();
        double total = 0;
        int pieces = 0;
        if (customer != null) {
            ArrayList<CartItem> cart = CartManager.getCart(customerId);
            for (int i = 0; i < cart.size(); i++) {
                Item item = ItemManager.getItemById(cart.get(i).getItemId());
                if (item == null) {
                    CartManager.removeItem(customerId, cart.get(i).getItemId());   // the piece was deleted meanwhile
                    continue;
                }
                int quantity = Math.min(cart.get(i).getQuantity(), Math.max(item.getQuantity(), 1));
                CartLine line = new CartLine(item, quantity);
                lines.add(line);
                total += line.getTotal();
                pieces += quantity;
            }
        }

        // the list of customers who have something in their cart
        ArrayList<CartSummary> summaries = new ArrayList<CartSummary>();
        ArrayList<Integer> ids = CartManager.getCustomerIdsWithCarts();
        for (int i = 0; i < ids.size(); i++) {
            Customer c = CustomerManager.getCustomerById(ids.get(i));
            if (c != null) {
                summaries.add(new CartSummary(c.getId(), c.getName(), CartManager.countPieces(c.getId())));
            }
        }
        session.setAttribute("cartCount", CartManager.totalPieces());

        request.setAttribute("customer", customer);
        request.setAttribute("lines", lines);
        request.setAttribute("cartTotal", total);
        request.setAttribute("cartPieces", pieces);
        request.setAttribute("summaries", summaries);
        request.setAttribute("customers", CustomerManager.getAllCustomers());
        request.getRequestDispatcher("/cart.jsp").forward(request, response);
    }

    // Checkout of one customer: checks everything first, then saves one sale per cart line.
    // Returns "" when all is fine, otherwise an error message (and nothing is sold).
    private String checkout(int customerId) {
        if (CustomerManager.getCustomerById(customerId) == null) {
            return "Please choose a customer first.";
        }
        ArrayList<CartItem> cart = new ArrayList<CartItem>(CartManager.getCart(customerId));
        if (cart.isEmpty()) {
            return "This cart is empty.";
        }
        // first check ALL lines, so we never sell only half of the cart
        for (int i = 0; i < cart.size(); i++) {
            Item item = ItemManager.getItemById(cart.get(i).getItemId());
            if (item == null) {
                return "A piece in this cart no longer exists. Please remove it.";
            }
            if (cart.get(i).getQuantity() > item.getQuantity()) {
                return "Not enough stock for " + item.getName() + ". Only " + item.getQuantity() + " piece(s) left.";
            }
        }
        // now save the sales
        for (int i = 0; i < cart.size(); i++) {
            String error = SaleManager.makeSale(customerId, cart.get(i).getItemId(), cart.get(i).getQuantity());
            if (!error.isEmpty()) {
                return error;
            }
        }
        CartManager.clearCart(customerId);
        return "";
    }

    // The customer whose cart is open (chosen last). -1 if nobody was chosen yet or he was deleted.
    static int getActiveCustomerId(HttpSession session) {
        Object saved = session.getAttribute("cartCustomerId");
        if (saved instanceof Integer && CustomerManager.getCustomerById((Integer) saved) != null) {
            return (Integer) saved;
        }
        return -1;
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
