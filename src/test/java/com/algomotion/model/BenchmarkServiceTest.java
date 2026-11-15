package com.algomotion.model;

import com.algomotion.algorithm.BubbleSort;
import com.algomotion.algorithm.InsertionSort;
import com.algomotion.algorithm.MergeSort;
import com.algomotion.algorithm.QuickSort;
import com.algomotion.algorithm.SelectionSort;
import com.algomotion.algorithm.SortingAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BenchmarkService Tests")
class BenchmarkServiceTest {

    private BenchmarkService benchmarkService;
    private List<SortingAlgorithm> algorithms;

    @BeforeEach
    void setUp() {
        benchmarkService = new BenchmarkService();
        algorithms = List.of(
                new BubbleSort(),
                new SelectionSort(),
                new InsertionSort(),
                new MergeSort(),
                new QuickSort()
        );
    }

    @Test
    @DisplayName("1. Successfully executes benchmark across multiple sizes and algorithms")
    void testBenchmarkExecutionAndVerification() {
        List<Integer> sizes = List.of(20, 50);
        List<Double> progressList = new ArrayList<>();

        List<BenchmarkResult> results = benchmarkService.runBenchmark(
                algorithms,
                sizes,
                DatasetPattern.RANDOM,
                42L,
                1,
                3,
                progressList::add
        );

        assertNotNull(results);
        assertEquals(algorithms.size() * sizes.size(), results.size());

        for (BenchmarkResult res : results) {
            assertEquals("SUCCESS", res.getStatus());
            assertTrue(res.isSortedCorrectly(), "Algorithm " + res.getAlgorithmName() + " must sort correctly.");
            assertTrue(res.getMedianTimeMillis() >= 0.0, "Execution time should be non-negative.");
            assertTrue(res.getTrialTimesNanos().length == 3, "Must record 3 trial timings.");
        }

        assertFalse(progressList.isEmpty());
        assertEquals(1.0, progressList.getLast(), 0.001, "Progress callback should reach 1.0.");
    }

    @Test
    @DisplayName("2. Confirms documented operation-counting conventions")
    void testOperationCountingConventions() {
        int[] input = {5, 2, 8, 1, 9};

        // Merge Sort must perform writes and 0 swaps
        SortStatistics mergeStats = BenchmarkService.countOperations(new MergeSort(), input);
        assertTrue(mergeStats.comparisons() > 0, "Merge Sort must perform comparisons.");
        assertTrue(mergeStats.writes() > 0, "Merge Sort must perform writes.");
        assertEquals(0, mergeStats.swaps(), "Merge Sort does not swap.");

        // Bubble Sort must perform comparisons and swaps, 0 writes
        SortStatistics bubbleStats = BenchmarkService.countOperations(new BubbleSort(), input);
        assertTrue(bubbleStats.comparisons() > 0);
        assertTrue(bubbleStats.swaps() > 0);
        assertEquals(0, bubbleStats.writes());

        // Insertion Sort must perform comparisons and writes
        SortStatistics insertStats = BenchmarkService.countOperations(new InsertionSort(), input);
        assertTrue(insertStats.comparisons() > 0);
        assertTrue(insertStats.writes() > 0);
    }

    @Test
    @DisplayName("3. Rejects empty algorithms list with IllegalArgumentException")
    void testEmptyAlgorithmsListThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                benchmarkService.runBenchmark(List.of(), List.of(50), DatasetPattern.RANDOM, 42L, 1, 1, null));
    }

    @Test
    @DisplayName("4. Rejects empty sizes list with IllegalArgumentException")
    void testEmptySizesListThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                benchmarkService.runBenchmark(algorithms, List.of(), DatasetPattern.RANDOM, 42L, 1, 1, null));
    }

    @Test
    @DisplayName("5. Handles negative size gracefully by reporting invalid size error")
    void testNegativeSizeHandledGracefully() {
        List<BenchmarkResult> results = benchmarkService.runBenchmark(
                List.of(new QuickSort()),
                List.of(-10),
                DatasetPattern.RANDOM,
                42L,
                1,
                1,
                null
        );

        assertEquals(1, results.size());
        BenchmarkResult res = results.getFirst();
        assertEquals("INVALID_SIZE", res.getStatus());
        assertFalse(res.isSortedCorrectly());
    }

    @Test
    @DisplayName("6. Benchmark results are reproducible with same seed")
    void testBenchmarkReproducibleDataset() {
        List<BenchmarkResult> run1 = benchmarkService.runBenchmark(
                List.of(new BubbleSort()), List.of(30), DatasetPattern.RANDOM, 9999L, 1, 1, null);
        List<BenchmarkResult> run2 = benchmarkService.runBenchmark(
                List.of(new BubbleSort()), List.of(30), DatasetPattern.RANDOM, 9999L, 1, 1, null);

        // Even though time will differ slightly by sub-milliseconds, the operations must be 100% identical!
        assertEquals(run1.getFirst().getComparisons(), run2.getFirst().getComparisons(),
                "Deterministic seed must produce identical comparison counts.");
        assertEquals(run1.getFirst().getSwaps(), run2.getFirst().getSwaps(),
                "Deterministic seed must produce identical swap counts.");
    }
}
