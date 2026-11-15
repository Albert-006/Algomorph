package com.algomotion.view;

import com.algomotion.algorithm.SortingAlgorithm;
import com.algomotion.model.SortEvent;
import com.algomotion.model.SortStatistics;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;

/**
 * Main visual container for Algomorph.
 * Manages the layout including algorithm selection, complexity panel,
 * metrics dashboard, visualization canvas, and playback toolbar.
 */
public class SortingView extends BorderPane {

    // Views
    private final BarChartView barChartView;

    // Algorithm Selector
    private final ComboBox<SortingAlgorithm> algorithmSelector;

    // Status and operation labels
    private final Label statusBadge;
    private final Label operationLabel;
    private final Label algorithmDescLabel;

    // Complexity panel labels
    private final Label bestTimeLabel;
    private final Label avgTimeLabel;
    private final Label worstTimeLabel;
    private final Label spaceComplexityLabel;

    // Stat metric values
    private final Label comparisonsValueLabel;
    private final Label swapsValueLabel;
    private final Label writesValueLabel;
    private final Label arraySizeValueLabel;
    private final Label progressValueLabel;

    // Controls
    private final Button playPauseButton;
    private final Button stepButton;
    private final Button resetButton;
    private final Button randomizeButton;
    private final Slider sizeSlider;
    private final Slider speedSlider;
    private final Label speedValueLabel;
    private final Label sizeValueDisplayLabel;

    public SortingView() {
        getStyleClass().add("root-layout");

        this.barChartView = new BarChartView();

        // Algorithm Selector
        this.algorithmSelector = new ComboBox<>();
        this.algorithmSelector.getStyleClass().add("algo-combo-box");
        setupAlgorithmSelectorCellFactory();

        // Status and info
        this.statusBadge = new Label("READY");
        this.statusBadge.getStyleClass().addAll("badge", "badge-ready");

        this.algorithmDescLabel = new Label("");
        this.algorithmDescLabel.getStyleClass().add("app-subtitle");

        this.operationLabel = new Label("Click 'Play' or 'Next Step' to begin sorting.");
        this.operationLabel.getStyleClass().add("operation-label");

        // Complexity Labels
        this.bestTimeLabel = new Label("O(n)");
        this.bestTimeLabel.getStyleClass().add("complexity-val");

        this.avgTimeLabel = new Label("O(n²)");
        this.avgTimeLabel.getStyleClass().add("complexity-val");

        this.worstTimeLabel = new Label("O(n²)");
        this.worstTimeLabel.getStyleClass().add("complexity-val");

        this.spaceComplexityLabel = new Label("O(1)");
        this.spaceComplexityLabel.getStyleClass().add("complexity-val");

        // Metrics
        this.comparisonsValueLabel = new Label("0");
        this.comparisonsValueLabel.getStyleClass().add("metric-value");

        this.swapsValueLabel = new Label("0");
        this.swapsValueLabel.getStyleClass().add("metric-value");

        this.writesValueLabel = new Label("0");
        this.writesValueLabel.getStyleClass().add("metric-value");

        this.arraySizeValueLabel = new Label("20");
        this.arraySizeValueLabel.getStyleClass().add("metric-value");

        this.progressValueLabel = new Label("0 / 0");
        this.progressValueLabel.getStyleClass().add("metric-value");

        // Buttons
        this.playPauseButton = new Button("Play ▶");
        this.playPauseButton.getStyleClass().addAll("btn", "btn-primary");

        this.stepButton = new Button("Next Step ⏭");
        this.stepButton.getStyleClass().addAll("btn", "btn-secondary");

        this.resetButton = new Button("Reset ↺");
        this.resetButton.getStyleClass().addAll("btn", "btn-secondary");

        this.randomizeButton = new Button("Randomize 🎲");
        this.randomizeButton.getStyleClass().addAll("btn", "btn-secondary");

        // Sliders
        this.sizeSlider = new Slider(5, 50, 20);
        this.sizeSlider.setBlockIncrement(1);
        this.sizeSlider.setMajorTickUnit(15);
        this.sizeSlider.setSnapToTicks(true);
        this.sizeSlider.getStyleClass().add("custom-slider");

        this.sizeValueDisplayLabel = new Label("20");
        this.sizeValueDisplayLabel.getStyleClass().add("slider-value-text");

        this.speedSlider = new Slider(10, 500, 100);
        this.speedSlider.getStyleClass().add("custom-slider");

        this.speedValueLabel = new Label("100 ms");
        this.speedValueLabel.getStyleClass().add("slider-value-text");

        // Construct layout sections
        setTop(buildTopSection());
        setCenter(buildCenterSection());
        setBottom(buildBottomSection());
    }

