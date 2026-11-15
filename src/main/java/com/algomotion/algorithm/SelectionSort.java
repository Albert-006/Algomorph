package com.algomotion.algorithm;

import com.algomotion.model.SortEvent;
import com.algomotion.model.SortResult;
import com.algomotion.model.SortStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * SelectionSort algorithm implementation.
 *
 * Selection Sort divides the array into a sorted prefix and an unsorted suffix.
 * In each iteration, it finds the minimum element in the unsorted suffix and
 * swaps it with the first unsorted element.
 *
 * Complexity Analysis:
 * <ul>
 *   <li><b>Best-case Time Complexity:</b> O(n²) — It always performs n*(n-1)/2 comparisons regardless of initial ordering.</li>
 *   <li><b>Average-case Time Complexity:</b> O(n²).</li>
 *   <li><b>Worst-case Time Complexity:</b> O(n²).</li>
 *   <li><b>Auxiliary Space Complexity:</b> O(1) in-place sorting logic (excluding visualization event trace).</li>
 * </ul>
 */
public class SelectionSort implements SortingAlgorithm {

    @Override
    public String getName() {
        return "Selection Sort";
    }

    @Override
    public String getBestTimeComplexity() {
        return "O(n²)";
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
        return "Finds the smallest element in the unsorted portion of the array and swaps it with the first unsorted element, growing the sorted portion one by one.";
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

        if (n <= 1) {
            events.add(SortEvent.completed(arr, comparisons, swaps, 0, "Array is already sorted."));
            long elapsed = System.nanoTime() - startTime;
            return new SortResult(arr, events, new SortStatistics(comparisons, swaps, 0, events.size()), elapsed);
        }

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            events.add(SortEvent.selectMin(i, minIndex, arr, comparisons, swaps, 0,
                    String.format("Starting pass %d: Assuming index %d (%d) is the minimum.", i + 1, minIndex, arr[minIndex])));

            for (int j = i + 1; j < n; j++) {
                comparisons++;
                events.add(SortEvent.compare(j, minIndex, arr, comparisons, swaps, 0,
                        String.format("Comparing index %d (%d) with current minimum at index %d (%d).",
                                j, arr[j], minIndex, arr[minIndex])));

                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                    events.add(SortEvent.selectMin(j, minIndex, arr, comparisons, swaps, 0,
                            String.format("Found new minimum: index %d (%d).", minIndex, arr[minIndex])));
                }
            }

            if (minIndex != i) {
                int temp = arr[i];
                arr[i] = arr[minIndex];
                arr[minIndex] = temp;
                swaps++;
                events.add(SortEvent.swap(i, minIndex, arr, comparisons, swaps, 0,
                        String.format("Swapped minimum index %d (%d) into position %d (%d).",
                                minIndex, arr[minIndex], i, arr[i])));
            }
        }

        events.add(SortEvent.completed(arr, comparisons, swaps, 0, "Selection Sort completed."));
        long elapsed = System.nanoTime() - startTime;
        return new SortResult(arr, events, new SortStatistics(comparisons, swaps, 0, events.size()), elapsed);
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
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                int temp = arr[i];
                arr[i] = arr[minIndex];
                arr[minIndex] = temp;
            }
        }
        return arr;
    }
}
