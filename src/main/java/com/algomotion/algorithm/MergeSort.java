package com.algomotion.algorithm;

import com.algomotion.model.SortEvent;
import com.algomotion.model.SortResult;
import com.algomotion.model.SortStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * MergeSort algorithm implementation.
 *
 * Merge Sort is an efficient, stable, divide-and-conquer comparison-based sorting algorithm.
 * It divides the array into two halves, recursively sorts them, and then merges the two
 * sorted halves into a single sorted array.
 *
 * Complexity Analysis:
 * <ul>
 *   <li><b>Best-case Time Complexity:</b> O(n log n).</li>
 *   <li><b>Average-case Time Complexity:</b> O(n log n).</li>
 *   <li><b>Worst-case Time Complexity:</b> O(n log n).</li>
 *   <li><b>Auxiliary Space Complexity:</b> O(n) for the temporary merge buffer (excluding visual event trace).</li>
 * </ul>
 */
public class MergeSort implements SortingAlgorithm {

    @Override
    public String getName() {
        return "Merge Sort";
    }

    @Override
    public String getBestTimeComplexity() {
        return "O(n log n)";
    }

    @Override
    public String getAverageTimeComplexity() {
        return "O(n log n)";
    }

    @Override
    public String getWorstTimeComplexity() {
        return "O(n log n)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(n)";
    }

    @Override
    public String getDescription() {
        return "Divide-and-conquer algorithm that recursively splits the array into halves, sorts them, and merges the sorted subarrays using an auxiliary buffer.";
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
        // counters: [0] = comparisons, [1] = swaps, [2] = writes
        int[] counters = new int[3];

        if (n <= 1) {
            events.add(SortEvent.completed(arr, 0, 0, 0, "Array is already sorted."));
            long elapsed = System.nanoTime() - startTime;
            return new SortResult(arr, events, new SortStatistics(0, 0, 0, events.size()), elapsed);
        }

        int[] temp = new int[n];
        mergeSort(arr, temp, 0, n - 1, events, counters);

        events.add(SortEvent.completed(arr, counters[0], counters[1], counters[2], "Merge Sort completed."));
        long elapsed = System.nanoTime() - startTime;
        return new SortResult(arr, events, new SortStatistics(counters[0], counters[1], counters[2], events.size()), elapsed);
    }

    private void mergeSort(int[] arr, int[] temp, int left, int right,
                           List<SortEvent> events, int[] counters) {
        if (left >= right) {
            return;
        }

        int mid = left + (right - left) / 2;
        mergeSort(arr, temp, left, mid, events, counters);
        mergeSort(arr, temp, mid + 1, right, events, counters);
        merge(arr, temp, left, mid, right, events, counters);
    }

    private void merge(int[] arr, int[] temp, int left, int mid, int right,
                       List<SortEvent> events, int[] counters) {
        // Copy subarray into auxiliary temporary buffer
        for (int k = left; k <= right; k++) {
            temp[k] = arr[k];
        }

        int i = left;
        int j = mid + 1;

        for (int k = left; k <= right; k++) {
            if (i > mid) {
                // Left half exhausted; pull from right
                arr[k] = temp[j++];
                counters[2]++; // writes++
                events.add(SortEvent.write(k, arr, counters[0], counters[1], counters[2],
                        String.format("Subarray [%d..%d]: Placed right element %d into index %d.", left, right, arr[k], k)));
            } else if (j > right) {
                // Right half exhausted; pull from left
                arr[k] = temp[i++];
                counters[2]++; // writes++
                events.add(SortEvent.write(k, arr, counters[0], counters[1], counters[2],
                        String.format("Subarray [%d..%d]: Placed left element %d into index %d.", left, right, arr[k], k)));
            } else {
                // Both halves have elements: compare heads
                counters[0]++; // comparisons++
                events.add(SortEvent.compare(i, j, arr, counters[0], counters[1], counters[2],
                        String.format("Subarray [%d..%d]: Comparing left index %d (%d) with right index %d (%d).",
                                left, right, i, temp[i], j, temp[j])));

                if (temp[i] <= temp[j]) {
                    arr[k] = temp[i++];
                } else {
                    arr[k] = temp[j++];
                }
                counters[2]++; // writes++
                events.add(SortEvent.write(k, arr, counters[0], counters[1], counters[2],
                        String.format("Subarray [%d..%d]: Merged smaller value %d into index %d.", left, right, arr[k], k)));
            }
        }
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

        int[] temp = new int[n];
        pureMergeSort(arr, temp, 0, n - 1);
        return arr;
    }

    private void pureMergeSort(int[] arr, int[] temp, int left, int right) {
        if (left >= right) {
            return;
        }

        int mid = left + (right - left) / 2;
        pureMergeSort(arr, temp, left, mid);
        pureMergeSort(arr, temp, mid + 1, right);

        for (int k = left; k <= right; k++) {
            temp[k] = arr[k];
        }

        int i = left;
        int j = mid + 1;
        for (int k = left; k <= right; k++) {
            if (i > mid) {
                arr[k] = temp[j++];
            } else if (j > right) {
                arr[k] = temp[i++];
            } else if (temp[i] <= temp[j]) {
                arr[k] = temp[i++];
            } else {
                arr[k] = temp[j++];
            }
        }
    }
}
