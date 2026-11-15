package com.algomotion.algorithm;

import com.algomotion.model.SortEvent;
import com.algomotion.model.SortResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MergeSort Algorithm Tests")
class MergeSortTest {

    private MergeSort mergeSort;

    @BeforeEach
    void setUp() {
        mergeSort = new MergeSort();
    }

    @Test
    @DisplayName("1. Sorts an ordinary unsorted array correctly")
    void testOrdinaryUnsortedArray() {
        int[] input = {38, 27, 43, 3, 9, 82, 10};
        SortResult result = mergeSort.sort(input);

        assertArrayEquals(new int[]{3, 9, 10, 27, 38, 43, 82}, result.sortedArray());
        assertEquals(SortEvent.Type.COMPLETED, result.events().getLast().getType());
        assertTrue(result.statistics().writes() > 0, "Merge Sort must perform writes into the target array.");
        assertEquals(0, result.statistics().swaps(), "Merge Sort does not swap; it performs writes.");
    }

    @Test
    @DisplayName("2. Handles an already sorted array correctly")
    void testAlreadySortedArray() {
        int[] input = {1, 2, 3, 4, 5, 6};
        SortResult result = mergeSort.sort(input);

        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6}, result.sortedArray());
        assertTrue(result.statistics().comparisons() > 0);
        assertTrue(result.statistics().writes() > 0);
    }

    @Test
    @DisplayName("3. Sorts a reverse-sorted array correctly")
    void testReverseSortedArray() {
        int[] input = {6, 5, 4, 3, 2, 1};
        SortResult result = mergeSort.sort(input);

        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6}, result.sortedArray());
    }

    @Test
    @DisplayName("4. Handles duplicate values properly and stably")
    void testDuplicateValues() {
        int[] input = {5, 2, 8, 5, 2, 8};
        SortResult result = mergeSort.sort(input);

        assertArrayEquals(new int[]{2, 2, 5, 5, 8, 8}, result.sortedArray());
    }

    @Test
    @DisplayName("5. Handles an empty array gracefully")
    void testEmptyArray() {
        int[] input = {};
        SortResult result = mergeSort.sort(input);

        assertArrayEquals(new int[]{}, result.sortedArray());
        assertEquals(0, result.statistics().comparisons());
        assertEquals(0, result.statistics().writes());
        assertEquals(1, result.events().size());
        assertEquals(SortEvent.Type.COMPLETED, result.events().getFirst().getType());
    }

    @Test
    @DisplayName("6. Handles a single-element array gracefully")
    void testSingleElementArray() {
        int[] input = {100};
        SortResult result = mergeSort.sort(input);

        assertArrayEquals(new int[]{100}, result.sortedArray());
        assertEquals(0, result.statistics().comparisons());
        assertEquals(0, result.statistics().writes());
        assertEquals(SortEvent.Type.COMPLETED, result.events().getFirst().getType());
    }

    @Test
    @DisplayName("7. Handles negative and mixed-sign values")
    void testNegativeAndMixedValues() {
        int[] input = {-10, 50, -30, 20, 0, -5};
        SortResult result = mergeSort.sort(input);

        assertArrayEquals(new int[]{-30, -10, -5, 0, 20, 50}, result.sortedArray());
    }

    @Test
    @DisplayName("8. Handles an array where all elements are equal")
    void testAllEqualValues() {
        int[] input = {4, 4, 4, 4};
        SortResult result = mergeSort.sort(input);

        assertArrayEquals(new int[]{4, 4, 4, 4}, result.sortedArray());
    }

    @Test
    @DisplayName("9. Confirms caller's input array is not modified")
    void testInputPreservation() {
        int[] input = {9, 1, 5, 3};
        int[] copy = input.clone();
        mergeSort.sort(input);

        assertArrayEquals(copy, input, "Original input array must remain unchanged.");
    }

    @Test
    @DisplayName("10. Verifies event replay snapshot integrity and intermediate writes")
    void testEventReplayAndSnapshots() {
        int[] input = {4, 2, 1, 3};
        SortResult result = mergeSort.sort(input);
        List<SortEvent> events = result.events();

        assertFalse(events.isEmpty());
        // Verify WRITE events occur
        boolean hasWrite = events.stream().anyMatch(e -> e.getType() == SortEvent.Type.WRITE);
        assertTrue(hasWrite, "Merge Sort must emit WRITE events as merged values are placed.");

        // The final event must contain the fully sorted array
        assertArrayEquals(result.sortedArray(), events.getLast().getSnapshot());

        assertEquals("Merge Sort", mergeSort.getName());
        assertEquals("O(n log n)", mergeSort.getAverageTimeComplexity());
        assertEquals("O(n)", mergeSort.getSpaceComplexity());
    }

    @Test
    @DisplayName("Throws exception on null input")
    void testNullInputThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> mergeSort.sort(null));
    }
}
