package com.desitech.vyaparsathi.expense.validation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Constants and validation for operational expense types.
 * Ensures inventory/stock purchases are not recorded as business expenses.
 */
public class ExpenseTypeValidator {

    // Valid operational expense types
    public static final Set<String> VALID_OPERATIONAL_EXPENSE_TYPES = new HashSet<>(Arrays.asList(
        "RENT",
        "UTILITIES", 
        "ELECTRICITY",
        "WATER",
        "GAS",
        "INTERNET",
        "PHONE",
        "SALARY",
        "WAGES",
        "MARKETING",
        "ADVERTISING",
        "OFFICE_SUPPLIES",
        "MAINTENANCE",
        "REPAIR",
        "INSURANCE",
        "LEGAL",
        "PROFESSIONAL",
        "TRANSPORTATION",
        "FUEL",
        "OFFICE_RENT",
        "EQUIPMENT_LEASE",
        "SOFTWARE_SUBSCRIPTION",
        "BANK_CHARGES",
        "MISCELLANEOUS"
    ));

    // Inventory/stock purchase related types that should NOT be allowed as expenses
    public static final Set<String> INVALID_EXPENSE_TYPES = new HashSet<>(Arrays.asList(
        "INVENTORY",
        "STOCK_PURCHASE", 
        "PRODUCT_PURCHASE",
        "GOODS_PURCHASE",
        "MERCHANDISE",
        "RAW_MATERIALS",
        "PURCHASE_ORDER",
        "SUPPLIER_PAYMENT",
        "STOCK",
        "ITEM_PURCHASE"
    ));

    /**
     * Validates if the expense type is a valid operational expense
     * @param expenseType the expense type to validate
     * @return true if valid operational expense, false if inventory-related
     * @throws IllegalArgumentException if the expense type is inventory-related
     */
    public static boolean isValidOperationalExpense(String expenseType) {
        if (expenseType == null || expenseType.trim().isEmpty()) {
            return false;
        }
        
        String normalizedType = expenseType.trim().toUpperCase();
        
        // Check if it's explicitly an invalid (inventory-related) type
        if (INVALID_EXPENSE_TYPES.contains(normalizedType)) {
            throw new IllegalArgumentException(
                "Inventory/stock purchases cannot be recorded as business expenses. " +
                "Use Purchase Orders in the inventory module instead. Invalid type: " + expenseType
            );
        }
        
        // For now, allow any type that's not explicitly invalid
        // In future, we could enforce only VALID_OPERATIONAL_EXPENSE_TYPES if needed
        return true;
    }

    /**
     * Get user-friendly error message for invalid expense types
     * @param expenseType the invalid expense type
     * @return error message
     */
    public static String getValidationErrorMessage(String expenseType) {
        return String.format(
            "Invalid expense type '%s'. Inventory/stock purchases should be recorded through Purchase Orders, " +
            "not as business expenses. Use operational expense types like RENT, UTILITIES, SALARY, etc.", 
            expenseType
        );
    }
}