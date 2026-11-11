package com.jewellery.model;

// Item class: stores the details of one jewellery item
public class Item {
    private int id;
    private String name;
    private String category;
    private String metalType;
    private double weight;   // in grams
    private double price;
    private int quantity;
    private String imageUrl;  // link of the picture (can be empty)

    public Item() {
    }

    // Item without a picture link
    public Item(int id, String name, String category, String metalType,
                double weight, double price, int quantity) {
        this(id, name, category, metalType, weight, price, quantity, "");
    }

    // Item with a picture link
    public Item(int id, String name, String category, String metalType,
                double weight, double price, int quantity, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.metalType = metalType;
        this.weight = weight;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMetalType() { return metalType; }
    public void setMetalType(String metalType) { this.metalType = metalType; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // ---------- picture helpers (used by the Collection page) ----------

    // The short names of the picture categories. Each one has a ready-made picture in images/collection/
    private static final String[] KNOWN_KEYS = {"ring", "necklace", "earring", "bracelet", "anklet",
            "nosepin", "armcuff", "bangle", "bridalset", "maangtikka"};

    // Makes a category easy to compare: "Nose Pins" -> "nosepin", "Rings" -> "ring"
    public static String makeKey(String category) {
        if (category == null) {
            return "";
        }
        String key = category.toLowerCase().replaceAll("[^a-z]", "");
        if (key.contains("tikk") || key.contains("tika")) {
            return "maangtikka";   // Maang Tikka, Maangtikka, Tikka, Tika...
        }
        if (key.contains("bridal") && key.contains("set")) {
            return "bridalset";    // Bridal Set, Bridal Jewellery Sets...
        }
        if (key.endsWith("s")) {
            key = key.substring(0, key.length() - 1);
        }
        return key;
    }

    // Short name of this item's category, or "others" when it is not one of the known categories
    public String getCategoryKey() {
        String key = makeKey(category);
        for (int i = 0; i < KNOWN_KEYS.length; i++) {
            if (KNOWN_KEYS[i].equals(key)) {
                return key;
            }
        }
        return "others";
    }

    // Position of this item's category in the usual order (Ring = 0, Necklace = 1 ...). "Others" come last.
    // Used to keep the pieces of one category together in the lists.
    public int getCategoryRank() {
        String key = getCategoryKey();
        for (int i = 0; i < KNOWN_KEYS.length; i++) {
            if (KNOWN_KEYS[i].equals(key)) {
                return i;
            }
        }
        return KNOWN_KEYS.length;
    }

    // The ready-made picture of this item's category (always exists inside the project)
    public String getDefaultImage() {
        return "images/collection/" + getCategoryKey() + ".svg";
    }

    // The picture to show. A picture link or an uploaded/bundled picture is used when it is valid.
    // A link to a file on someone's own computer (like C:\Users\...) can never work on another
    // computer, so in that case (or when no picture is given) the category picture is used instead.
    public String getImagePath() {
        if (imageUrl != null) {
            String url = imageUrl.trim();
            boolean online = url.startsWith("http://") || url.startsWith("https://");
            boolean inside = url.startsWith("images/") || url.startsWith("uploads/");
            if (online || inside) {
                return url;
            }
        }
        return getDefaultImage();
    }

    // True when the picture comes from the internet (so the page must not add the website path before it)
    public boolean isExternalImage() {
        String path = getImagePath();
        return path.startsWith("http://") || path.startsWith("https://");
    }

    // An item is "low stock" when 5 or fewer pieces are left
    public boolean isLowStock() {
        return quantity <= 5;
    }

    // Converts this item into one comma-separated line for the text file
    public String toLine() {
        return id + "," + name + "," + category + "," + metalType + ","
                + weight + "," + price + "," + quantity + "," + imageUrl;
    }

    // Creates an Item from one line of the text file (returns null if the line is bad)
    public static Item fromLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 7) {
            return null;
        }
        try {
            int id = Integer.parseInt(parts[0].trim());
            double weight = Double.parseDouble(parts[4].trim());
            double price = Double.parseDouble(parts[5].trim());
            int quantity = Integer.parseInt(parts[6].trim());
            String imageUrl = "";
            if (parts.length >= 8) {
                imageUrl = parts[7].trim();   // old files without a picture column still work
            }
            return new Item(id, parts[1].trim(), parts[2].trim(), parts[3].trim(),
                    weight, price, quantity, imageUrl);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
