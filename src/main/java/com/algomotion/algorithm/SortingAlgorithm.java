package com.algomotion.algorithm;

import com.algomotion.model.SortResult;

/**
 * Common contract for all sorting algorithms in Algomorph.
 *
 * Each algorithm provides descriptive metadata, theoretical time/space complexity,
 * an execution method that produces a deterministic sequence of sorting events
 * for visualization replay, and an optimized pure sort method for benchmarking.
 *
 * All implementations must be completely decoupled from JavaFX and must avoid
 * mutating the caller's input array.
 */
public interface SortingAlgorithm {

    /**
     * @return User-friendly display name (e.g. "Bubble Sort", "Merge Sort").
     */
    String getName();

    /**
     * @return Best-case asymptotic time complexity (e.g. "O(n)").
     */
    String getBestTimeComplexity();

    /**
     * @return Average-case asymptotic time complexity (e.g. "O(n log n)").
     */
    String getAverageTimeComplexity();

    /**
     * @return Worst-case asymptotic time complexity (e.g. "O(n²)").
     */
    String getWorstTimeComplexity();

    /**
     * @return Theoretical auxiliary space complexity of the sorting logic (e.g. "O(1)").
     *         (Note: Excludes event history retained for visual replay).
     */
    String getSpaceComplexity();

    /**
     * @return Concise educational description of how the algorithm works.
     */
    String getDescription();

    /**
     * Sorts the input array in ascending order and records the visual event trace.
     * The input array must NOT be modified.
     *
     * @param input the input array to sort
     * @return SortResult containing the sorted array, event history, metrics, and execution time
     * @throws IllegalArgumentException if input is null
     */
    SortResult sort(int[] input);

    /**
     * Pure sorting execution on a copy of the input array without generating visual event snapshots.
     * Designed for high-speed, memory-efficient benchmarking on large datasets (100 to 5,000+ elements).
     *
     * @param input array to sort (caller array is not modified)
     * @return sorted array copy
     * @throws IllegalArgumentException if input is null
     */
    default int[] sortPure(int[] input) {
        return sort(input).sortedArray();
    }
}
