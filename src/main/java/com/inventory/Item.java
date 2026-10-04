package com.inventory;

/** A single stock-keeping record in the inventory. */
public class Item {
    private final String id;
    private String name;
    private int quantity;
    private double price;

    public Item(String id, String name, int quantity, double price) {
        requireNonBlank(id, "ID");
        requireNonBlank(name, "Name");
        requireNonNegative(quantity, "Quantity");
        requireNonNegative(price, "Price");
        this.id = id.trim();
        this.name = name.trim();
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    public void setName(String name) {
        requireNonBlank(name, "Name");
        this.name = name.trim();
    }

    public void setQuantity(int quantity) {
        requireNonNegative(quantity, "Quantity");
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        requireNonNegative(price, "Price");
        this.price = price;
    }

    // Shared validation helpers (package-private so InventoryManager can validate BEFORE mutating).
    static void requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }

    static void requireNonNegative(double value, String field) {
        if (value < 0) {
            throw new IllegalArgumentException(field + " must not be negative");
        }
    }

    @Override
    public String toString() {
        return String.format("Item[id=%s, name=%s, qty=%d, price=%.2f]", id, name, quantity, price);
    }
}
