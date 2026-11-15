package com.algomotion.model;

import java.util.List;

/**
 * Encapsulates the complete result of a sorting algorithm execution,
 * including the final sorted array, the ordered history of sorting events,
 * aggregate performance statistics, and elapsed computation time.
 */
public class SortResult {

    private final int[] sortedArray;
    private final List<SortEvent> events;
    private final SortStatistics statistics;
    private final long executionTimeNanos;

    public SortResult(int[] sortedArray, List<SortEvent> events, SortStatistics statistics, long executionTimeNanos) {
        this.sortedArray = sortedArray != null ? sortedArray.clone() : new int[0];
        this.events = events != null ? List.copyOf(events) : List.of();
        this.statistics = statistics != null ? statistics : SortStatistics.empty();
        this.executionTimeNanos = executionTimeNanos;
    }

    public SortResult(int[] sortedArray, List<SortEvent> events, SortStatistics statistics) {
        this(sortedArray, events, statistics, 0L);
    }

    public int[] sortedArray() {
        return sortedArray.clone();
    }

    public List<SortEvent> events() {
        return events;
    }

    public SortStatistics statistics() {
        return statistics;
    }

    public long executionTimeNanos() {
        return executionTimeNanos;
    }

    public double getExecutionTimeMillis() {
        return executionTimeNanos / 1_000_000.0;
    }

    public int[] getSortedArray() {
        return sortedArray();
    }

    public List<SortEvent> getEvents() {
        return events;
    }

    public SortStatistics getStatistics() {
        return statistics;
    }

    public long getExecutionTimeNanos() {
        return executionTimeNanos;
    }
}
