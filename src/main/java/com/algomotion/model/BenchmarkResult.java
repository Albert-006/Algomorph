package com.algomotion.model;

import com.algomotion.algorithm.SortingAlgorithm;

import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable model holding the statistical timing and operation measurements
 * for an algorithm on a specific dataset size and distribution pattern.
 */
public class BenchmarkResult {

    private final SortingAlgorithm algorithm;
    private final int datasetSize;
    private final DatasetPattern pattern;
    private final double medianTimeMillis;
    private final double minTimeMillis;
    private final double maxTimeMillis;
    private final long[] trialTimesNanos;
    private final int comparisons;
    private final int swaps;
    private final int writes;
    private final boolean sortedCorrectly;
    private final String status;
    private final String errorDetails;

    public BenchmarkResult(SortingAlgorithm algorithm, int datasetSize, DatasetPattern pattern,
                           double medianTimeMillis, double minTimeMillis, double maxTimeMillis,
                           long[] trialTimesNanos, int comparisons, int swaps, int writes,
                           boolean sortedCorrectly, String status, String errorDetails) {
        this.algorithm = Objects.requireNonNull(algorithm, "algorithm must not be null");
        this.datasetSize = datasetSize;
        this.pattern = Objects.requireNonNull(pattern, "pattern must not be null");
        this.medianTimeMillis = medianTimeMillis;
        this.minTimeMillis = minTimeMillis;
        this.maxTimeMillis = maxTimeMillis;
        this.trialTimesNanos = trialTimesNanos != null ? trialTimesNanos.clone() : new long[0];
        this.comparisons = comparisons;
        this.swaps = swaps;
        this.writes = writes;
        this.sortedCorrectly = sortedCorrectly;
        this.status = status != null ? status : "UNKNOWN";
        this.errorDetails = errorDetails != null ? errorDetails : "";
    }

    public static BenchmarkResult success(SortingAlgorithm algorithm, int datasetSize, DatasetPattern pattern,
                                          long[] trialTimesNanos, int comparisons, int swaps, int writes,
                                          boolean sortedCorrectly) {
        long[] sortedNanos = trialTimesNanos.clone();
        Arrays.sort(sortedNanos);

        double medianMs = sortedNanos[sortedNanos.length / 2] / 1_000_000.0;
        double minMs = sortedNanos[0] / 1_000_000.0;
        double maxMs = sortedNanos[sortedNanos.length - 1] / 1_000_000.0;

        return new BenchmarkResult(algorithm, datasetSize, pattern, medianMs, minMs, maxMs,
                trialTimesNanos, comparisons, swaps, writes, sortedCorrectly,
                sortedCorrectly ? "SUCCESS" : "CORRUPTION_ERROR", "");
    }

    public static BenchmarkResult error(SortingAlgorithm algorithm, int datasetSize, DatasetPattern pattern,
                                        String status, String details) {
        return new BenchmarkResult(algorithm, datasetSize, pattern, -1.0, -1.0, -1.0,
                new long[0], 0, 0, 0, false, status, details);
    }

    public SortingAlgorithm getAlgorithm() {
        return algorithm;
    }

    public String getAlgorithmName() {
        return algorithm.getName();
    }

    public int getDatasetSize() {
        return datasetSize;
    }

    public DatasetPattern getPattern() {
        return pattern;
    }

    public double getMedianTimeMillis() {
        return medianTimeMillis;
    }

    public double getMinTimeMillis() {
        return minTimeMillis;
    }

    public double getMaxTimeMillis() {
        return maxTimeMillis;
    }

    public long[] getTrialTimesNanos() {
        return trialTimesNanos.clone();
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

    public boolean isSortedCorrectly() {
        return sortedCorrectly;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    @Override
    public String toString() {
        return String.format("BenchmarkResult[%s, N=%d, %s, median=%.3f ms, comps=%d, swaps=%d, writes=%d, status=%s]",
                algorithm.getName(), datasetSize, pattern, medianTimeMillis, comparisons, swaps, writes, status);
    }
}
