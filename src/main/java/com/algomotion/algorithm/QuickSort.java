package com.algomotion.algorithm;

import com.algomotion.model.SortEvent;
import com.algomotion.model.SortResult;
import com.algomotion.model.SortStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * QuickSort algorithm implementation using Lomuto partitioning.
 *
 * Quick Sort is an efficient, comparison-based divide-and-conquer algorithm.
 * It selects a 'pivot' element from the array and partitions the other elements
 * into two sub-arrays according to whether they are less than or greater than the pivot.
 * The sub-arrays are then recursively sorted.
 *
 * Pivot Strategy:
 * <ul>
 *   <li>The last element of the active subarray (arr[high]) is selected as the pivot.</li>
 *   <li>Elements smaller than or equal to the pivot are partitioned to the left; larger to the right.</li>
 * </ul>
 *
 * Complexity Analysis:
 * <ul>
 *   <li><b>Best-case Time Complexity:</b> O(n log n) — Occurs when partitions are evenly balanced at each level.</li>
 *   <li><b>Average-case Time Complexity:</b> O(n log n).</li>
 *   <li><b>Worst-case Time Complexity:</b> O(n²) — Occurs when the subarray is already sorted or reverse sorted with an endpoint pivot.</li>
 *   <li><b>Auxiliary Space Complexity:</b> O(log n) average recursion stack space, O(n) worst-case recursion stack space (excluding visualization event trace).</li>
 * </ul>
 */
public class QuickSort implements SortingAlgorithm {

    @Override
    public String getName() {
        return "Quick Sort";
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
        return "O(n²)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(log n) stack";
    }

    @Override
    public String getDescription() {
        return "Divides the array around a pivot element so smaller elements move left and larger elements move right, then recursively sorts each partition.";
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

        quickSort(arr, 0, n - 1, events, counters);

        events.add(SortEvent.completed(arr, counters[0], counters[1], counters[2], "Quick Sort completed."));
        long elapsed = System.nanoTime() - startTime;
        return new SortResult(arr, events, new SortStatistics(counters[0], counters[1], counters[2], events.size()), elapsed);
    }

    private void quickSort(int[] arr, int low, int high, List<SortEvent> events, int[] counters) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high, events, counters);
            quickSort(arr, low, pivotIndex - 1, events, counters);
            quickSort(arr, pivotIndex + 1, high, events, counters);
        }
    }

    private int partition(int[] arr, int low, int high, List<SortEvent> events, int[] counters) {
        int pivotValue = arr[high];

        // Emit PARTITION event highlighting the chosen pivot and the partition boundaries [low..high]
        events.add(SortEvent.partition(high, low, high, arr, counters[0], counters[1], counters[2],
                String.format("Partitioning range [%d..%d]: Chosen pivot at index %d (value %d).",
                        low, high, high, pivotValue)));

        int i = low - 1;

        for (int j = low; j < high; j++) {
            counters[0]++; // comparisons++
            events.add(SortEvent.compare(j, high, arr, counters[0], counters[1], counters[2],
                    String.format("Range [%d..%d]: Comparing index %d (%d) with pivot (%d).",
                            low, high, j, arr[j], pivotValue)));

            if (arr[j] <= pivotValue) {
                i++;
                if (i != j) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                    counters[1]++; // swaps++
                    events.add(SortEvent.swap(i, j, arr, counters[0], counters[1], counters[2],
                            String.format("Swapped index %d (%d) and index %d (%d) to partition smaller element left.",
                                    i, arr[i], j, arr[j])));
                }
            }
        }

        // Place pivot into its correct final sorted position
        int finalPivotIndex = i + 1;
        if (finalPivotIndex != high) {
            int temp = arr[finalPivotIndex];
            arr[finalPivotIndex] = arr[high];
            arr[high] = temp;
            counters[1]++; // swaps++
            events.add(SortEvent.swap(finalPivotIndex, high, arr, counters[0], counters[1], counters[2],
                    String.format("Placed pivot (%d) into sorted position at index %d.",
                            arr[finalPivotIndex], finalPivotIndex)));
        }

        return finalPivotIndex;
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

        pureQuickSort(arr, 0, n - 1);
        return arr;
    }

    private void pureQuickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = purePartition(arr, low, high);
            pureQuickSort(arr, low, pivotIndex - 1);
            pureQuickSort(arr, pivotIndex + 1, high);
        }
    }

    private int purePartition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                if (i != j) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }

        int finalPivotIndex = i + 1;
        if (finalPivotIndex != high) {
            int temp = arr[finalPivotIndex];
            arr[finalPivotIndex] = arr[high];
            arr[high] = temp;
        }
        return finalPivotIndex;
    }
}
