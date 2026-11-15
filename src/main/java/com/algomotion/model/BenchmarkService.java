package com.algomotion.model;

import com.algomotion.algorithm.BubbleSort;
import com.algomotion.algorithm.InsertionSort;
import com.algomotion.algorithm.MergeSort;
import com.algomotion.algorithm.QuickSort;
import com.algomotion.algorithm.SelectionSort;
import com.algomotion.algorithm.SortingAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Service executing rigorous, headless algorithm benchmarking.
 *
 * Implements warmup runs, multi-trial timing with System.nanoTime(),
 * correctness verification, and operation metric counting without the overhead
 * of visualization event generation.
 */
public class BenchmarkService {

    public static final int DEFAULT_WARMUP_TRIALS = 3;
    public static final int DEFAULT_MEASUREMENT_TRIALS = 5;

    public List<BenchmarkResult> runBenchmark(List<SortingAlgorithm> algorithms,
                                              List<Integer> datasetSizes,
                                              DatasetPattern pattern,
                                              Long seed,
                                              int warmupTrials,
                                              int measurementTrials,
                                              Consumer<Double> progressCallback) {
        if (algorithms == null || algorithms.isEmpty()) {
            throw new IllegalArgumentException("Algorithms list must not be empty");
        }
        if (datasetSizes == null || datasetSizes.isEmpty()) {
            throw new IllegalArgumentException("Dataset sizes must not be empty");
        }
        Objects.requireNonNull(pattern, "DatasetPattern must not be null");

        int warmups = Math.max(1, warmupTrials);
        int measurements = Math.max(1, measurementTrials);

        int totalWork = datasetSizes.size() * algorithms.size();
        int completedWork = 0;

        List<BenchmarkResult> results = new ArrayList<>();

        for (int size : datasetSizes) {
            if (size < 0) {
                for (SortingAlgorithm algo : algorithms) {
                    results.add(BenchmarkResult.error(algo, size, pattern, "INVALID_SIZE", "Size must be non-negative: " + size));
                }
                continue;
            }

            // 1. Generate base dataset once per size for identical comparisons
            int[] sharedInput = DatasetGenerator.generate(size, pattern, seed);
            int[] referenceSorted = sharedInput.clone();
            Arrays.sort(referenceSorted);

            for (SortingAlgorithm algo : algorithms) {
                try {
                    // Check extreme quadratic size safety (warn or protect)
                    if (size > 10_000 && isQuadratic(algo)) {
                        results.add(BenchmarkResult.error(algo, size, pattern, "SKIPPED_TIMEOUT_RISK",
                                "Quadratic algorithm skipped on size " + size + " to avoid UI freeze."));
                        completedWork++;
                        if (progressCallback != null) progressCallback.accept((double) completedWork / totalWork);
                        continue;
                    }

                    // 2. JVM Warmup (pure execution)
                    for (int w = 0; w < warmups; w++) {
                        algo.sortPure(sharedInput.clone());
                    }

                    // 3. Timed Trials
                    long[] trialTimesNanos = new long[measurements];
                    boolean sortedCorrectly = true;

                    for (int m = 0; m < measurements; m++) {
                        int[] trialArray = sharedInput.clone();
                        long startTime = System.nanoTime();
                        int[] sortedResult = algo.sortPure(trialArray);
                        long elapsedNanos = System.nanoTime() - startTime;
                        trialTimesNanos[m] = elapsedNanos;

                        if (m == 0) {
                            sortedCorrectly = Arrays.equals(sortedResult, referenceSorted);
                        }
                    }

                    // 4. Operation Metric Counts (using exact lightweight counting)
                    SortStatistics stats = countOperations(algo, sharedInput.clone());

                    BenchmarkResult benchResult = BenchmarkResult.success(
                            algo, size, pattern, trialTimesNanos,
                            stats.comparisons(), stats.swaps(), stats.writes(), sortedCorrectly
                    );
                    results.add(benchResult);

                } catch (Exception e) {
                    results.add(BenchmarkResult.error(algo, size, pattern, "EXECUTION_ERROR", e.getMessage()));
                }

                completedWork++;
                if (progressCallback != null) {
                    progressCallback.accept((double) completedWork / totalWork);
                }
            }
        }

        return results;
    }

