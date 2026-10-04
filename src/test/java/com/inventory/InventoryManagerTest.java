package com.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InventoryManager")
class InventoryManagerTest {

    private InventoryManager inventory;

    @BeforeEach
    void setUp() {
        inventory = new InventoryManager();
    }

    private Item laptop()  { return new Item("L1", "Laptop",   10, 999.99); }
    private Item mouse()   { return new Item("M1", "Mouse",    50,  19.50); }
    private Item monitor() { return new Item("D1", "Monitor",   5, 199.00); }

    // ------------------------------------------------------------------ CREATE
    @Nested
    @DisplayName("Create (addItem)")
    class Create {

        @Test
        @DisplayName("adds an item and increases size")
        void addItem_newItem_isStored() {
            inventory.addItem(laptop());
            assertEquals(1, inventory.size());
            assertEquals("Laptop", inventory.getItem("L1").getName());
        }

        @Test
        @DisplayName("adds several items and keeps insertion order")
        void addItem_multiple_keepsInsertionOrder() {
            inventory.addItem(laptop());
            inventory.addItem(mouse());
            inventory.addItem(monitor());
            List<Item> all = inventory.getAllItems();
            assertEquals(List.of("L1", "M1", "D1"), all.stream().map(Item::getId).toList());
        }

        @Test
        @DisplayName("duplicate ID is rejected and original is preserved")
        void addItem_duplicateId_throwsAndKeepsOriginal() {
            inventory.addItem(laptop());
            Item imposter = new Item("L1", "Fake Laptop", 1, 1.0);
            assertThrows(DuplicateItemException.class, () -> inventory.addItem(imposter));
            assertEquals("Laptop", inventory.getItem("L1").getName());
            assertEquals(1, inventory.size());
        }

        @Test
        @DisplayName("null item is rejected")
        void addItem_null_throws() {
            assertThrows(IllegalArgumentException.class, () -> inventory.addItem(null));
        }
    }

    // -------------------------------------------------------------------- READ
    @Nested
    @DisplayName("Read (getItem / getAllItems)")
    class Read {

        @Test
        @DisplayName("returns the stored item")
        void getItem_existing_returnsItem() {
            Item stored = laptop();
            inventory.addItem(stored);
            assertSame(stored, inventory.getItem("L1"));
        }

        @Test
        @DisplayName("unknown ID throws ItemNotFoundException")
        void getItem_unknown_throws() {
            assertThrows(ItemNotFoundException.class, () -> inventory.getItem("NOPE"));
        }

        @ParameterizedTest(name = "invalid id [{0}] throws IllegalArgumentException")
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        void getItem_blankOrNullId_throws(String id) {
            assertThrows(IllegalArgumentException.class, () -> inventory.getItem(id));
        }

        @Test
        @DisplayName("empty inventory returns an empty list")
        void getAllItems_empty_returnsEmptyList() {
            assertTrue(inventory.getAllItems().isEmpty());
        }

        @Test
        @DisplayName("returned list is a defensive copy")
        void getAllItems_isDefensiveCopy() {
            inventory.addItem(laptop());
            List<Item> snapshot = inventory.getAllItems();
            snapshot.clear();
            assertEquals(1, inventory.size());
        }
    }

    // ------------------------------------------------------------------ UPDATE
    @Nested
    @DisplayName("Update (updateItem / adjustQuantity)")
    class Update {

        @BeforeEach
        void seed() { inventory.addItem(laptop()); }

        @Test
        @DisplayName("updateItem changes name, quantity and price")
        void updateItem_valid_changesAllFields() {
            inventory.updateItem("L1", "Gaming Laptop", 7, 1499.0);
            Item item = inventory.getItem("L1");
            assertAll(
                () -> assertEquals("Gaming Laptop", item.getName()),
                () -> assertEquals(7, item.getQuantity()),
                () -> assertEquals(1499.0, item.getPrice(), 0.0001));
        }

        @Test
        @DisplayName("updateItem on unknown ID throws ItemNotFoundException")
        void updateItem_unknownId_throwsNotFound() {
            assertThrows(ItemNotFoundException.class,
                () -> inventory.updateItem("NOPE", "X", 1, 1.0));
        }

        @Test
        @DisplayName("updateItem with invalid data leaves the item unchanged")
        void updateItem_invalidData_leavesItemUnchanged() {
            assertThrows(IllegalArgumentException.class,
                () -> inventory.updateItem("L1", "Renamed", -3, 5.0));
            Item item = inventory.getItem("L1");
            assertEquals("Laptop", item.getName(), "name must not be half-updated");
            assertEquals(10, item.getQuantity());
            assertEquals(999.99, item.getPrice(), 0.0001);
        }

        @ParameterizedTest(name = "adjust by {0} -> quantity {1}")
        @CsvSource({"5, 15", "-4, 6", "0, 10", "-10, 0"})
        void adjustQuantity_validDeltas_updatesQuantity(int delta, int expected) {
            inventory.adjustQuantity("L1", delta);
            assertEquals(expected, inventory.getItem("L1").getQuantity());
        }

        @Test
        @DisplayName("removing more stock than available throws and keeps quantity")
        void adjustQuantity_belowZero_throwsInsufficientStock() {
            assertThrows(InsufficientStockException.class, () -> inventory.adjustQuantity("L1", -11));
            assertEquals(10, inventory.getItem("L1").getQuantity());
        }

