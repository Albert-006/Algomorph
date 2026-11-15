package com.algomotion.model;

/**
 * Encapsulates aggregate statistics for an algorithm execution run,
 * such as comparison count, swap count, write count, and the number of generated events.
 */
public record SortStatistics(int comparisons, int swaps, int writes, int totalEvents) {

    public SortStatistics(int comparisons, int swaps, int totalEvents) {
        this(comparisons, swaps, 0, totalEvents);
    }

    public static SortStatistics empty() {
        return new SortStatistics(0, 0, 0, 0);
    }

    public int getComparisons() {
        return comparisons;
    }

    public int getSwaps() {
        return swaps;
    }

    public int getWrites() {
        return writes;
    }

    public int getTotalEvents() {
        return totalEvents;
    }
}
