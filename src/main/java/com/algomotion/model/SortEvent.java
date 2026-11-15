package com.algomotion.model;

import java.util.Objects;

/**
 * Represents a single discrete sorting event occurring during algorithm execution.
 * Each event captures the state of the array at that exact instant, providing
 * immutability and defensive copying for deterministic visualization and replay.
 */
public final class SortEvent {

    public enum Type {
        COMPARE,
        SWAP,
        SELECT_MIN,
        HIGHLIGHT_KEY,
        SHIFT,
        WRITE,
        PARTITION,
        COMPLETED
    }

    private final Type type;
    private final int firstIndex;
    private final int secondIndex;
    private final int pivotIndex;
    private final int rangeStart;
    private final int rangeEnd;
    private final int[] snapshot;
    private final int comparisonsSoFar;
    private final int swapsSoFar;
    private final int writesSoFar;
    private final String description;

    public SortEvent(Type type, int firstIndex, int secondIndex, int pivotIndex,
                     int rangeStart, int rangeEnd, int[] snapshot,
                     int comparisonsSoFar, int swapsSoFar, int writesSoFar,
                     String description) {
        this.type = Objects.requireNonNull(type, "Event type must not be null");
        this.firstIndex = firstIndex;
        this.secondIndex = secondIndex;
        this.pivotIndex = pivotIndex;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.snapshot = snapshot != null ? snapshot.clone() : new int[0];
        this.comparisonsSoFar = comparisonsSoFar;
        this.swapsSoFar = swapsSoFar;
        this.writesSoFar = writesSoFar;
        this.description = description != null ? description : "";
    }

    public SortEvent(Type type, int firstIndex, int secondIndex, int[] snapshot,
                     int comparisonsSoFar, int swapsSoFar, String description) {
        this(type, firstIndex, secondIndex, -1, -1, -1, snapshot, comparisonsSoFar, swapsSoFar, 0, description);
    }

    // --- Factory Methods ---

    public static SortEvent compare(int firstIndex, int secondIndex, int[] snapshot,
                                    int comparisonsSoFar, int swapsSoFar, String description) {
        return new SortEvent(Type.COMPARE, firstIndex, secondIndex, -1, -1, -1,
                snapshot, comparisonsSoFar, swapsSoFar, 0, description);
    }

    public static SortEvent compare(int firstIndex, int secondIndex, int[] snapshot,
                                    int comparisonsSoFar, int swapsSoFar, int writesSoFar,
                                    String description) {
        return new SortEvent(Type.COMPARE, firstIndex, secondIndex, -1, -1, -1,
                snapshot, comparisonsSoFar, swapsSoFar, writesSoFar, description);
    }

    public static SortEvent swap(int firstIndex, int secondIndex, int[] snapshot,
                                 int comparisonsSoFar, int swapsSoFar, String description) {
        return new SortEvent(Type.SWAP, firstIndex, secondIndex, -1, -1, -1,
                snapshot, comparisonsSoFar, swapsSoFar, 0, description);
    }

    public static SortEvent swap(int firstIndex, int secondIndex, int[] snapshot,
                                 int comparisonsSoFar, int swapsSoFar, int writesSoFar,
                                 String description) {
        return new SortEvent(Type.SWAP, firstIndex, secondIndex, -1, -1, -1,
                snapshot, comparisonsSoFar, swapsSoFar, writesSoFar, description);
    }

    public static SortEvent selectMin(int scanIndex, int minIndex, int[] snapshot,
                                      int comparisonsSoFar, int swapsSoFar, int writesSoFar,
                                      String description) {
        return new SortEvent(Type.SELECT_MIN, scanIndex, minIndex, -1, -1, -1,
                snapshot, comparisonsSoFar, swapsSoFar, writesSoFar, description);
    }

    public static SortEvent highlightKey(int keyIndex, int[] snapshot,
                                         int comparisonsSoFar, int swapsSoFar, int writesSoFar,
                                         String description) {
        return new SortEvent(Type.HIGHLIGHT_KEY, keyIndex, -1, -1, -1, -1,
                snapshot, comparisonsSoFar, swapsSoFar, writesSoFar, description);
    }

    public static SortEvent shift(int fromIndex, int toIndex, int[] snapshot,
                                  int comparisonsSoFar, int swapsSoFar, int writesSoFar,
                                  String description) {
        return new SortEvent(Type.SHIFT, fromIndex, toIndex, -1, -1, -1,
                snapshot, comparisonsSoFar, swapsSoFar, writesSoFar, description);
    }

    public static SortEvent write(int targetIndex, int[] snapshot,
                                  int comparisonsSoFar, int swapsSoFar, int writesSoFar,
                                  String description) {
        return new SortEvent(Type.WRITE, targetIndex, -1, -1, -1, -1,
                snapshot, comparisonsSoFar, swapsSoFar, writesSoFar, description);
    }

    public static SortEvent partition(int pivotIndex, int rangeStart, int rangeEnd, int[] snapshot,
                                      int comparisonsSoFar, int swapsSoFar, int writesSoFar,
                                      String description) {
        return new SortEvent(Type.PARTITION, -1, -1, pivotIndex, rangeStart, rangeEnd,
                snapshot, comparisonsSoFar, swapsSoFar, writesSoFar, description);
    }

    public static SortEvent completed(int[] snapshot, int comparisonsSoFar, int swapsSoFar,
                                      String description) {
        return new SortEvent(Type.COMPLETED, -1, -1, -1, -1, -1,
                snapshot, comparisonsSoFar, swapsSoFar, 0, description);
    }

    public static SortEvent completed(int[] snapshot, int comparisonsSoFar, int swapsSoFar,
                                      int writesSoFar, String description) {
        return new SortEvent(Type.COMPLETED, -1, -1, -1, -1, -1,
                snapshot, comparisonsSoFar, swapsSoFar, writesSoFar, description);
    }

    // --- Getters ---

    public Type getType() {
        return type;
    }

    public int getFirstIndex() {
        return firstIndex;
    }

    public int getSecondIndex() {
        return secondIndex;
    }

    public int getPivotIndex() {
        return pivotIndex;
    }

    public int getRangeStart() {
        return rangeStart;
    }

    public int getRangeEnd() {
        return rangeEnd;
    }

    /**
     * Returns a defensive copy of the array snapshot at the moment this event occurred.
     */
    public int[] getSnapshot() {
        return snapshot.clone();
    }

    public int getComparisonsSoFar() {
        return comparisonsSoFar;
    }

    public int getSwapsSoFar() {
        return swapsSoFar;
    }

    public int getWritesSoFar() {
        return writesSoFar;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("SortEvent[type=%s, indices=(%d, %d), pivot=%d, range=[%d, %d], comparisons=%d, swaps=%d, writes=%d, desc='%s']",
                type, firstIndex, secondIndex, pivotIndex, rangeStart, rangeEnd, comparisonsSoFar, swapsSoFar, writesSoFar, description);
    }
}
