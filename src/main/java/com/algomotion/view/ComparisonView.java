package com.algomotion.view;

import com.algomotion.algorithm.SortingAlgorithm;
import com.algomotion.model.ComparisonRowModel;
import com.algomotion.model.DatasetPattern;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main view for the Multi-Algorithm Comparison Workspace.
 * Accommodates dataset selection, algorithm checkboxes, synchronized visualization panels,
 * playback toolbar, and live results comparison table.
 */
public class ComparisonView extends BorderPane {

    // Config controls
    private final ComboBox<DatasetPattern> patternComboBox;
    private final Slider sizeSlider;
    private final Label sizeValueLabel;
    private final TextField seedTextField;
    private final Button newDatasetButton;

    // Checkboxes for algorithms
    private final Map<SortingAlgorithm, CheckBox> algorithmCheckBoxes = new HashMap<>();

    // Visualization Panels Area
    private final GridPane panelsGrid;
    private final ScrollPane panelsScrollPane;

    // Playback Controls
    private final Button playPauseButton;
    private final Button stepButton;
    private final Button resetButton;
    private final Button restartButton;
    private final Slider speedSlider;
    private final Label speedValueLabel;

    // Results Table & Insights
    private final TableView<ComparisonRowModel> resultsTable;
    private final Label insightsLabel;

    public ComparisonView(List<SortingAlgorithm> availableAlgorithms) {
        getStyleClass().add("root-layout");

        // Input Configuration
        this.patternComboBox = new ComboBox<>();
        this.patternComboBox.getItems().setAll(DatasetPattern.values());
        this.patternComboBox.setValue(DatasetPattern.RANDOM);
        this.patternComboBox.getStyleClass().add("algo-combo-box");

        this.sizeSlider = new Slider(5, 50, 20);
        this.sizeSlider.setBlockIncrement(1);
        this.sizeSlider.setSnapToTicks(true);
        this.sizeSlider.getStyleClass().add("custom-slider");

        this.sizeValueLabel = new Label("20");
        this.sizeValueLabel.getStyleClass().add("slider-value-text");

        this.seedTextField = new TextField();
        this.seedTextField.setPromptText("Seed (optional)");
        this.seedTextField.setPrefWidth(110);
        this.seedTextField.getStyleClass().add("seed-input");

        this.newDatasetButton = new Button("New Dataset 🎲");
        this.newDatasetButton.getStyleClass().addAll("btn", "btn-secondary");

        // Algorithm Checkboxes
        for (SortingAlgorithm algo : availableAlgorithms) {
            CheckBox cb = new CheckBox(algo.getName());
            cb.setSelected(true); // default all selected
            cb.getStyleClass().add("algo-checkbox");
            algorithmCheckBoxes.put(algo, cb);
        }

        // Center Panels Grid
        this.panelsGrid = new GridPane();
        this.panelsGrid.setHgap(12);
        this.panelsGrid.setVgap(12);
        this.panelsGrid.setPadding(new Insets(10));

        this.panelsScrollPane = new ScrollPane(panelsGrid);
        this.panelsScrollPane.setFitToWidth(true);
        this.panelsScrollPane.getStyleClass().add("panels-scroll-pane");

        // Playback Buttons
        this.playPauseButton = new Button("Play ▶");
        this.playPauseButton.getStyleClass().addAll("btn", "btn-primary");

        this.stepButton = new Button("Next Step ⏭");
        this.stepButton.getStyleClass().addAll("btn", "btn-secondary");

        this.resetButton = new Button("Reset ↺");
        this.resetButton.getStyleClass().addAll("btn", "btn-secondary");

        this.restartButton = new Button("Restart ⟲");
        this.restartButton.getStyleClass().addAll("btn", "btn-secondary");

        this.speedSlider = new Slider(10, 500, 100);
        this.speedSlider.getStyleClass().add("custom-slider");

        this.speedValueLabel = new Label("100 ms");
        this.speedValueLabel.getStyleClass().add("slider-value-text");

        // Table & Insights
        this.resultsTable = createResultsTable();
        this.insightsLabel = new Label("Select algorithms and start playback to observe real-time operational metrics.");
        this.insightsLabel.getStyleClass().add("insights-banner");

        // Layout Assembly
        setTop(buildTopSection());
        setCenter(panelsScrollPane);
        setBottom(buildBottomSection());
    }

    private VBox buildTopSection() {
        VBox topBox = new VBox(10);
        topBox.getStyleClass().add("top-container");
        topBox.setPadding(new Insets(14, 20, 10, 20));

        // Header Title
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("Algomorph — Comparison Lab");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("Compare multiple algorithms running concurrently on identical shared datasets.");
        subtitle.getStyleClass().add("app-subtitle");
        titleBox.getChildren().addAll(title, subtitle);

        titleRow.getChildren().add(titleBox);

        // Shared Configuration Bar
        HBox configRow = new HBox(14);
        configRow.setAlignment(Pos.CENTER_LEFT);

        HBox patternBox = new HBox(6, new Label("Pattern:"), patternComboBox);
        patternBox.setAlignment(Pos.CENTER_LEFT);

        HBox sizeBox = new HBox(6, new Label("Size:"), sizeSlider, sizeValueLabel);
        sizeBox.setAlignment(Pos.CENTER_LEFT);

        HBox seedBox = new HBox(6, new Label("Seed:"), seedTextField);
        seedBox.setAlignment(Pos.CENTER_LEFT);

        configRow.getChildren().addAll(patternBox, sizeBox, seedBox, newDatasetButton);

        // Algorithm Selection Row
        HBox selectionRow = new HBox(14);
        selectionRow.setAlignment(Pos.CENTER_LEFT);
        Label selectLabel = new Label("Active Algorithms (min 2):");
        selectLabel.getStyleClass().add("control-label");
        selectionRow.getChildren().add(selectLabel);
        selectionRow.getChildren().addAll(algorithmCheckBoxes.values());

        topBox.getChildren().addAll(titleRow, configRow, selectionRow);
        return topBox;
    }

