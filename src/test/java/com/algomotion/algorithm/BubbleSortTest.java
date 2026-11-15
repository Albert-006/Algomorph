package com.algomotion.algorithm;

import com.algomotion.model.SortEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BubbleSort Algorithm Tests")
class BubbleSortTest {

    private BubbleSort bubbleSort;

    @BeforeEach
    void setUp() {
        bubbleSort = new BubbleSort();
    }

    @Test
    @DisplayName("1. Sorts an ordinary unsorted array correctly")
    void testOrdinaryUnsortedArray() {
        int[] input = {64, 34, 25, 12, 22, 11, 90};
        BubbleSort.Result result = bubbleSort.sort(input);

        int[] expected = {11, 12, 22, 25, 34, 64, 90};
        assertArrayEquals(expected, result.sortedArray(), "The output array must be sorted in ascending order.");
        assertFalse(result.events().isEmpty(), "Events should be generated.");
        assertEquals(SortEvent.Type.COMPLETED, result.events().getLast().getType(), "Last event must be COMPLETED.");
    }

    @Test
    @DisplayName("2. Handles an already sorted array with early-exit optimization")
    void testAlreadySortedArray() {
        int[] input = {10, 20, 30, 40, 50};
        BubbleSort.Result result = bubbleSort.sort(input);

        assertArrayEquals(new int[]{10, 20, 30, 40, 50}, result.sortedArray());
        assertEquals(0, result.statistics().swaps(), "Already sorted array must require 0 swaps.");
        // With n=5, first pass does 4 comparisons, discovers no swaps, and terminates immediately.
        assertEquals(4, result.statistics().comparisons(), "Should perform only n-1 comparisons due to early-exit.");
    }

    @Test
    @DisplayName("3. Sorts an array in descending (reverse) order")
    void testDescendingArray() {
        int[] input = {5, 4, 3, 2, 1};
        BubbleSort.Result result = bubbleSort.sort(input);

        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result.sortedArray());
        // For reverse array of length 5:
        // comparisons: 4 + 3 + 2 + 1 = 10
        // swaps: 4 + 3 + 2 + 1 = 10
        assertEquals(10, result.statistics().comparisons(), "Worst-case comparisons for n=5 should be 10.");
        assertEquals(10, result.statistics().swaps(), "Worst-case swaps for n=5 should be 10.");
    }

    @Test
    @DisplayName("4. Handles duplicate values correctly and stably")
    void testDuplicateValues() {
        int[] input = {5, 1, 3, 5, 2, 1, 3};
        BubbleSort.Result result = bubbleSort.sort(input);

        int[] expected = {1, 1, 2, 3, 3, 5, 5};
        assertArrayEquals(expected, result.sortedArray(), "Array with duplicates must be sorted properly.");
    }

    @Test
    @DisplayName("5. Handles an empty array gracefully")
    void testEmptyArray() {
        int[] input = {};
        BubbleSort.Result result = bubbleSort.sort(input);

        assertArrayEquals(new int[]{}, result.sortedArray());
        assertEquals(0, result.statistics().comparisons());
        assertEquals(0, result.statistics().swaps());
        assertEquals(1, result.events().size(), "Should have a single COMPLETED event.");
        assertEquals(SortEvent.Type.COMPLETED, result.events().getFirst().getType());
    }

    @Test
    @DisplayName("6. Handles a single-element array gracefully")
    void testSingleElementArray() {
        int[] input = {42};
        BubbleSort.Result result = bubbleSort.sort(input);

        assertArrayEquals(new int[]{42}, result.sortedArray());
        assertEquals(0, result.statistics().comparisons());
        assertEquals(0, result.statistics().swaps());
        assertEquals(1, result.events().size());
        assertEquals(SortEvent.Type.COMPLETED, result.events().getFirst().getType());
    }

    @Test
    @DisplayName("7. Handles negative values and mixed signs correctly")
    void testNegativeValues() {
        int[] input = {-5, 12, -20, 0, 8, -1};
        BubbleSort.Result result = bubbleSort.sort(input);

        int[] expected = {-20, -5, -1, 0, 8, 12};
        assertArrayEquals(expected, result.sortedArray(), "Negative values should be sorted in ascending order.");
    }

    @Test
    @DisplayName("8. Tracks accurate comparison and swap counts throughout execution")
    void testComparisonAndSwapCounts() {
        int[] input = {3, 1, 2};
        // Pass 1:
        // compare(0,1): 3 > 1 -> swap(0,1) -> [1, 3, 2]
        // compare(1,2): 3 > 2 -> swap(1,2) -> [1, 2, 3]
        // Pass 2:
        // compare(0,1): 1 > 2 (false, no swap)
        // Pass 2 ends, early termination!
        // Total comparisons: 3. Total swaps: 2.
        BubbleSort.Result result = bubbleSort.sort(input);

        assertEquals(3, result.statistics().comparisons(), "Total comparisons should match exact step trace.");
        assertEquals(2, result.statistics().swaps(), "Total swaps should match exact step trace.");
    }

    @Test
    @DisplayName("9. Generates correct event ordering and valid array snapshots")
    void testEventOrderingAndSnapshots() {
        int[] input = {2, 1};
        BubbleSort.Result result = bubbleSort.sort(input);
        List<SortEvent> events = result.events();

        // Expect: COMPARE(0,1) -> SWAP(0,1) -> COMPLETED
        assertEquals(3, events.size(), "Should have 3 events: COMPARE, SWAP, COMPLETED.");

        SortEvent event0 = events.get(0);
        assertEquals(SortEvent.Type.COMPARE, event0.getType());
        assertEquals(0, event0.getFirstIndex());
        assertEquals(1, event0.getSecondIndex());
        assertArrayEquals(new int[]{2, 1}, event0.getSnapshot());

        SortEvent event1 = events.get(1);
        assertEquals(SortEvent.Type.SWAP, event1.getType());
        assertEquals(0, event1.getFirstIndex());
        assertEquals(1, event1.getSecondIndex());
        assertArrayEquals(new int[]{1, 2}, event1.getSnapshot());

        SortEvent event2 = events.get(2);
        assertEquals(SortEvent.Type.COMPLETED, event2.getType());
        assertArrayEquals(new int[]{1, 2}, event2.getSnapshot());
    }

    @Test
    @DisplayName("10. Ensures caller-owned input array is never modified")
    void testCallerInputNotModified() {
        int[] original = {50, 40, 30, 20, 10};
        int[] copy = original.clone();

        bubbleSort.sort(original);

        assertArrayEquals(copy, original, "The caller's original array must not be modified by the sort method.");
    }

    @Test
    @DisplayName("Throws exception on null input")
    void testNullInputThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> bubbleSort.sort(null));
    }
}
