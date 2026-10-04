package com.inventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Item model validation")
class ItemTest {

    @Test
    @DisplayName("valid item stores all fields (trimmed)")
    void constructor_validArguments_storesFields() {
        Item item = new Item(" A1 ", "  Widget ", 5, 2.50);
        assertAll(
            () -> assertEquals("A1", item.getId()),
            () -> assertEquals("Widget", item.getName()),
            () -> assertEquals(5, item.getQuantity()),
            () -> assertEquals(2.50, item.getPrice(), 0.0001));
    }

    @ParameterizedTest(name = "blank id [{0}] is rejected")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void constructor_blankId_throws(String badId) {
        assertThrows(IllegalArgumentException.class, () -> new Item(badId, "Widget", 1, 1.0));
    }

    @ParameterizedTest(name = "blank name [{0}] is rejected")
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void constructor_blankName_throws(String badName) {
        assertThrows(IllegalArgumentException.class, () -> new Item("A1", badName, 1, 1.0));
    }

    @Test
    @DisplayName("negative quantity is rejected")
    void constructor_negativeQuantity_throws() {
        assertThrows(IllegalArgumentException.class, () -> new Item("A1", "Widget", -1, 1.0));
    }

    @Test
    @DisplayName("negative price is rejected")
    void constructor_negativePrice_throws() {
        assertThrows(IllegalArgumentException.class, () -> new Item("A1", "Widget", 1, -0.01));
    }

    @Test
    @DisplayName("zero quantity and zero price are allowed (boundary)")
    void constructor_zeroBoundaries_allowed() {
        assertDoesNotThrow(() -> new Item("A1", "Freebie", 0, 0.0));
    }

    @Test
    @DisplayName("setQuantity rejects negative values")
    void setQuantity_negative_throws() {
        Item item = new Item("A1", "Widget", 1, 1.0);
        assertThrows(IllegalArgumentException.class, () -> item.setQuantity(-5));
        assertEquals(1, item.getQuantity(), "quantity must be unchanged after a rejected update");
    }

    @Test
    @DisplayName("setName rejects blank values")
    void setName_blank_throws() {
        Item item = new Item("A1", "Widget", 1, 1.0);
        assertThrows(IllegalArgumentException.class, () -> item.setName("  "));
        assertEquals("Widget", item.getName());
    }
}