        @Test
        @DisplayName("adjustQuantity on unknown ID throws ItemNotFoundException")
        void adjustQuantity_unknownId_throws() {
            assertThrows(ItemNotFoundException.class, () -> inventory.adjustQuantity("NOPE", 1));
        }
    }

    // ------------------------------------------------------------------ DELETE
    @Nested
    @DisplayName("Delete (removeItem)")
    class Delete {

        @Test
        @DisplayName("removes an existing item")
        void removeItem_existing_isRemoved() {
            inventory.addItem(laptop());
            inventory.addItem(mouse());
            inventory.removeItem("L1");
            assertEquals(1, inventory.size());
            assertThrows(ItemNotFoundException.class, () -> inventory.getItem("L1"));
        }

        @Test
        @DisplayName("unknown ID throws ItemNotFoundException")
        void removeItem_unknown_throws() {
            assertThrows(ItemNotFoundException.class, () -> inventory.removeItem("NOPE"));
        }

        @Test
        @DisplayName("removing the same item twice fails the second time")
        void removeItem_twice_secondCallThrows() {
            inventory.addItem(laptop());
            inventory.removeItem("L1");
            assertThrows(ItemNotFoundException.class, () -> inventory.removeItem("L1"));
        }

        @Test
        @DisplayName("an ID can be re-used after removal")
        void removeItem_thenReAdd_works() {
            inventory.addItem(laptop());
            inventory.removeItem("L1");
            assertDoesNotThrow(() -> inventory.addItem(laptop()));
            assertEquals(1, inventory.size());
        }
    }

    // ------------------------------------------------------------------ SEARCH
    @Nested
    @DisplayName("Search (searchByName)")
    class Search {

        @BeforeEach
        void seed() {
            inventory.addItem(laptop());
            inventory.addItem(mouse());
            inventory.addItem(new Item("M2", "Mouse Pad", 20, 8.0));
        }

        @ParameterizedTest(name = "term [{0}] finds {1} item(s)")
        @CsvSource({"Mouse, 2", "mouse, 2", "MOUSE, 2", "pad, 1", "top, 1", "zzz, 0"})
        void searchByName_variousTerms_caseInsensitivePartialMatch(String term, int expectedCount) {
            assertEquals(expectedCount, inventory.searchByName(term).size());
        }

        @ParameterizedTest(name = "blank term [{0}] returns empty list")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void searchByName_blankOrNull_returnsEmpty(String term) {
            assertTrue(inventory.searchByName(term).isEmpty());
        }
    }

    // ----------------------------------------------------------------- REPORTS
    @Nested
    @DisplayName("Reports (getLowStockItems / getTotalValue)")
    class Reports {

        @BeforeEach
        void seed() {
            inventory.addItem(laptop());   // 10
            inventory.addItem(mouse());    // 50
            inventory.addItem(monitor());  //  5
        }

        @Test
        @DisplayName("low stock uses strictly-less-than the threshold")
        void getLowStockItems_boundary_isStrictlyLessThan() {
            // monitor has exactly 5: threshold 5 must NOT include it, threshold 6 must
            assertTrue(inventory.getLowStockItems(5).isEmpty());
            List<Item> low = inventory.getLowStockItems(6);
            assertEquals(1, low.size());
            assertEquals("D1", low.get(0).getId());
        }

        @Test
        @DisplayName("threshold of zero returns nothing")
        void getLowStockItems_zero_returnsEmpty() {
            assertTrue(inventory.getLowStockItems(0).isEmpty());
        }

        @Test
        @DisplayName("negative threshold is rejected")
        void getLowStockItems_negative_throws() {
            assertThrows(IllegalArgumentException.class, () -> inventory.getLowStockItems(-1));
        }

        @Test
        @DisplayName("total value keeps the cents")
        void getTotalValue_includesCents() {
            // 10*999.99 + 50*19.50 + 5*199.00 = 9999.90 + 975.00 + 995.00
            assertEquals(11969.90, inventory.getTotalValue(), 0.001);
        }

        @Test
        @DisplayName("total value of an empty inventory is zero")
        void getTotalValue_empty_isZero() {
            assertEquals(0.0, new InventoryManager().getTotalValue(), 0.0);
        }

        @Test
        @DisplayName("total value reflects removals")
        void getTotalValue_afterRemoval_decreases() {
            inventory.removeItem("L1");
            assertEquals(1970.00, inventory.getTotalValue(), 0.001);
        }
    }

    // -------------------------------------------------------------- INTEGRATION
    @Test
    @DisplayName("full lifecycle: add -> read -> update -> adjust -> search -> delete")
    void fullLifecycle_endToEnd() {
        inventory.addItem(new Item("K1", "Keyboard", 10, 45.00));
        assertEquals("Keyboard", inventory.getItem("K1").getName());

        inventory.updateItem("K1", "Mechanical Keyboard", 10, 80.00);
        inventory.adjustQuantity("K1", -3);

        assertEquals(1, inventory.searchByName("mechanical").size());
        assertEquals(7, inventory.getItem("K1").getQuantity());
        assertEquals(560.00, inventory.getTotalValue(), 0.001);

        inventory.removeItem("K1");
        assertEquals(0, inventory.size());
        assertEquals(0.0, inventory.getTotalValue(), 0.0);
    }
}
