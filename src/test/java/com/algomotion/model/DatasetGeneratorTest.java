package com.algomotion.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DatasetGenerator Tests")
class DatasetGeneratorTest {

    @Test
    @DisplayName("1. Generates correct size for all supported patterns")
    void testAllPatternsGenerateCorrectSize() {
        int size = 50;
        for (DatasetPattern pattern : DatasetPattern.values()) {
            int[] arr = DatasetGenerator.generate(size, pattern);
            assertNotNull(arr);
            assertEquals(size, arr.length, "Pattern " + pattern + " should produce array of length " + size);
        }
    }

    @Test
    @DisplayName("2. Generates reproducible dataset when seed is provided")
    void testSeedReproducibility() {
        long seed = 123456789L;
        int size = 100;

        for (DatasetPattern pattern : DatasetPattern.values()) {
            int[] arr1 = DatasetGenerator.generate(size, pattern, seed);
            int[] arr2 = DatasetGenerator.generate(size, pattern, seed);

            assertArrayEquals(arr1, arr2, "Identical seed must produce identical arrays for pattern: " + pattern);
        }
    }

    @Test
    @DisplayName("3. Different seeds produce different datasets on Random pattern")
    void testDifferentSeedsProduceDifferentDatasets() {
        int[] arr1 = DatasetGenerator.generate(100, DatasetPattern.RANDOM, 111L);
        int[] arr2 = DatasetGenerator.generate(100, DatasetPattern.RANDOM, 999L);

        assertFalse(Arrays.equals(arr1, arr2), "Different seeds should produce different sequences.");
    }

    @Test
    @DisplayName("4. ALREADY_SORTED pattern generates monotonically non-decreasing array")
    void testAlreadySortedPattern() {
        int[] arr = DatasetGenerator.generate(50, DatasetPattern.ALREADY_SORTED, 42L);
        for (int i = 0; i < arr.length - 1; i++) {
            assertTrue(arr[i] <= arr[i + 1], "Element at " + i + " must be <= element at " + (i + 1));
        }
    }

    @Test
    @DisplayName("5. REVERSE_SORTED pattern generates monotonically non-increasing array")
    void testReverseSortedPattern() {
        int[] arr = DatasetGenerator.generate(50, DatasetPattern.REVERSE_SORTED, 42L);
        for (int i = 0; i < arr.length - 1; i++) {
            assertTrue(arr[i] >= arr[i + 1], "Element at " + i + " must be >= element at " + (i + 1));
        }
    }

    @Test
    @DisplayName("6. MANY_DUPLICATES pattern generates elements from a restricted small set of values")
    void testManyDuplicatesPattern() {
        int[] arr = DatasetGenerator.generate(100, DatasetPattern.MANY_DUPLICATES, 10, 100, 42L);
        Set<Integer> uniqueValues = new HashSet<>();
        for (int val : arr) {
            uniqueValues.add(val);
        }

        assertTrue(uniqueValues.size() <= 5, "MANY_DUPLICATES should use a small pool of distinct values (<= 5). Found: " + uniqueValues.size());
    }

    @Test
    @DisplayName("7. Handles empty array (size 0) gracefully")
    void testEmptyArray() {
        int[] arr = DatasetGenerator.generate(0, DatasetPattern.RANDOM);
        assertEquals(0, arr.length);
    }

    @Test
    @DisplayName("8. Handles single-element array (size 1) gracefully")
    void testSingleElementArray() {
        int[] arr = DatasetGenerator.generate(1, DatasetPattern.NEARLY_SORTED);
        assertEquals(1, arr.length);
    }

    @Test
    @DisplayName("9. Throws IllegalArgumentException on negative size")
    void testNegativeSizeThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                DatasetGenerator.generate(-5, DatasetPattern.RANDOM));
    }

    @Test
    @DisplayName("10. Throws NullPointerException on null pattern")
    void testNullPatternThrows() {
        assertThrows(NullPointerException.class, () ->
                DatasetGenerator.generate(10, null));
    }

    @Test
    @DisplayName("11. Normalizes inverted min and max bounds")
    void testInvertedBoundsNormalized() {
        // minVal = 100, maxVal = 10
        int[] arr = DatasetGenerator.generate(20, DatasetPattern.RANDOM, 100, 10, 42L);
        assertEquals(20, arr.length);
        for (int val : arr) {
            assertTrue(val >= 10 && val <= 100, "Values must remain within [10, 100].");
        }
    }
}
