package com.algomotion.algorithm;

import com.algomotion.model.SortEvent;
import com.algomotion.model.SortResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SelectionSort Algorithm Tests")
class SelectionSortTest {

    private SelectionSort selectionSort;

    @BeforeEach
    void setUp() {
        selectionSort = new SelectionSort();
    }

    @Test
    @DisplayName("1. Sorts an ordinary unsorted array correctly")
    void testOrdinaryUnsortedArray() {
        int[] input = {29, 10, 14, 37, 13};
        SortResult result = selectionSort.sort(input);

        assertArrayEquals(new int[]{10, 13, 14, 29, 37}, result.sortedArray());
        assertEquals(SortEvent.Type.COMPLETED, result.events().getLast().getType());
    }

    @Test
    @DisplayName("2. Handles an already sorted array correctly")
    void testAlreadySortedArray() {
        int[] input = {1, 2, 3, 4, 5};
        SortResult result = selectionSort.sort(input);

        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result.sortedArray());
        // For n=5, Selection Sort always performs 4 + 3 + 2 + 1 = 10 comparisons
        assertEquals(10, result.statistics().comparisons());
        assertEquals(0, result.statistics().swaps(), "No swaps needed when elements already in place.");
    }

    @Test
    @DisplayName("3. Sorts a reverse-sorted array correctly")
    void testReverseSortedArray() {
        int[] input = {5, 4, 3, 2, 1};
        SortResult result = selectionSort.sort(input);

        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result.sortedArray());
        assertEquals(10, result.statistics().comparisons());
        assertTrue(result.statistics().swaps() > 0);
    }

    @Test
    @DisplayName("4. Handles duplicate values properly")
    void testDuplicateValues() {
        int[] input = {4, 2, 4, 1, 3, 2};
        SortResult result = selectionSort.sort(input);

        assertArrayEquals(new int[]{1, 2, 2, 3, 4, 4}, result.sortedArray());
    }

    @Test
    @DisplayName("5. Handles an empty array gracefully")
    void testEmptyArray() {
        int[] input = {};
        SortResult result = selectionSort.sort(input);

        assertArrayEquals(new int[]{}, result.sortedArray());
        assertEquals(0, result.statistics().comparisons());
        assertEquals(0, result.statistics().swaps());
        assertEquals(1, result.events().size());
        assertEquals(SortEvent.Type.COMPLETED, result.events().getFirst().getType());
    }

    @Test
    @DisplayName("6. Handles a single-element array gracefully")
    void testSingleElementArray() {
        int[] input = {99};
        SortResult result = selectionSort.sort(input);

        assertArrayEquals(new int[]{99}, result.sortedArray());
        assertEquals(0, result.statistics().comparisons());
        assertEquals(0, result.statistics().swaps());
        assertEquals(SortEvent.Type.COMPLETED, result.events().getFirst().getType());
    }

    @Test
    @DisplayName("7. Handles negative and mixed-sign values")
    void testNegativeAndMixedValues() {
        int[] input = {-12, 5, -3, 0, 8, -25};
        SortResult result = selectionSort.sort(input);

        assertArrayEquals(new int[]{-25, -12, -3, 0, 5, 8}, result.sortedArray());
    }

    @Test
    @DisplayName("8. Handles an array where all elements are equal")
    void testAllEqualValues() {
        int[] input = {7, 7, 7, 7};
        SortResult result = selectionSort.sort(input);

        assertArrayEquals(new int[]{7, 7, 7, 7}, result.sortedArray());
        assertEquals(6, result.statistics().comparisons());
        assertEquals(0, result.statistics().swaps());
    }

    @Test
    @DisplayName("9. Confirms caller's input array is not modified")
    void testInputPreservation() {
        int[] input = {15, 3, 8};
        int[] copy = input.clone();
        selectionSort.sort(input);

        assertArrayEquals(copy, input, "Caller array must remain untouched.");
    }

    @Test
    @DisplayName("10. Verifies event replay snapshot integrity and metadata")
    void testEventReplayAndMetadata() {
        int[] input = {3, 1, 2};
        SortResult result = selectionSort.sort(input);
        List<SortEvent> events = result.events();

        assertFalse(events.isEmpty());
        // Last event must match sortedArray
        int[] lastSnapshot = events.getLast().getSnapshot();
        assertArrayEquals(result.sortedArray(), lastSnapshot);

        assertEquals("Selection Sort", selectionSort.getName());
        assertEquals("O(n²)", selectionSort.getBestTimeComplexity());
        assertEquals("O(1)", selectionSort.getSpaceComplexity());
    }

    @Test
    @DisplayName("Throws exception on null input")
    void testNullInputThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> selectionSort.sort(null));
    }
}
