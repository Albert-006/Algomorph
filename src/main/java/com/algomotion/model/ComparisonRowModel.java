package com.algomotion.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Model representing a single row in the Comparison Results Table.
 */
public class ComparisonRowModel {

    private final SimpleStringProperty algorithmName;
    private final SimpleIntegerProperty comparisons;
    private final SimpleIntegerProperty swaps;
    private final SimpleIntegerProperty writes;
    private final SimpleIntegerProperty totalEvents;
    private final SimpleDoubleProperty elapsedMillis;
    private final SimpleStringProperty status;
    private final SimpleStringProperty highlights;

    public ComparisonRowModel(String algorithmName, int comparisons, int swaps, int writes,
                              int totalEvents, double elapsedMillis, String status, String highlights) {
        this.algorithmName = new SimpleStringProperty(algorithmName);
        this.comparisons = new SimpleIntegerProperty(comparisons);
        this.swaps = new SimpleIntegerProperty(swaps);
        this.writes = new SimpleIntegerProperty(writes);
        this.totalEvents = new SimpleIntegerProperty(totalEvents);
        this.elapsedMillis = new SimpleDoubleProperty(elapsedMillis);
        this.status = new SimpleStringProperty(status);
        this.highlights = new SimpleStringProperty(highlights != null ? highlights : "");
    }

    public String getAlgorithmName() {
        return algorithmName.get();
    }

    public void setAlgorithmName(String value) {
        this.algorithmName.set(value);
    }

    public SimpleStringProperty algorithmNameProperty() {
        return algorithmName;
    }

    public int getComparisons() {
        return comparisons.get();
    }

    public void setComparisons(int value) {
        this.comparisons.set(value);
    }

    public SimpleIntegerProperty comparisonsProperty() {
        return comparisons;
    }

    public int getSwaps() {
        return swaps.get();
    }

    public void setSwaps(int value) {
        this.swaps.set(value);
    }

    public SimpleIntegerProperty swapsProperty() {
        return swaps;
    }

    public int getWrites() {
        return writes.get();
    }

    public void setWrites(int value) {
        this.writes.set(value);
    }

    public SimpleIntegerProperty writesProperty() {
        return writes;
    }

    public int getTotalEvents() {
        return totalEvents.get();
    }

    public void setTotalEvents(int value) {
        this.totalEvents.set(value);
    }

    public SimpleIntegerProperty totalEventsProperty() {
        return totalEvents;
    }

    public double getElapsedMillis() {
        return elapsedMillis.get();
    }

    public void setElapsedMillis(double value) {
        this.elapsedMillis.set(value);
    }

    public SimpleDoubleProperty elapsedMillisProperty() {
        return elapsedMillis;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String value) {
        this.status.set(value);
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public String getHighlights() {
        return highlights.get();
    }

    public void setHighlights(String value) {
        this.highlights.set(value);
    }

    public SimpleStringProperty highlightsProperty() {
        return highlights;
    }
}
