package com.algomotion.algorithm;

import com.algomotion.model.SortEvent;
import com.algomotion.model.SortResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InsertionSort Algorithm Tests")
class InsertionSortTest {

    private InsertionSort insertionSort;

    @BeforeEach
    void setUp() {
        insertionSort = new InsertionSort();
    }

    @Test
    @DisplayName("1. Sorts an ordinary unsorted array correctly")
    void testOrdinaryUnsortedArray() {
        int[] input = {12, 11, 13, 5, 6};
        SortResult result = insertionSort.sort(input);

        assertArrayEquals(new int[]{5, 6, 11, 12, 13}, result.sortedArray());
        assertEquals(SortEvent.Type.COMPLETED, result.events().getLast().getType());
        assertTrue(result.statistics().writes() > 0, "Writes should be recorded for shifts and key insertion.");
    }

    @Test
    @DisplayName("2. Handles an already sorted array in linear O(n) comparisons and 0 writes")
    void testAlreadySortedArray() {
        int[] input = {10, 20, 30, 40, 50};
        SortResult result = insertionSort.sort(input);

        assertArrayEquals(new int[]{10, 20, 30, 40, 50}, result.sortedArray());
        // For n=5 already sorted, each element i > 0 is compared once with arr[i-1] -> 4 comparisons
        assertEquals(4, result.statistics().comparisons());
        assertEquals(0, result.statistics().writes(), "No shifts or writes should occur on an already-sorted array.");
    }

    @Test
    @DisplayName("3. Sorts a reverse-sorted array correctly (worst-case)")
    void testReverseSortedArray() {
        int[] input = {5, 4, 3, 2, 1};
        SortResult result = insertionSort.sort(input);

        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result.sortedArray());
        assertEquals(10, result.statistics().comparisons(), "Worst-case comparisons for n=5 is 10.");
        assertTrue(result.statistics().writes() > 0);
    }

    @Test
    @DisplayName("4. Handles duplicate values properly and stably")
    void testDuplicateValues() {
        int[] input = {3, 1, 2, 3, 1, 2};
        SortResult result = insertionSort.sort(input);

        assertArrayEquals(new int[]{1, 1, 2, 2, 3, 3}, result.sortedArray());
    }

    @Test
    @DisplayName("5. Handles an empty array gracefully")
    void testEmptyArray() {
        int[] input = {};
        SortResult result = insertionSort.sort(input);

        assertArrayEquals(new int[]{}, result.sortedArray());
        assertEquals(0, result.statistics().comparisons());
        assertEquals(0, result.statistics().writes());
        assertEquals(1, result.events().size());
        assertEquals(SortEvent.Type.COMPLETED, result.events().getFirst().getType());
    }

    @Test
    @DisplayName("6. Handles a single-element array gracefully")
    void testSingleElementArray() {
        int[] input = {7};
        SortResult result = insertionSort.sort(input);

        assertArrayEquals(new int[]{7}, result.sortedArray());
        assertEquals(0, result.statistics().comparisons());
        assertEquals(0, result.statistics().writes());
        assertEquals(SortEvent.Type.COMPLETED, result.events().getFirst().getType());
    }

    @Test
    @DisplayName("7. Handles negative and mixed-sign values")
    void testNegativeAndMixedValues() {
        int[] input = {-5, 10, -20, 0, 15, -1};
        SortResult result = insertionSort.sort(input);

        assertArrayEquals(new int[]{-20, -5, -1, 0, 10, 15}, result.sortedArray());
    }

    @Test
    @DisplayName("8. Handles an array where all elements are equal")
    void testAllEqualValues() {
        int[] input = {9, 9, 9, 9, 9};
        SortResult result = insertionSort.sort(input);

        assertArrayEquals(new int[]{9, 9, 9, 9, 9}, result.sortedArray());
        assertEquals(4, result.statistics().comparisons());
        assertEquals(0, result.statistics().writes());
    }

    @Test
    @DisplayName("9. Confirms caller's input array is not modified")
    void testInputPreservation() {
        int[] input = {8, 4, 6};
        int[] copy = input.clone();
        insertionSort.sort(input);

        assertArrayEquals(copy, input, "Original array must not be altered.");
    }

    @Test
    @DisplayName("10. Verifies event sequence highlights key, shift, and final snapshot")
    void testEventSequenceAndSnapshots() {
        int[] input = {2, 1};
        SortResult result = insertionSort.sort(input);
        List<SortEvent> events = result.events();

        assertFalse(events.isEmpty());
        // Should have HIGHLIGHT_KEY, COMPARE, SHIFT, WRITE, COMPLETED
        boolean hasKey = events.stream().anyMatch(e -> e.getType() == SortEvent.Type.HIGHLIGHT_KEY);
        boolean hasShift = events.stream().anyMatch(e -> e.getType() == SortEvent.Type.SHIFT);
        boolean hasWrite = events.stream().anyMatch(e -> e.getType() == SortEvent.Type.WRITE);

        assertTrue(hasKey, "Must emit HIGHLIGHT_KEY event.");
        assertTrue(hasShift, "Must emit SHIFT event.");
        assertTrue(hasWrite, "Must emit WRITE event.");

        assertArrayEquals(result.sortedArray(), events.getLast().getSnapshot());
    }

    @Test
    @DisplayName("Throws exception on null input")
    void testNullInputThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> insertionSort.sort(null));
    }
}