    private void setupAlgorithmSelectorCellFactory() {
        StringConverter<SortingAlgorithm> converter = new StringConverter<>() {
            @Override
            public String toString(SortingAlgorithm algo) {
                return algo != null ? algo.getName() : "";
            }

            @Override
            public SortingAlgorithm fromString(String string) {
                return null;
            }
        };
        algorithmSelector.setConverter(converter);

        algorithmSelector.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(SortingAlgorithm item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        algorithmSelector.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(SortingAlgorithm item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
    }

    private VBox buildTopSection() {
        VBox topBox = new VBox(10);
        topBox.getStyleClass().add("top-container");
        topBox.setPadding(new Insets(14, 24, 10, 24));

        // Row 1: App Header + Algorithm Selector + Status Badge
        HBox headerRow = new HBox(16);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label titleLabel = new Label("Algomorph");
        titleLabel.getStyleClass().add("app-title");
        titleBox.getChildren().addAll(titleLabel, algorithmDescLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox selectorBox = new HBox(8);
        selectorBox.setAlignment(Pos.CENTER);
        Label algoLabel = new Label("Algorithm:");
        algoLabel.getStyleClass().add("control-label");
        selectorBox.getChildren().addAll(algoLabel, algorithmSelector);

        HBox statusBox = new HBox(8);
        statusBox.setAlignment(Pos.CENTER_RIGHT);
        Label statusPrefix = new Label("Status:");
        statusPrefix.getStyleClass().add("status-prefix");
        statusBox.getChildren().addAll(statusPrefix, statusBadge);

        headerRow.getChildren().addAll(titleBox, spacer, selectorBox, statusBox);

        // Row 2: Complexity Information Panel
        HBox complexityRow = buildComplexityPanel();

        // Row 3: Metrics Dashboard + Visual Legend
        HBox metricsRow = new HBox(10);
        metricsRow.setAlignment(Pos.CENTER_LEFT);

        HBox compCard = createMetricCard("Comparisons", comparisonsValueLabel);
        HBox swapCard = createMetricCard("Swaps", swapsValueLabel);
        HBox writeCard = createMetricCard("Writes", writesValueLabel);
        HBox sizeCard = createMetricCard("Array Size", arraySizeValueLabel);
        HBox stepCard = createMetricCard("Events", progressValueLabel);

        Region legendSpacer = new Region();
        HBox.setHgrow(legendSpacer, Priority.ALWAYS);

        HBox legendBox = buildLegend();

        metricsRow.getChildren().addAll(compCard, swapCard, writeCard, sizeCard, stepCard, legendSpacer, legendBox);

        // Row 4: Operation text banner
        HBox operationRow = new HBox(operationLabel);
        operationRow.setAlignment(Pos.CENTER_LEFT);
        operationRow.getStyleClass().add("operation-banner");

        topBox.getChildren().addAll(headerRow, complexityRow, metricsRow, operationRow);
        return topBox;
    }

    private HBox buildComplexityPanel() {
        HBox panel = new HBox(18);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.getStyleClass().add("complexity-panel");

        panel.getChildren().addAll(
                createComplexityChip("Best Time", bestTimeLabel),
                createComplexityChip("Avg Time", avgTimeLabel),
                createComplexityChip("Worst Time", worstTimeLabel),
                createComplexityChip("Aux Space", spaceComplexityLabel)
        );
        return panel;
    }

    private HBox createComplexityChip(String labelText, Label valLabel) {
        HBox chip = new HBox(6);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.getStyleClass().add("complexity-chip");

        Label label = new Label(labelText + ":");
        label.getStyleClass().add("complexity-label");

        chip.getChildren().addAll(label, valLabel);
        return chip;
    }

    private HBox createMetricCard(String title, Label valueLabel) {
        HBox card = new HBox(8);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("metric-card");

        VBox textGroup = new VBox(1);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");
        textGroup.getChildren().addAll(titleLabel, valueLabel);

        card.getChildren().add(textGroup);
        return card;
    }

    private HBox buildLegend() {
        HBox legend = new HBox(10);
        legend.setAlignment(Pos.CENTER_RIGHT);
        legend.getStyleClass().add("legend-container");

        legend.getChildren().addAll(
                createLegendItem("Default", "#38bdf8"),
                createLegendItem("Comparing", "#fbbf24"),
                createLegendItem("Swap", "#f43f5e"),
                createLegendItem("Write/Shift", "#ec4899"),
                createLegendItem("Pivot/Min", "#facc15"),
                createLegendItem("Sorted", "#34d399")
        );
        return legend;
    }

    private HBox createLegendItem(String labelText, String hexColor) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER);
        Circle dot = new Circle(4.5);
        dot.setStyle("-fx-fill: " + hexColor + ";");
        Label label = new Label(labelText);
        label.getStyleClass().add("legend-label");
        item.getChildren().addAll(dot, label);
        return item;
    }

    private VBox buildCenterSection() {
        VBox centerBox = new VBox();
        centerBox.setPadding(new Insets(4, 24, 10, 24));
        VBox.setVgrow(barChartView, Priority.ALWAYS);
        barChartView.setMinHeight(250);

        centerBox.getChildren().add(barChartView);
        return centerBox;
    }

    private VBox buildBottomSection() {
        VBox bottomBox = new VBox(10);
        bottomBox.getStyleClass().add("bottom-container");
        bottomBox.setPadding(new Insets(12, 24, 16, 24));

        HBox controlsRow = new HBox(16);
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        // Action buttons
        HBox buttonGroup = new HBox(10, playPauseButton, stepButton, resetButton, randomizeButton);
        buttonGroup.setAlignment(Pos.CENTER_LEFT);

        Separator sep1 = new Separator();
        sep1.setOrientation(javafx.geometry.Orientation.VERTICAL);

        // Size slider group
        HBox sizeGroup = new HBox(8);
        sizeGroup.setAlignment(Pos.CENTER);
        Label sizeLabel = new Label("Size:");
        sizeLabel.getStyleClass().add("control-label");
        sizeGroup.getChildren().addAll(sizeLabel, sizeSlider, sizeValueDisplayLabel);

        Separator sep2 = new Separator();
        sep2.setOrientation(javafx.geometry.Orientation.VERTICAL);

        // Speed slider group
        HBox speedGroup = new HBox(8);
        speedGroup.setAlignment(Pos.CENTER);
        Label speedLabel = new Label("Delay:");
        speedLabel.getStyleClass().add("control-label");
        speedGroup.getChildren().addAll(speedLabel, speedSlider, speedValueLabel);

        controlsRow.getChildren().addAll(buttonGroup, sep1, sizeGroup, sep2, speedGroup);
        bottomBox.getChildren().add(controlsRow);

        return bottomBox;
    }

    // --- State Update Methods ---

    public void updateAlgorithmInfo(SortingAlgorithm algorithm) {
        if (algorithm == null) return;
        algorithmDescLabel.setText(algorithm.getName() + " — " + algorithm.getDescription());
        bestTimeLabel.setText(algorithm.getBestTimeComplexity());
        avgTimeLabel.setText(algorithm.getAverageTimeComplexity());
        worstTimeLabel.setText(algorithm.getWorstTimeComplexity());
        spaceComplexityLabel.setText(algorithm.getSpaceComplexity());
    }

    public void updateStatus(String statusText, String badgeClass) {
        statusBadge.setText(statusText);
        statusBadge.getStyleClass().removeAll("badge-ready", "badge-running", "badge-paused", "badge-completed");
        statusBadge.getStyleClass().add(badgeClass);
    }

    public void updateOperation(String operation) {
        operationLabel.setText(operation);
    }

    public void updateMetrics(int comparisons, int swaps, int writes, int currentEvent, int totalEvents, int arraySize) {
        comparisonsValueLabel.setText(String.valueOf(comparisons));
        swapsValueLabel.setText(String.valueOf(swaps));
        writesValueLabel.setText(String.valueOf(writes));
        arraySizeValueLabel.setText(String.valueOf(arraySize));
        progressValueLabel.setText(currentEvent + " / " + totalEvents);
    }

    public void setPlayButtonRunning(boolean isRunning) {
        if (isRunning) {
            playPauseButton.setText("Pause ⏸");
            playPauseButton.getStyleClass().removeAll("btn-primary");
            playPauseButton.getStyleClass().add("btn-warning");
        } else {
            playPauseButton.setText("Play ▶");
            playPauseButton.getStyleClass().removeAll("btn-warning");
            playPauseButton.getStyleClass().add("btn-primary");
        }
    }

    public void setPlaybackControlsDisabled(boolean running) {
        stepButton.setDisable(running);
        sizeSlider.setDisable(running);
        algorithmSelector.setDisable(running);
    }

    public void displayInitialState(int[] array, int totalEvents) {
        barChartView.displayArray(array);
        updateStatus("READY", "badge-ready");
        updateOperation("Array ready. Click 'Play' or 'Next Step' to begin sorting.");
        updateMetrics(0, 0, 0, 0, totalEvents, array.length);
        setPlayButtonRunning(false);
        setPlaybackControlsDisabled(false);
        stepButton.setDisable(false);
    }

    public void displayEvent(SortEvent event, int currentEventIndex, int totalEvents, int arraySize) {
        barChartView.displayEvent(event);
        updateOperation(event.getDescription());
        updateMetrics(event.getComparisonsSoFar(), event.getSwapsSoFar(), event.getWritesSoFar(),
                currentEventIndex, totalEvents, arraySize);
    }

    public void displayCompleted(int[] finalArray, SortStatistics stats) {
        barChartView.displayCompleted(finalArray);
        updateStatus("COMPLETED", "badge-completed");
        updateOperation("Sorting successfully finished! Array is fully sorted in ascending order.");
        updateMetrics(stats.comparisons(), stats.swaps(), stats.writes(),
                stats.totalEvents(), stats.totalEvents(), finalArray.length);
        setPlayButtonRunning(false);
        setPlaybackControlsDisabled(false);
        stepButton.setDisable(true); // Can't step further after completion
    }

    // --- Component Getters ---

    public ComboBox<SortingAlgorithm> getAlgorithmSelector() {
        return algorithmSelector;
    }

    public BarChartView getBarChartView() {
        return barChartView;
    }

    public Button getPlayPauseButton() {
        return playPauseButton;
    }

    public Button getStepButton() {
        return stepButton;
    }

    public Button getResetButton() {
        return resetButton;
    }

    public Button getRandomizeButton() {
        return randomizeButton;
    }

    public Slider getSizeSlider() {
        return sizeSlider;
    }

    public Slider getSpeedSlider() {
        return speedSlider;
    }

    public Label getSpeedValueLabel() {
        return speedValueLabel;
    }

    public Label getSizeValueDisplayLabel() {
        return sizeValueDisplayLabel;
    }
}
