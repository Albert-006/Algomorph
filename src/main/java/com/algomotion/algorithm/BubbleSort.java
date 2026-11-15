package com.algomotion.algorithm;

import com.algomotion.model.SortEvent;
import com.algomotion.model.SortResult;
import com.algomotion.model.SortStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * BubbleSort algorithm implementation designed for interactive visualization.
 *
 * Bubble Sort repeatedly steps through the list, compares adjacent elements,
 * and swaps them if they are in the wrong order. The pass through the list is
 * repeated until no swaps are needed, which indicates that the list is sorted.
 *
 * Complexity Analysis:
 * <ul>
 *   <li><b>Worst-case Time Complexity:</b> O(n²) — Occurs when the array is reverse sorted.
 *       Every element must be compared against all unsorted elements, taking n*(n-1)/2 comparisons.</li>
 *   <li><b>Best-case Time Complexity:</b> O(n) — Occurs when the array is already sorted.
 *       The early-exit flag detects that 0 swaps occurred in the first pass and finishes immediately.</li>
 *   <li><b>Average-case Time Complexity:</b> O(n²).</li>
 *   <li><b>Auxiliary Space Complexity:</b> O(1) for the fundamental in-place sorting logic.
 *       (Note: Storing the event history for visualization replay requires O(n²) space).</li>
 * </ul>
 *
 * This class is completely decoupled from JavaFX.
 */
public class BubbleSort implements SortingAlgorithm {

    /**
     * Immutable container for the sorting execution results.
     * Inherits from {@link SortResult} to maintain backward compatibility.
     */
    public static class Result extends SortResult {
        public Result(int[] sortedArray, List<SortEvent> events, SortStatistics statistics, long executionTimeNanos) {
            super(sortedArray, events, statistics, executionTimeNanos);
        }

        public Result(int[] sortedArray, List<SortEvent> events, SortStatistics statistics) {
            super(sortedArray, events, statistics, 0L);
        }
    }

    @Override
    public String getName() {
        return "Bubble Sort";
    }

    @Override
    public String getBestTimeComplexity() {
        return "O(n)";
    }

    @Override
    public String getAverageTimeComplexity() {
        return "O(n²)";
    }

    @Override
    public String getWorstTimeComplexity() {
        return "O(n²)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(1)";
    }

    @Override
    public String getDescription() {
        return "Repeatedly steps through the list, compares adjacent elements, and swaps them if they are in the wrong order. Includes an early-exit optimization.";
    }

    @Override
    public Result sort(int[] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }

        long startTime = System.nanoTime();
        int[] arr = input.clone();
        int n = arr.length;
        List<SortEvent> events = new ArrayList<>();
        int comparisons = 0;
        int swaps = 0;

        // Edge case: Empty or single-element array is trivially sorted
        if (n <= 1) {
            events.add(SortEvent.completed(arr, comparisons, swaps, 0, "Array is already sorted."));
            long elapsed = System.nanoTime() - startTime;
            return new Result(arr, events, new SortStatistics(comparisons, swaps, 0, events.size()), elapsed);
        }

        for (int i = 0; i < n - 1; i++) {
            boolean swappedInPass = false;

            for (int j = 0; j < n - 1 - i; j++) {
                comparisons++;
                String compDesc = String.format("Comparing index %d (%d) and index %d (%d)",
                        j, arr[j], j + 1, arr[j + 1]);
                events.add(SortEvent.compare(j, j + 1, arr, comparisons, swaps, 0, compDesc));

                if (arr[j] > arr[j + 1]) {
                    // Perform swap
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swaps++;
                    swappedInPass = true;

                    String swapDesc = String.format("Swapped index %d (%d) and index %d (%d)",
                            j, arr[j], j + 1, arr[j + 1]);
                    events.add(SortEvent.swap(j, j + 1, arr, comparisons, swaps, 0, swapDesc));
                }
            }

            // Early exit optimization: If no swaps occurred in this pass, the array is sorted
            if (!swappedInPass) {
                break;
            }
        }

        events.add(SortEvent.completed(arr, comparisons, swaps, 0, "Sorting completed."));
        long elapsed = System.nanoTime() - startTime;
        return new Result(arr, events, new SortStatistics(comparisons, swaps, 0, events.size()), elapsed);
    }

    @Override
    public int[] sortPure(int[] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }
        int[] arr = input.clone();
        int n = arr.length;
        if (n <= 1) {
            return arr;
        }

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return arr;
    }
}
