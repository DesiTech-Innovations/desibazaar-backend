package com.desitech.vyaparsathi.expense.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ExpenseTypeValidatorTest {

    @Test
    @DisplayName("Should allow valid operational expense types")
    void shouldAllowValidOperationalExpenses() {
        assertTrue(ExpenseTypeValidator.isValidOperationalExpense("RENT"));
        assertTrue(ExpenseTypeValidator.isValidOperationalExpense("UTILITIES"));
        assertTrue(ExpenseTypeValidator.isValidOperationalExpense("SALARY"));
        assertTrue(ExpenseTypeValidator.isValidOperationalExpense("MARKETING"));
        assertTrue(ExpenseTypeValidator.isValidOperationalExpense("MISCELLANEOUS"));
    }

    @Test
    @DisplayName("Should reject inventory-related expense types")
    void shouldRejectInventoryRelatedExpenses() {
        assertThrows(IllegalArgumentException.class, () -> 
            ExpenseTypeValidator.isValidOperationalExpense("INVENTORY"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            ExpenseTypeValidator.isValidOperationalExpense("STOCK_PURCHASE"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            ExpenseTypeValidator.isValidOperationalExpense("PRODUCT_PURCHASE"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            ExpenseTypeValidator.isValidOperationalExpense("PURCHASE_ORDER"));
    }

    @Test
    @DisplayName("Should handle case insensitive validation")
    void shouldHandleCaseInsensitive() {
        assertTrue(ExpenseTypeValidator.isValidOperationalExpense("rent"));
        assertTrue(ExpenseTypeValidator.isValidOperationalExpense("Rent"));
        assertTrue(ExpenseTypeValidator.isValidOperationalExpense("RENT"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            ExpenseTypeValidator.isValidOperationalExpense("inventory"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            ExpenseTypeValidator.isValidOperationalExpense("Inventory"));
    }

    @Test
    @DisplayName("Should handle empty or null expense types")
    void shouldHandleEmptyOrNullTypes() {
        assertFalse(ExpenseTypeValidator.isValidOperationalExpense(null));
        assertFalse(ExpenseTypeValidator.isValidOperationalExpense(""));
        assertFalse(ExpenseTypeValidator.isValidOperationalExpense("   "));
    }

    @Test
    @DisplayName("Should allow custom expense types not in invalid list")
    void shouldAllowCustomExpenseTypes() {
        assertTrue(ExpenseTypeValidator.isValidOperationalExpense("CUSTOM_OPERATIONAL_EXPENSE"));
        assertTrue(ExpenseTypeValidator.isValidOperationalExpense("OFFICE_CLEANING"));
        assertTrue(ExpenseTypeValidator.isValidOperationalExpense("TRAVEL"));
    }

    @Test
    @DisplayName("Should provide helpful error message for invalid types")
    void shouldProvideHelpfulErrorMessage() {
        String errorMessage = ExpenseTypeValidator.getValidationErrorMessage("INVENTORY");
        assertNotNull(errorMessage);
        assertTrue(errorMessage.contains("Purchase Orders"));
        assertTrue(errorMessage.contains("INVENTORY"));
    }
}