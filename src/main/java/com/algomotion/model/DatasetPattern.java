package com.algomotion.model;

/**
 * Supported dataset distribution patterns for algorithm visualization and benchmarking.
 */
public enum DatasetPattern {
    RANDOM("Random"),
    ALREADY_SORTED("Already Sorted"),
    REVERSE_SORTED("Reverse Sorted"),
    NEARLY_SORTED("Nearly Sorted"),
    MANY_DUPLICATES("Many Duplicates");

    private final String displayName;

    DatasetPattern(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
