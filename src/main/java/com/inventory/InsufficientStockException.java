package com.inventory;

/** Thrown when a stock adjustment would drive quantity below zero. */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String id, int available, int requested) {
        super("Insufficient stock for " + id + ": available=" + available + ", requested=" + requested);
    }
}
