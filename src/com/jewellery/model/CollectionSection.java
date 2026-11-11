package com.jewellery.model;

import java.util.ArrayList;

// CollectionSection class: one section of the Collection page (for example "Rings")
public class CollectionSection {
    private String id;      // used for the page link, like "rings"
    private String title;   // shown on the page, like "Rings"
    private String icon;    // Bootstrap icon class (not used now, the page draws its own icons)
    private String addCategory;   // category used by the "Add piece" link, like "Ring"
    private ArrayList<Item> items = new ArrayList<Item>();

    public CollectionSection(String id, String title, String icon, String addCategory) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.addCategory = addCategory;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getIcon() { return icon; }
    public String getAddCategory() { return addCategory; }
    public ArrayList<Item> getItems() { return items; }

    // How many pieces are in this section
    public int getCount() {
        return items.size();
    }
}
