package com.algomotion.model;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * Utility for generating reproducible datasets of varying sizes and statistical distributions.
 */
public final class DatasetGenerator {

    public static final int DEFAULT_MIN_VALUE = 5;
    public static final int DEFAULT_MAX_VALUE = 100;

    private DatasetGenerator() {
        // Utility class
    }

    /**
     * Generates an array of integers matching the requested size, pattern, and bounds.
     *
     * @param size    number of elements (must be >= 0)
     * @param pattern distribution pattern
     * @param minVal  minimum element value
     * @param maxVal  maximum element value
     * @param seed    optional random seed for deterministic reproducibility (null for non-deterministic)
     * @return generated integer array
     */
    public static int[] generate(int size, DatasetPattern pattern, int minVal, int maxVal, Long seed) {
        if (size < 0) {
            throw new IllegalArgumentException("Dataset size must be non-negative: " + size);
        }
        Objects.requireNonNull(pattern, "DatasetPattern must not be null");

        if (size == 0) {
            return new int[0];
        }

        // Normalize bounds if inverted
        int actualMin = Math.min(minVal, maxVal);
        int actualMax = Math.max(minVal, maxVal);
        int range = actualMax - actualMin + 1;

        Random rng = (seed != null) ? new Random(seed) : new Random();
        int[] arr = new int[size];

        switch (pattern) {
            case RANDOM -> {
                for (int i = 0; i < size; i++) {
                    arr[i] = actualMin + rng.nextInt(range);
                }
            }
            case ALREADY_SORTED -> {
                for (int i = 0; i < size; i++) {
                    arr[i] = actualMin + rng.nextInt(range);
                }
                Arrays.sort(arr);
            }
            case REVERSE_SORTED -> {
                for (int i = 0; i < size; i++) {
                    arr[i] = actualMin + rng.nextInt(range);
                }
                Arrays.sort(arr);
                // Reverse in place
                for (int i = 0; i < size / 2; i++) {
                    int temp = arr[i];
                    arr[i] = arr[size - 1 - i];
                    arr[size - 1 - i] = temp;
                }
            }
            case NEARLY_SORTED -> {
                for (int i = 0; i < size; i++) {
                    arr[i] = actualMin + rng.nextInt(range);
                }
                Arrays.sort(arr);
                // Perform a small number of swaps (~5-8% of size, at least 1 for size >= 2)
                int numSwaps = Math.max(1, (int) Math.round(size * 0.06));
                for (int s = 0; s < numSwaps; s++) {
                    if (size > 1) {
                        int idx1 = rng.nextInt(size - 1);
                        // Swap with adjacent or near element to preserve general sortedness
                        int idx2 = Math.min(size - 1, idx1 + 1 + (size > 5 ? rng.nextInt(2) : 0));
                        int temp = arr[idx1];
                        arr[idx1] = arr[idx2];
                        arr[idx2] = temp;
                    }
                }
            }
            case MANY_DUPLICATES -> {
                // Select from a tiny pool of 3-5 distinct values
                int poolSize = Math.max(2, Math.min(5, range));
                int[] pool = new int[poolSize];
                for (int p = 0; p < poolSize; p++) {
                    pool[p] = actualMin + (p * (range - 1)) / (poolSize - 1);
                }
                for (int i = 0; i < size; i++) {
                    arr[i] = pool[rng.nextInt(poolSize)];
                }
            }
        }

        return arr;
    }

    public static int[] generate(int size, DatasetPattern pattern, Long seed) {
        return generate(size, pattern, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, seed);
    }

    public static int[] generate(int size, DatasetPattern pattern) {
        return generate(size, pattern, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, null);
    }
}
