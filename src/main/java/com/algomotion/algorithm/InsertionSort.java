package com.algomotion.algorithm;

import com.algomotion.model.SortEvent;
import com.algomotion.model.SortResult;
import com.algomotion.model.SortStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * InsertionSort algorithm implementation.
 *
 * Insertion Sort builds the final sorted array one element at a time by consuming
 * one input element per repetition and growing a sorted output list. At each iteration,
 * it removes one element from the input data, finds the location it belongs within
 * the sorted list, and inserts it there.
 *
 * Complexity Analysis:
 * <ul>
 *   <li><b>Best-case Time Complexity:</b> O(n) — Occurs when array is already sorted (1 comparison per element, 0 shifts).</li>
 *   <li><b>Average-case Time Complexity:</b> O(n²).</li>
 *   <li><b>Worst-case Time Complexity:</b> O(n²) — Occurs when array is reverse sorted.</li>
 *   <li><b>Auxiliary Space Complexity:</b> O(1) in-place sorting logic (excluding visualization event trace).</li>
 * </ul>
 */
public class InsertionSort implements SortingAlgorithm {

    @Override
    public String getName() {
        return "Insertion Sort";
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
        return "Iteratively picks the next element as a 'key' and shifts larger sorted elements to the right to insert the key into its correct relative position.";
    }

    @Override
    public SortResult sort(int[] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }

        long startTime = System.nanoTime();
        int[] arr = input.clone();
        int n = arr.length;
        List<SortEvent> events = new ArrayList<>();
        int comparisons = 0;
        int swaps = 0;
        int writes = 0;

        if (n <= 1) {
            events.add(SortEvent.completed(arr, comparisons, swaps, writes, "Array is already sorted."));
            long elapsed = System.nanoTime() - startTime;
            return new SortResult(arr, events, new SortStatistics(comparisons, swaps, writes, events.size()), elapsed);
        }

        for (int i = 1; i < n; i++) {
            int key = arr[i];
            events.add(SortEvent.highlightKey(i, arr, comparisons, swaps, writes,
                    String.format("Selected key index %d (value %d) for insertion into sorted prefix.", i, key)));

            int j = i - 1;
            while (j >= 0) {
                comparisons++;
                events.add(SortEvent.compare(j, j + 1, arr, comparisons, swaps, writes,
                        String.format("Comparing sorted element at index %d (%d) with key (%d).", j, arr[j], key)));

                if (arr[j] > key) {
                    arr[j + 1] = arr[j];
                    writes++;
                    events.add(SortEvent.shift(j, j + 1, arr, comparisons, swaps, writes,
                            String.format("Shifted value %d from index %d to index %d.", arr[j + 1], j, j + 1)));
                    j--;
                } else {
                    break;
                }
            }

            if (j + 1 != i) {
                arr[j + 1] = key;
                writes++;
                events.add(SortEvent.write(j + 1, arr, comparisons, swaps, writes,
                        String.format("Inserted key (%d) at index %d.", key, j + 1)));
            }
        }

        events.add(SortEvent.completed(arr, comparisons, swaps, writes, "Insertion Sort completed."));
        long elapsed = System.nanoTime() - startTime;
        return new SortResult(arr, events, new SortStatistics(comparisons, swaps, writes, events.size()), elapsed);
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

        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
        return arr;
    }
}
