package com.algomotion.algorithm;

import com.algomotion.model.SortEvent;
import com.algomotion.model.SortResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QuickSort Algorithm Tests")
class QuickSortTest {

    private QuickSort quickSort;

    @BeforeEach
    void setUp() {
        quickSort = new QuickSort();
    }

    @Test
    @DisplayName("1. Sorts an ordinary unsorted array correctly")
    void testOrdinaryUnsortedArray() {
        int[] input = {10, 80, 30, 90, 40, 50, 70};
        SortResult result = quickSort.sort(input);

        assertArrayEquals(new int[]{10, 30, 40, 50, 70, 80, 90}, result.sortedArray());
        assertEquals(SortEvent.Type.COMPLETED, result.events().getLast().getType());
        assertTrue(result.statistics().comparisons() > 0);
    }

    @Test
    @DisplayName("2. Handles an already sorted array correctly")
    void testAlreadySortedArray() {
        int[] input = {1, 2, 3, 4, 5};
        SortResult result = quickSort.sort(input);

        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result.sortedArray());
    }

    @Test
    @DisplayName("3. Sorts a reverse-sorted array correctly")
    void testReverseSortedArray() {
        int[] input = {5, 4, 3, 2, 1};
        SortResult result = quickSort.sort(input);

        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result.sortedArray());
    }

    @Test
    @DisplayName("4. Handles duplicate values properly")
    void testDuplicateValues() {
        int[] input = {4, 2, 4, 3, 2, 1, 3};
        SortResult result = quickSort.sort(input);

        assertArrayEquals(new int[]{1, 2, 2, 3, 3, 4, 4}, result.sortedArray());
    }

    @Test
    @DisplayName("5. Handles an empty array gracefully")
    void testEmptyArray() {
        int[] input = {};
        SortResult result = quickSort.sort(input);

        assertArrayEquals(new int[]{}, result.sortedArray());
        assertEquals(0, result.statistics().comparisons());
        assertEquals(0, result.statistics().swaps());
        assertEquals(1, result.events().size());
        assertEquals(SortEvent.Type.COMPLETED, result.events().getFirst().getType());
    }

    @Test
    @DisplayName("6. Handles a single-element array gracefully")
    void testSingleElementArray() {
        int[] input = {88};
        SortResult result = quickSort.sort(input);

        assertArrayEquals(new int[]{88}, result.sortedArray());
        assertEquals(0, result.statistics().comparisons());
        assertEquals(0, result.statistics().swaps());
        assertEquals(SortEvent.Type.COMPLETED, result.events().getFirst().getType());
    }

    @Test
    @DisplayName("7. Handles negative and mixed-sign values")
    void testNegativeAndMixedValues() {
        int[] input = {-15, 23, -2, 0, 7, -40, 18};
        SortResult result = quickSort.sort(input);

        assertArrayEquals(new int[]{-40, -15, -2, 0, 7, 18, 23}, result.sortedArray());
    }

    @Test
    @DisplayName("8. Handles an array where all elements are equal")
    void testAllEqualValues() {
        int[] input = {6, 6, 6, 6, 6};
        SortResult result = quickSort.sort(input);

        assertArrayEquals(new int[]{6, 6, 6, 6, 6}, result.sortedArray());
    }

    @Test
    @DisplayName("9. Confirms caller's input array is not modified")
    void testInputPreservation() {
        int[] input = {7, 2, 9, 4};
        int[] copy = input.clone();
        quickSort.sort(input);

        assertArrayEquals(copy, input, "Original input array must not be modified.");
    }

    @Test
    @DisplayName("10. Emits PARTITION events with correct pivot information and valid replay")
    void testPartitionEventsAndReplay() {
        int[] input = {3, 1, 2};
        SortResult result = quickSort.sort(input);
        List<SortEvent> events = result.events();

        assertFalse(events.isEmpty());
        boolean hasPartition = events.stream().anyMatch(e -> e.getType() == SortEvent.Type.PARTITION);
        assertTrue(hasPartition, "Quick Sort must emit PARTITION events.");

        SortEvent partitionEvent = events.stream()
                .filter(e -> e.getType() == SortEvent.Type.PARTITION)
                .findFirst()
                .orElseThrow();
        assertEquals(2, partitionEvent.getPivotIndex(), "First partition on {3, 1, 2} has pivot index 2.");
        assertEquals(0, partitionEvent.getRangeStart());
        assertEquals(2, partitionEvent.getRangeEnd());

        assertArrayEquals(result.sortedArray(), events.getLast().getSnapshot());

        assertEquals("Quick Sort", quickSort.getName());
        assertEquals("O(n log n)", quickSort.getAverageTimeComplexity());
        assertEquals("O(n²)", quickSort.getWorstTimeComplexity());
    }

    @Test
    @DisplayName("Throws exception on null input")
    void testNullInputThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> quickSort.sort(null));
    }
}
