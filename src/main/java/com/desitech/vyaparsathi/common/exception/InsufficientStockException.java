package com.desitech.vyaparsathi.common.exception;

/**
 * A custom exception to indicate that a requested quantity of an item is not available in stock.
 * This provides a more specific and descriptive error than a generic RuntimeException.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }
}