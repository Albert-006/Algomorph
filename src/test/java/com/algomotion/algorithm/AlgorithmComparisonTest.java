package com.algomotion.algorithm;

import com.algomotion.model.SortResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Algorithm Consistency & Switching Tests")
class AlgorithmComparisonTest {

    private final List<SortingAlgorithm> algorithms = List.of(
            new BubbleSort(),
            new SelectionSort(),
            new InsertionSort(),
            new MergeSort(),
            new QuickSort()
    );

    @Test
    @DisplayName("1. All five algorithms produce identical sorted output for random deterministic arrays")
    void testAllAlgorithmsProduceSameSortedOutput() {
        int[] input = {64, -5, 25, 12, 22, -5, 90, 0, 11};
        int[] expected = {-5, -5, 0, 11, 12, 22, 25, 64, 90};

        for (SortingAlgorithm algo : algorithms) {
            SortResult result = algo.sort(input);
            assertArrayEquals(expected, result.sortedArray(),
                    "Algorithm " + algo.getName() + " failed to sort array properly.");
            assertFalse(result.events().isEmpty(),
                    "Algorithm " + algo.getName() + " generated no events.");
        }
    }

    @Test
    @DisplayName("2. All algorithms correctly preserve theoretical complexity metadata")
    void testComplexityMetadata() {
        for (SortingAlgorithm algo : algorithms) {
            assertNotNull(algo.getName());
            assertNotNull(algo.getBestTimeComplexity());
            assertNotNull(algo.getAverageTimeComplexity());
            assertNotNull(algo.getWorstTimeComplexity());
            assertNotNull(algo.getSpaceComplexity());
            assertNotNull(algo.getDescription());
        }

        // Verify specific theoretical expectations
        SortingAlgorithm bubble = algorithms.get(0);
        assertEquals("Bubble Sort", bubble.getName());
        assertEquals("O(n)", bubble.getBestTimeComplexity());

        SortingAlgorithm selection = algorithms.get(1);
        assertEquals("Selection Sort", selection.getName());
        assertEquals("O(n²)", selection.getBestTimeComplexity());

        SortingAlgorithm insertion = algorithms.get(2);
        assertEquals("Insertion Sort", insertion.getName());
        assertEquals("O(n)", insertion.getBestTimeComplexity());

        SortingAlgorithm merge = algorithms.get(3);
        assertEquals("Merge Sort", merge.getName());
        assertEquals("O(n log n)", merge.getBestTimeComplexity());
        assertEquals("O(n)", merge.getSpaceComplexity());

        SortingAlgorithm quick = algorithms.get(4);
        assertEquals("Quick Sort", quick.getName());
        assertEquals("O(n log n)", quick.getBestTimeComplexity());
        assertEquals("O(n²)", quick.getWorstTimeComplexity());
    }

    @Test
    @DisplayName("3. Verify switching between algorithms on the same dataset")
    void testSwitchingBetweenAlgorithms() {
        int[] dataset = {50, 20, 40, 10, 30};

        // First run with Bubble Sort
        SortingAlgorithm bubble = new BubbleSort();
        SortResult bubbleResult = bubble.sort(dataset);
        assertEquals(5, bubbleResult.sortedArray().length);
        assertArrayEquals(new int[]{10, 20, 30, 40, 50}, bubbleResult.sortedArray());

        // Switch to Merge Sort
        SortingAlgorithm merge = new MergeSort();
        SortResult mergeResult = merge.sort(dataset);
        assertArrayEquals(bubbleResult.sortedArray(), mergeResult.sortedArray());
        assertTrue(mergeResult.statistics().writes() > 0, "Merge sort should record writes.");

        // Switch to Quick Sort
        SortingAlgorithm quick = new QuickSort();
        SortResult quickResult = quick.sort(dataset);
        assertArrayEquals(bubbleResult.sortedArray(), quickResult.sortedArray());

        // Switch to Selection Sort
        SortingAlgorithm selection = new SelectionSort();
        SortResult selectionResult = selection.sort(dataset);
        assertArrayEquals(bubbleResult.sortedArray(), selectionResult.sortedArray());

        // Switch to Insertion Sort
        SortingAlgorithm insertion = new InsertionSort();
        SortResult insertionResult = insertion.sort(dataset);
        assertArrayEquals(bubbleResult.sortedArray(), insertionResult.sortedArray());
    }

    @Test
    @DisplayName("4. sortPure matches sort().sortedArray() exactly for all algorithms across all patterns")
    void testSortPureMatchesFullSort() {
        for (com.algomotion.model.DatasetPattern pattern : com.algomotion.model.DatasetPattern.values()) {
            int[] dataset = com.algomotion.model.DatasetGenerator.generate(40, pattern, 12345L);
            for (SortingAlgorithm algo : algorithms) {
                int[] pureResult = algo.sortPure(dataset);
                int[] fullResult = algo.sort(dataset).sortedArray();

                assertArrayEquals(fullResult, pureResult,
                        "sortPure output must match sort().sortedArray() for " + algo.getName() + " on " + pattern);
            }
        }
    }

    @Test
    @DisplayName("5. All algorithms correctly sort identical copies across all 5 dataset patterns")
    void testAllAlgorithmsAcrossAllPatterns() {
        for (com.algomotion.model.DatasetPattern pattern : com.algomotion.model.DatasetPattern.values()) {
            int[] sharedInput = com.algomotion.model.DatasetGenerator.generate(35, pattern, 9999L);
            int[] reference = sharedInput.clone();
            java.util.Arrays.sort(reference);

            for (SortingAlgorithm algo : algorithms) {
                int[] copy = sharedInput.clone();
                SortResult res = algo.sort(copy);
                assertArrayEquals(reference, res.sortedArray(),
                        algo.getName() + " failed to sort pattern " + pattern);
                assertArrayEquals(sharedInput, copy,
                        algo.getName() + " must not mutate caller input array for pattern " + pattern);
            }
        }
    }
}