    private VBox buildBottomSection() {
        VBox bottomBox = new VBox(10);
        bottomBox.getStyleClass().add("bottom-container");
        bottomBox.setPadding(new Insets(10, 20, 14, 20));

        // Controls Row
        HBox controlsRow = new HBox(14);
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        HBox btnGroup = new HBox(8, playPauseButton, stepButton, resetButton, restartButton);
        btnGroup.setAlignment(Pos.CENTER_LEFT);

        Separator sep = new Separator(javafx.geometry.Orientation.VERTICAL);

        HBox speedBox = new HBox(6, new Label("Delay:"), speedSlider, speedValueLabel);
        speedBox.setAlignment(Pos.CENTER_LEFT);

        controlsRow.getChildren().addAll(btnGroup, sep, speedBox);

        // Results Table Container
        VBox tableContainer = new VBox(6);
        Label tableHeader = new Label("Synchronized Operation Metrics & Comparison");
        tableHeader.getStyleClass().add("control-label");

        resultsTable.setPrefHeight(130);
        tableContainer.getChildren().addAll(tableHeader, resultsTable, insightsLabel);

        bottomBox.getChildren().addAll(controlsRow, tableContainer);
        return bottomBox;
    }

    private TableView<ComparisonRowModel> createResultsTable() {
        TableView<ComparisonRowModel> table = new TableView<>();
        table.getStyleClass().add("comparison-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<ComparisonRowModel, String> colAlgo = new TableColumn<>("Algorithm");
        colAlgo.setCellValueFactory(c -> c.getValue().algorithmNameProperty());
        colAlgo.setPrefWidth(130);

        TableColumn<ComparisonRowModel, Number> colComp = new TableColumn<>("Comparisons");
        colComp.setCellValueFactory(c -> c.getValue().comparisonsProperty());
        colComp.setPrefWidth(95);

        TableColumn<ComparisonRowModel, Number> colSwaps = new TableColumn<>("Swaps");
        colSwaps.setCellValueFactory(c -> c.getValue().swapsProperty());
        colSwaps.setPrefWidth(80);

        TableColumn<ComparisonRowModel, Number> colWrites = new TableColumn<>("Writes");
        colWrites.setCellValueFactory(c -> c.getValue().writesProperty());
        colWrites.setPrefWidth(80);

        TableColumn<ComparisonRowModel, Number> colEvents = new TableColumn<>("Total Events");
        colEvents.setCellValueFactory(c -> c.getValue().totalEventsProperty());
        colEvents.setPrefWidth(95);

        TableColumn<ComparisonRowModel, Number> colTime = new TableColumn<>("Prep Time (ms)");
        colTime.setCellValueFactory(c -> c.getValue().elapsedMillisProperty());
        colTime.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f ms", item.doubleValue()));
            }
        });
        colTime.setPrefWidth(105);

        TableColumn<ComparisonRowModel, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(c -> c.getValue().statusProperty());
        colStatus.setPrefWidth(90);

        TableColumn<ComparisonRowModel, String> colHighlights = new TableColumn<>("Analysis Highlights");
        colHighlights.setCellValueFactory(c -> c.getValue().highlightsProperty());
        colHighlights.setPrefWidth(180);

        table.getColumns().addAll(colAlgo, colComp, colSwaps, colWrites, colEvents, colTime, colStatus, colHighlights);
        return table;
    }

    public void arrangePanels(List<AlgorithmComparisonPanel> panels) {
        panelsGrid.getChildren().clear();
        int count = panels.size();
        if (count == 0) return;

        // Dynamic column layout: 2 columns for up to 4 items, 3 columns for 5+
        int numCols = (count <= 2) ? 2 : (count <= 4 ? 2 : 3);

        for (int i = 0; i < count; i++) {
            int row = i / numCols;
            int col = i % numCols;
            AlgorithmComparisonPanel panel = panels.get(i);
            GridPane.setHgrow(panel, Priority.ALWAYS);
            GridPane.setVgrow(panel, Priority.ALWAYS);
            panelsGrid.add(panel, col, row);
        }
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
        patternComboBox.setDisable(running);
        seedTextField.setDisable(running);
        newDatasetButton.setDisable(running);
        algorithmCheckBoxes.values().forEach(cb -> cb.setDisable(running));
    }

    public void updateInsights(String insights) {
        insightsLabel.setText(insights);
    }

    // Getters
    public ComboBox<DatasetPattern> getPatternComboBox() {
        return patternComboBox;
    }

    public Slider getSizeSlider() {
        return sizeSlider;
    }

    public Label getSizeValueLabel() {
        return sizeValueLabel;
    }

    public TextField getSeedTextField() {
        return seedTextField;
    }

    public Button getNewDatasetButton() {
        return newDatasetButton;
    }

    public Map<SortingAlgorithm, CheckBox> getAlgorithmCheckBoxes() {
        return algorithmCheckBoxes;
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

    public Button getRestartButton() {
        return restartButton;
    }

    public Slider getSpeedSlider() {
        return speedSlider;
    }

    public Label getSpeedValueLabel() {
        return speedValueLabel;
    }

    public TableView<ComparisonRowModel> getResultsTable() {
        return resultsTable;
    }
}
