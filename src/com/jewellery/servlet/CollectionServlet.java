package com.jewellery.servlet;

import com.jewellery.model.CartItem;
import com.jewellery.model.CartLine;
import com.jewellery.model.CollectionSection;
import com.jewellery.model.Item;
import com.jewellery.storage.CartManager;
import com.jewellery.storage.CustomerManager;
import com.jewellery.storage.ItemManager;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// CollectionServlet: puts every jewellery item into its section (by category) and opens the collection page.
// The pieces come from ItemManager, so adding an item in "Jewellery Items" updates the collection too.
public class CollectionServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!LoginServlet.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        ArrayList<CollectionSection> sections = buildSections(ItemManager.getAllItems());
        request.setAttribute("sections", sections);
        // the section buttons are shown in 2 equal rows: 10 sections = 5 on top and 5 below
        request.setAttribute("pillColumns", (sections.size() + 1) / 2);
        request.setAttribute("totalPieces", ItemManager.getAllItems().size());
        // the admin chooses the customer whose cart the "Add to Cart" buttons fill
        int cartCustomerId = CartServlet.getActiveCustomerId(request.getSession());
        request.setAttribute("customers", CustomerManager.getAllCustomers());
        request.setAttribute("cartCustomerId", cartCustomerId);
        request.setAttribute("cartMine", cartCustomerId < 0 ? 0 : CartManager.countPieces(cartCustomerId));

        // view=cart: instead of the whole collection, show only the pieces that are in the chosen customer's cart
        boolean cartView = "cart".equals(request.getParameter("view")) && cartCustomerId >= 0;
        request.setAttribute("cartView", cartView);
        if (cartView) {
            ArrayList<CartLine> cartLines = new ArrayList<CartLine>();
            double cartTotal = 0;
            ArrayList<CartItem> cart = CartManager.getCart(cartCustomerId);
            for (int i = 0; i < cart.size(); i++) {
                Item item = ItemManager.getItemById(cart.get(i).getItemId());
                if (item != null) {   // a deleted piece is skipped
                    CartLine line = new CartLine(item, cart.get(i).getQuantity());
                    cartLines.add(line);
                    cartTotal += line.getTotal();
                }
            }
            request.setAttribute("cartLines", cartLines);
            request.setAttribute("cartTotal", cartTotal);
            request.setAttribute("cartCustomerName", CustomerManager.getCustomerById(cartCustomerId).getName());
        }
        request.getRequestDispatcher("/collection.jsp").forward(request, response);
    }

    // Makes the sections and puts each item in the section that matches its category
    static ArrayList<CollectionSection> buildSections(ArrayList<Item> items) {
        String[] ids = {"rings", "necklaces", "earrings", "bracelets", "anklets", "nosepins",
                "armcuffs", "bangles", "bridalsets", "maangtikkas"};
        String[] titles = {"Rings", "Necklaces", "Earrings", "Bracelets", "Anklets", "Nose Pins",
                "Arm Cuffs", "Bangles", "Bridal Sets", "Maang Tikka"};
        String[] icons = {"bi-circle", "bi-gem", "bi-stars", "bi-link-45deg", "bi-music-note-beamed",
                "bi-star", "bi-record-circle", "bi-circle-square", "bi-gem", "bi-gem"};
        String[] keys = {"ring", "necklace", "earring", "bracelet", "anklet", "nosepin",
                "armcuff", "bangle", "bridalset", "maangtikka"};
        String[] categories = {"Ring", "Necklace", "Earring", "Bracelet", "Anklet", "Nose Pin",
                "Arm Cuff", "Bangle", "Bridal Set", "Maang Tikka"};

        ArrayList<CollectionSection> sections = new ArrayList<CollectionSection>();
        for (int i = 0; i < ids.length; i++) {
            sections.add(new CollectionSection(ids[i], titles[i], icons[i], categories[i]));
        }
        CollectionSection others = new CollectionSection("others", "Other Pieces", "bi-gem", "");

        // we go from the first item to the last, so a newly added piece comes LAST in its own section
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String itemKey = makeKey(item.getCategory());
            boolean placed = false;
            for (int k = 0; k < keys.length; k++) {
                if (keys[k].equals(itemKey)) {
                    sections.get(k).getItems().add(item);
                    placed = true;
                }
            }
            if (!placed) {
                others.getItems().add(item);
            }
        }

        if (others.getCount() > 0) {
            sections.add(others);   // only shown when some pieces do not fit a section
        }
        return sections;
    }

    // Makes a category easy to compare: "Nose Pins" -> "nosepin", "Rings" -> "ring"
    // (the work is done in Item.makeKey so the page and the pictures use the same rule)
    static String makeKey(String category) {
        return Item.makeKey(category);
    }
}
