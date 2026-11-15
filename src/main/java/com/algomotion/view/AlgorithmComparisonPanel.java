package com.algomotion.view;

import com.algomotion.algorithm.SortingAlgorithm;
import com.algomotion.model.SortEvent;
import com.algomotion.model.SortStatistics;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * Individual visual panel representing a single algorithm inside the Comparison Workspace.
 * Contains its own header, operation banner, bar chart, and real-time metrics.
 */
public class AlgorithmComparisonPanel extends VBox {

    private final SortingAlgorithm algorithm;
    private final BarChartView barChartView;

    private final Label statusBadge;
    private final Label operationLabel;

    private final Label comparisonsValueLabel;
    private final Label swapsValueLabel;
    private final Label writesValueLabel;
    private final Label progressValueLabel;

    public AlgorithmComparisonPanel(SortingAlgorithm algorithm) {
        this.algorithm = Objects.requireNonNull(algorithm, "Algorithm must not be null");
        getStyleClass().add("comparison-panel");
        setSpacing(6);
        setPadding(new Insets(10));

        this.barChartView = new BarChartView();
        this.barChartView.setMinHeight(160);
        VBox.setVgrow(barChartView, Priority.ALWAYS);

        // Header: Name + Badge
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(algorithm.getName());
        nameLabel.getStyleClass().add("comparison-algo-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.statusBadge = new Label("READY");
        this.statusBadge.getStyleClass().addAll("badge", "badge-ready");

        header.getChildren().addAll(nameLabel, spacer, statusBadge);

        // Operation Banner
        this.operationLabel = new Label("Ready to compare.");
        this.operationLabel.getStyleClass().add("comparison-operation-label");

        // Metrics
        this.comparisonsValueLabel = new Label("0");
        this.comparisonsValueLabel.getStyleClass().add("mini-metric-value");

        this.swapsValueLabel = new Label("0");
        this.swapsValueLabel.getStyleClass().add("mini-metric-value");

        this.writesValueLabel = new Label("0");
        this.writesValueLabel.getStyleClass().add("mini-metric-value");

        this.progressValueLabel = new Label("0 / 0");
        this.progressValueLabel.getStyleClass().add("mini-metric-value");

        HBox metricsRow = new HBox(8);
        metricsRow.setAlignment(Pos.CENTER_LEFT);
        metricsRow.getChildren().addAll(
                createMiniCard("C:", comparisonsValueLabel),
                createMiniCard("S:", swapsValueLabel),
                createMiniCard("W:", writesValueLabel),
                createMiniCard("Step:", progressValueLabel)
        );

        getChildren().addAll(header, operationLabel, barChartView, metricsRow);
    }

    private HBox createMiniCard(String prefix, Label value) {
        HBox box = new HBox(3);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("mini-metric-box");
        Label p = new Label(prefix);
        p.getStyleClass().add("mini-metric-prefix");
        box.getChildren().addAll(p, value);
        return box;
    }

    public void setFixedMaxAbsValue(int maxVal) {
        barChartView.setFixedMaxAbsValue(maxVal);
    }

    public void displayInitialState(int[] array, int totalEvents) {
        barChartView.displayArray(array);
        updateStatus("READY", "badge-ready");
        operationLabel.setText("Ready to sort identical dataset.");
        comparisonsValueLabel.setText("0");
        swapsValueLabel.setText("0");
        writesValueLabel.setText("0");
        progressValueLabel.setText("0 / " + totalEvents);
    }

    public void displayEvent(SortEvent event, int currentStep, int totalEvents, int arraySize) {
        barChartView.displayEvent(event);
        operationLabel.setText(event.getDescription());
        comparisonsValueLabel.setText(String.valueOf(event.getComparisonsSoFar()));
        swapsValueLabel.setText(String.valueOf(event.getSwapsSoFar()));
        writesValueLabel.setText(String.valueOf(event.getWritesSoFar()));
        progressValueLabel.setText(currentStep + " / " + totalEvents);
    }

    public void displayCompleted(int[] finalArray, SortStatistics stats) {
        barChartView.displayCompleted(finalArray);
        updateStatus("COMPLETED", "badge-completed");
        operationLabel.setText("Finished sorting!");
        comparisonsValueLabel.setText(String.valueOf(stats.comparisons()));
        swapsValueLabel.setText(String.valueOf(stats.swaps()));
        writesValueLabel.setText(String.valueOf(stats.writes()));
        progressValueLabel.setText(stats.totalEvents() + " / " + stats.totalEvents());
    }

    public void updateStatus(String statusText, String badgeClass) {
        statusBadge.setText(statusText);
        statusBadge.getStyleClass().removeAll("badge-ready", "badge-running", "badge-paused", "badge-completed");
        statusBadge.getStyleClass().add(badgeClass);
    }

    public SortingAlgorithm getAlgorithm() {
        return algorithm;
    }

    public BarChartView getBarChartView() {
        return barChartView;
    }
}
