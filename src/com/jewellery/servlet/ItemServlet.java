package com.jewellery.servlet;

import com.jewellery.model.Item;
import com.jewellery.storage.CartManager;
import com.jewellery.storage.FileHelper;
import com.jewellery.storage.ImageDownloader;
import com.jewellery.storage.ItemManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

// ItemServlet: handles list, search, add, edit and delete of jewellery items.
// The add/edit form is sent as "multipart" so the admin can also upload a picture from his computer
// (the multipart settings are in web.xml).
public class ItemServlet extends HttpServlet {

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
            request.getRequestDispatcher("/item-form.jsp").forward(request, response);
        } else if (action.equals("edit")) {
            Item item = ItemManager.getItemById(toInt(request.getParameter("id")));
            if (item == null) {
                request.getSession().setAttribute("flashError", "Item not found.");
                response.sendRedirect(request.getContextPath() + "/items");
            } else {
                request.setAttribute("item", item);
                request.getRequestDispatcher("/item-form.jsp").forward(request, response);
            }
        } else if (action.equals("delete")) {
            int deletedId = toInt(request.getParameter("id"));
            ItemManager.deleteItem(deletedId);
            CartManager.removeItemEverywhere(deletedId);   // the piece is taken out of all carts
            request.getSession().setAttribute("cartCount", CartManager.totalPieces());
            request.getSession().setAttribute("flashSuccess", "Item deleted successfully.");
            response.sendRedirect(request.getContextPath() + "/items");
        } else {
            showList(request, response);
        }
    }

    // POST request: save (add or update) an item
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!LoginServlet.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idText = clean(request.getParameter("id"));
        String name = clean(request.getParameter("name"));
        String category = clean(request.getParameter("category"));
        String metalType = clean(request.getParameter("metalType"));
        String weightText = clean(request.getParameter("weight"));
        String priceText = clean(request.getParameter("price"));
        String quantityText = clean(request.getParameter("quantity"));
        String imageUrl = cleanLink(request.getParameter("imageUrl"));

        String error = validate(name, category, metalType, weightText, priceText, quantityText);
        if (error.isEmpty() && !imageUrl.isEmpty() && (!isAllowedLink(imageUrl) || imageUrl.length() > 300 || imageUrl.contains(" "))) {
            error = "Picture link must start with http:// or https:// (no spaces).";
        }

        // if a picture file was chosen, save it; its saved name replaces the picture link
        if (error.isEmpty()) {
            try {
                String uploaded = saveUploadedPicture(request);
                if (!uploaded.isEmpty()) {
                    imageUrl = uploaded;
                }
            } catch (IllegalArgumentException e) {
                error = e.getMessage();   // wrong file type or file too big
            } catch (IllegalStateException e) {
                error = "The picture is too big. Please choose a picture smaller than 3 MB.";
            } catch (IOException e) {
                error = "The picture could not be saved. Please try again.";
            }
        }

        // an online picture link (for example from Pinterest): download the picture and keep it inside the project,
        // so it never disappears and also shows without internet
        String pictureNote = "";
        if (error.isEmpty() && (imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
            try {
                String saved = ImageDownloader.download(imageUrl);
                if (saved != null) {
                    imageUrl = saved;
                    pictureNote = "saved";
                } else {
                    pictureNote = "kept";   // could not reach it: we keep the link
                }
            } catch (IllegalArgumentException e) {
                error = e.getMessage();   // the link is a web page, too big, wrong format ...
            }
        }
        if (!error.isEmpty()) {
            // show the form again with the error message and the typed values
            request.setAttribute("error", error);
            request.setAttribute("submitted", true);
            request.getRequestDispatcher("/item-form.jsp").forward(request, response);
            return;
        }

        double weight = Double.parseDouble(weightText);
        double price = Double.parseDouble(priceText);
        int quantity = Integer.parseInt(quantityText);

        if (idText.isEmpty()) {
            Item item = new Item(0, name, category, metalType, weight, price, quantity, imageUrl);
            ItemManager.addItem(item);
            flash(request, "Item added successfully!", pictureNote);
        } else {
            Item item = new Item(toInt(idText), name, category, metalType, weight, price, quantity, imageUrl);
            ItemManager.updateItem(item);
            flash(request, "Item updated successfully!", pictureNote);
        }
        response.sendRedirect(request.getContextPath() + "/items");
    }

    // Sets the message shown after saving. pictureNote: "saved" = the picture of the link was downloaded,
    // "kept" = it could not be downloaded so the link is kept, "" = no link was used.
    private void flash(HttpServletRequest request, String message, String pictureNote) {
        if (pictureNote.equals("saved")) {
            request.getSession().setAttribute("flashSuccess", message + " The picture from the link was saved inside the project.");
        } else if (pictureNote.equals("kept")) {
            request.getSession().setAttribute("flashError", message + " But the picture could not be downloaded "
                    + "(no internet, or the website refused), so the link is kept and may still show.");
        } else {
            request.getSession().setAttribute("flashSuccess", message);
        }
    }

    // Shows the items table (all items, or only the search results)
    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter("q");
        if (query != null && query.length() > 50) {
            query = query.substring(0, 50);   // search text is limited to 50 characters
        }
        String filter = request.getParameter("filter");
        ArrayList<Item> items;
        if (query != null && !query.trim().isEmpty()) {
            items = ItemManager.searchItems(query);
        } else if ("low".equals(filter)) {
            items = ItemManager.getLowStockItems();   // opened from the Low Stock card
            request.setAttribute("lowOnly", true);
        } else {
            items = ItemManager.getAllItems();
        }
        // pieces of one category stay together (all rings, then all necklaces ...);
        // a newly added piece goes to the END of its own category, not to the end of the whole list
        ArrayList<Item> sorted = new ArrayList<Item>(items);
        Collections.sort(sorted, new Comparator<Item>() {
            public int compare(Item a, Item b) {
                if (a.getCategoryRank() != b.getCategoryRank()) {
                    return a.getCategoryRank() - b.getCategoryRank();
                }
                return a.getId() - b.getId();   // same category: older pieces first, newest last
            }
        });
        request.setAttribute("items", sorted);
        request.setAttribute("query", query);
        request.getRequestDispatcher("/items.jsp").forward(request, response);
    }

    // Checks the form fields. Returns an empty text if all is fine, otherwise an error message.
    private String validate(String name, String category, String metalType,
                            String weightText, String priceText, String quantityText) {
        if (name.isEmpty() || category.isEmpty() || metalType.isEmpty()
                || weightText.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
            return "Please fill in all the fields.";
        }
        if (!name.matches("[\\p{L}0-9][\\p{L}0-9 &.'()-]{1,49}")) {
            return "Item name must be 2 to 50 characters (letters, numbers, spaces and & . ' ( ) - are allowed).";
        }
        if (!category.matches("[\\p{L}][\\p{L} ]{1,29}")) {
            return "Category must be 2 to 30 letters (no numbers or symbols).";
        }
        if (!metalType.matches("[\\p{L}0-9][\\p{L}0-9 .-]{1,29}")) {
            return "Metal type must be 2 to 30 characters, like Gold 22K or Silver.";
        }
        if (!weightText.matches("[0-9]{1,5}(\\.[0-9]{1,2})?") || !priceText.matches("[0-9]{1,9}(\\.[0-9]{1,2})?")
                || !quantityText.matches("[0-9]{1,5}")) {
            return "Weight and price must be numbers (up to 2 decimals), and quantity a whole number.";
        }
        try {
            double weight = Double.parseDouble(weightText);
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);
            if (weight <= 0 || weight > 10000) {
                return "Weight must be between 0.01 and 10000 grams.";
            }
            if (price <= 0 || price > 100000000) {
                return "Price must be between 1 and 100,000,000.";
            }
            if (quantity < 0 || quantity > 100000) {
                return "Quantity must be between 0 and 100000.";
            }
        } catch (NumberFormatException e) {
            return "Weight and price must be numbers, and quantity must be a whole number.";
        }
        return "";
    }

    // A picture link is fine if it is an internet link, or a picture that is already inside the project
    private boolean isAllowedLink(String link) {
        return link.startsWith("http://") || link.startsWith("https://")
                || link.startsWith("images/") || link.startsWith("uploads/");
    }

    // Saves the picture file chosen in the form into data/uploads and returns its address
    // (like "uploads/item-1700000000000.png"). Returns "" when no file was chosen.
    // Throws IllegalArgumentException with a clear message for a wrong file type or a file over 3 MB.
    private String saveUploadedPicture(HttpServletRequest request) throws IOException, ServletException {
        Part part = request.getPart("imageFile");
        if (part == null || part.getSize() == 0) {
            return "";
        }
        if (part.getSize() > 3 * 1024 * 1024) {
            throw new IllegalArgumentException("The picture is too big. Please choose a picture smaller than 3 MB.");
        }

        String original = part.getSubmittedFileName();
        String extension = "";
        if (original != null && original.lastIndexOf('.') >= 0) {
            extension = original.substring(original.lastIndexOf('.') + 1).toLowerCase();
        }
        boolean okType = extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png")
                || extension.equals("gif") || extension.equals("webp") || extension.equals("svg");
        if (!okType) {
            throw new IllegalArgumentException("Please choose a JPG, PNG, GIF, WEBP or SVG picture.");
        }

        // new file name made from the time, so two pictures never get the same name
        String savedName = "item-" + System.currentTimeMillis() + "." + extension;
        File folder = new File(FileHelper.getUploadFolder());
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Could not create the upload folder");
        }
        Files.copy(part.getInputStream(), new File(folder, savedName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        return "uploads/" + savedName;
    }

    // Removes extra spaces and commas (commas would break our comma-separated file)
    private String clean(String text) {
        if (text == null) {
            return "";
        }
        return text.trim().replace(",", " ");
    }

    // Cleans a picture link (a comma inside a link is changed to %2C so our file is not broken)
    private String cleanLink(String text) {
        if (text == null) {
            return "";
        }
        return text.trim().replace(",", "%2C");
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