    /**
     * Accurately counts algorithm operations on an input array without allocating
     * millions of visualization event snapshots.
     */
    public static SortStatistics countOperations(SortingAlgorithm algo, int[] input) {
        int[] arr = input.clone();
        int n = arr.length;
        if (n <= 1) {
            return new SortStatistics(0, 0, 0, 1);
        }

        if (algo instanceof BubbleSort) {
            int comps = 0, swaps = 0;
            for (int i = 0; i < n - 1; i++) {
                boolean swapped = false;
                for (int j = 0; j < n - 1 - i; j++) {
                    comps++;
                    if (arr[j] > arr[j + 1]) {
                        int t = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = t;
                        swaps++;
                        swapped = true;
                    }
                }
                if (!swapped) break;
            }
            return new SortStatistics(comps, swaps, 0, comps + swaps);
        }

        if (algo instanceof SelectionSort) {
            int comps = 0, swaps = 0;
            for (int i = 0; i < n - 1; i++) {
                int minIdx = i;
                for (int j = i + 1; j < n; j++) {
                    comps++;
                    if (arr[j] < arr[minIdx]) minIdx = j;
                }
                if (minIdx != i) {
                    int t = arr[i]; arr[i] = arr[minIdx]; arr[minIdx] = t;
                    swaps++;
                }
            }
            return new SortStatistics(comps, swaps, 0, comps + swaps);
        }

        if (algo instanceof InsertionSort) {
            int comps = 0, writes = 0;
            for (int i = 1; i < n; i++) {
                int key = arr[i];
                int j = i - 1;
                while (j >= 0) {
                    comps++;
                    if (arr[j] > key) {
                        arr[j + 1] = arr[j];
                        writes++;
                        j--;
                    } else {
                        break;
                    }
                }
                if (j + 1 != i) {
                    arr[j + 1] = key;
                    writes++;
                }
            }
            return new SortStatistics(comps, 0, writes, comps + writes);
        }

        if (algo instanceof MergeSort) {
            int[] counters = new int[3]; // 0=comp, 1=swap, 2=writes
            int[] temp = new int[n];
            countMergeSort(arr, temp, 0, n - 1, counters);
            return new SortStatistics(counters[0], counters[1], counters[2], counters[0] + counters[2]);
        }

        if (algo instanceof QuickSort) {
            int[] counters = new int[3];
            countQuickSort(arr, 0, n - 1, counters);
            return new SortStatistics(counters[0], counters[1], counters[2], counters[0] + counters[1]);
        }

        // Fallback to sort().statistics()
        SortResult res = algo.sort(input);
        return res.statistics();
    }

    private static void countMergeSort(int[] arr, int[] temp, int left, int right, int[] counters) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        countMergeSort(arr, temp, left, mid, counters);
        countMergeSort(arr, temp, mid + 1, right, counters);

        for (int k = left; k <= right; k++) temp[k] = arr[k];
        int i = left, j = mid + 1;
        for (int k = left; k <= right; k++) {
            if (i > mid) {
                arr[k] = temp[j++];
                counters[2]++;
            } else if (j > right) {
                arr[k] = temp[i++];
                counters[2]++;
            } else {
                counters[0]++;
                if (temp[i] <= temp[j]) arr[k] = temp[i++];
                else arr[k] = temp[j++];
                counters[2]++;
            }
        }
    }

    private static void countQuickSort(int[] arr, int low, int high, int[] counters) {
        if (low < high) {
            int p = countPartition(arr, low, high, counters);
            countQuickSort(arr, low, p - 1, counters);
            countQuickSort(arr, p + 1, high, counters);
        }
    }

    private static int countPartition(int[] arr, int low, int high, int[] counters) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            counters[0]++;
            if (arr[j] <= pivot) {
                i++;
                if (i != j) {
                    int t = arr[i]; arr[i] = arr[j]; arr[j] = t;
                    counters[1]++;
                }
            }
        }
        int pi = i + 1;
        if (pi != high) {
            int t = arr[pi]; arr[pi] = arr[high]; arr[high] = t;
            counters[1]++;
        }
        return pi;
    }

    private static boolean isQuadratic(SortingAlgorithm algo) {
        return algo instanceof BubbleSort || algo instanceof SelectionSort || algo instanceof InsertionSort;
    }
}
