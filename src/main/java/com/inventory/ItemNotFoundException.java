package com.inventory;

/** Thrown when an operation refers to an item ID that is not in the inventory. */
public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String id) {
        super("Item not found: " + id);
    }
}
