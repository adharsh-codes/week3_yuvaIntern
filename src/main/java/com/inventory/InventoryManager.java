package com.inventory;

import java.util.*;

public class InventoryManager {
    private final Map<String, Item> items = new LinkedHashMap<>();

    public void addItem(Item item) {
        if (item == null) throw new IllegalArgumentException("Item must not be null");
        if (items.containsKey(item.getId())) throw new DuplicateItemException(item.getId());   // FIX 1
        items.put(item.getId(), item);
    }

    public Item getItem(String id) {
        Item.requireNonBlank(id, "ID");                                                          // FIX 2
        Item item = items.get(id.trim());
        if (item == null) throw new ItemNotFoundException(id);
        return item;
    }

    public void updateItem(String id, String name, int quantity, double price) {
        Item item = getItem(id);
        // FIX 9: validate ALL inputs first so a bad value can never leave the item half-updated.
        Item.requireNonBlank(name, "Name");
        Item.requireNonNegative(quantity, "Quantity");
        Item.requireNonNegative(price, "Price");
        item.setName(name);
        item.setQuantity(quantity);
        item.setPrice(price);
    }

    public void adjustQuantity(String id, int delta) {
        Item item = getItem(id);
        int updated = item.getQuantity() + delta;
        if (updated < 0) throw new InsufficientStockException(id, item.getQuantity(), -delta);   // FIX 4
        item.setQuantity(updated);
    }

    public void removeItem(String id) {
        Item item = getItem(id);                                                                 // FIX 3
        items.remove(item.getId());
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public List<Item> searchByName(String term) {
        List<Item> result = new ArrayList<>();
        if (term == null || term.isBlank()) return result;
        String needle = term.trim().toLowerCase();
        for (Item i : items.values()) {
            if (i.getName().toLowerCase().contains(needle)) result.add(i);                       // FIX 5
        }
        return result;
    }

    public List<Item> getLowStockItems(int threshold) {
        if (threshold < 0) throw new IllegalArgumentException("Threshold must not be negative");
        List<Item> result = new ArrayList<>();
        for (Item i : items.values()) {
            if (i.getQuantity() < threshold) result.add(i);                                      // FIX 6
        }
        return result;
    }

    public double getTotalValue() {
        double total = 0;
        for (Item i : items.values()) {
            total += i.getPrice() * i.getQuantity();                                             // FIX 8
        }
        return total;
    }

    public int size() { return items.size(); }
}
