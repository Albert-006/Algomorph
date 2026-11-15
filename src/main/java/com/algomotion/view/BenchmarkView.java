package com.algomotion.view;

import com.algomotion.algorithm.SortingAlgorithm;
import com.algomotion.model.BenchmarkResult;
import com.algomotion.model.DatasetPattern;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main view for the Benchmark Mode.
 * Features benchmark configuration, execution progress, sortable results table,
 * JavaFX charts, and data-driven insights.
 */
public class BenchmarkView extends BorderPane {

    // Algorithm Checkboxes
    private final Map<SortingAlgorithm, CheckBox> algorithmCheckBoxes = new HashMap<>();

    // Size Checkboxes
    private final Map<Integer, CheckBox> sizeCheckBoxes = new HashMap<>();

    // Config Controls
    private final ComboBox<DatasetPattern> patternComboBox;
    private final TextField seedTextField;
    private final Button runBenchmarkButton;
    private final ProgressBar progressBar;
    private final Label progressLabel;
    private final Label warningLabel;

    // Results Display
    private final TabPane resultsTabPane;
    private final TableView<BenchmarkResult> resultsTable;

    // Charts
    private final BarChart<String, Number> timeChart;
    private final CategoryAxis timeXAxis;
    private final NumberAxis timeYAxis;

    private final BarChart<String, Number> operationsChart;
    private final CategoryAxis opsXAxis;
    private final NumberAxis opsYAxis;

    // Insights
    private final Label insightsSummaryLabel;

    public BenchmarkView(List<SortingAlgorithm> availableAlgorithms) {
        getStyleClass().add("root-layout");

        // 1. Algorithms Selection
        for (SortingAlgorithm algo : availableAlgorithms) {
            CheckBox cb = new CheckBox(algo.getName());
            cb.setSelected(true);
            cb.getStyleClass().add("algo-checkbox");
            algorithmCheckBoxes.put(algo, cb);
        }

        // 2. Sizes Selection (100, 500, 1000, 5000)
        int[] supportedSizes = {100, 500, 1000, 5000};
        for (int size : supportedSizes) {
            CheckBox cb = new CheckBox(String.valueOf(size));
            cb.setSelected(size <= 1000); // default 100, 500, 1000 selected
            cb.getStyleClass().add("algo-checkbox");
            sizeCheckBoxes.put(size, cb);
        }

        // 3. Pattern & Seed Controls
        this.patternComboBox = new ComboBox<>();
        this.patternComboBox.getItems().setAll(DatasetPattern.values());
        this.patternComboBox.setValue(DatasetPattern.RANDOM);
        this.patternComboBox.getStyleClass().add("algo-combo-box");

        this.seedTextField = new TextField();
        this.seedTextField.setPromptText("Seed (optional)");
        this.seedTextField.setPrefWidth(120);
        this.seedTextField.getStyleClass().add("seed-input");

        this.runBenchmarkButton = new Button("Run Benchmark ⚡");
        this.runBenchmarkButton.getStyleClass().addAll("btn", "btn-primary");

        this.progressBar = new ProgressBar(0);
        this.progressBar.setPrefWidth(180);
        this.progressBar.setVisible(false);

        this.progressLabel = new Label("Ready to run benchmark.");
        this.progressLabel.getStyleClass().add("status-prefix");

        this.warningLabel = new Label("Note: Large sizes (5,000) on O(n²) algorithms may take longer.");
        this.warningLabel.getStyleClass().add("benchmark-warning");

        // 4. Tables and Charts Setup
        this.resultsTable = createResultsTable();

        // Time Chart
        this.timeXAxis = new CategoryAxis();
        this.timeXAxis.setLabel("Algorithm");
        this.timeYAxis = new NumberAxis();
        this.timeYAxis.setLabel("Median Time (ms)");
        this.timeChart = new BarChart<>(timeXAxis, timeYAxis);
        this.timeChart.setTitle("Execution Time by Algorithm (Median of 5 Trials)");
        this.timeChart.getStyleClass().add("benchmark-chart");

        // Operations Chart
        this.opsXAxis = new CategoryAxis();
        this.opsXAxis.setLabel("Algorithm");
        this.opsYAxis = new NumberAxis();
        this.opsYAxis.setLabel("Operation Count");
        this.operationsChart = new BarChart<>(opsXAxis, opsYAxis);
        this.operationsChart.setTitle("Comparative Operation Counts");
        this.operationsChart.getStyleClass().add("benchmark-chart");

        // Insights banner
        this.insightsSummaryLabel = new Label("Configure parameters and click 'Run Benchmark' to generate empirical performance analysis.");
        this.insightsSummaryLabel.getStyleClass().add("insights-banner");

        // Results TabPane
        this.resultsTabPane = new TabPane();
        this.resultsTabPane.getStyleClass().add("benchmark-tab-pane");

        Tab tableTab = new Tab("Data Table", resultsTable);
        tableTab.setClosable(false);

        Tab timeChartTab = new Tab("Execution Time Chart", timeChart);
        timeChartTab.setClosable(false);

        Tab opsChartTab = new Tab("Operations Chart", operationsChart);
        opsChartTab.setClosable(false);

        resultsTabPane.getTabs().addAll(tableTab, timeChartTab, opsChartTab);

        // Layout Assembly
        setTop(buildTopSection());
        setCenter(buildCenterSection());
    }

    private VBox buildTopSection() {
        VBox topBox = new VBox(10);
        topBox.getStyleClass().add("top-container");
        topBox.setPadding(new Insets(14, 20, 10, 20));

        // Header
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("Algomorph — Empirical Benchmark Lab");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("Measure isolated algorithm execution times (System.nanoTime), operation metrics, and empirical scalability.");
        subtitle.getStyleClass().add("app-subtitle");
        titleBox.getChildren().addAll(title, subtitle);

        titleRow.getChildren().add(titleBox);

        // Config Row 1: Algorithms
        HBox algoRow = new HBox(10);
        algoRow.setAlignment(Pos.CENTER_LEFT);
        Label algoLabel = new Label("Target Algorithms:");
        algoLabel.getStyleClass().add("control-label");
        algoRow.getChildren().add(algoLabel);
        algoRow.getChildren().addAll(algorithmCheckBoxes.values());

        // Config Row 2: Sizes, Pattern, Seed, Run
        HBox paramRow = new HBox(12);
        paramRow.setAlignment(Pos.CENTER_LEFT);

        HBox sizeGroup = new HBox(6);
        sizeGroup.setAlignment(Pos.CENTER_LEFT);
        Label sizeLabel = new Label("Sizes (N):");
        sizeLabel.getStyleClass().add("control-label");
        sizeGroup.getChildren().add(sizeLabel);
        sizeGroup.getChildren().addAll(sizeCheckBoxes.values());

        HBox patternGroup = new HBox(6, new Label("Pattern:"), patternComboBox);
        patternGroup.setAlignment(Pos.CENTER_LEFT);

        HBox seedGroup = new HBox(6, new Label("Seed:"), seedTextField);
        seedGroup.setAlignment(Pos.CENTER_LEFT);

        paramRow.getChildren().addAll(sizeGroup, patternGroup, seedGroup, runBenchmarkButton);

        // Status & Progress Row
        HBox progressRow = new HBox(12, progressBar, progressLabel, warningLabel);
        progressRow.setAlignment(Pos.CENTER_LEFT);

        topBox.getChildren().addAll(titleRow, algoRow, paramRow, progressRow);
        return topBox;
    }

    private VBox buildCenterSection() {
        VBox centerBox = new VBox(8);
        centerBox.setPadding(new Insets(10, 20, 14, 20));
        VBox.setVgrow(resultsTabPane, Priority.ALWAYS);

        centerBox.getChildren().addAll(resultsTabPane, insightsSummaryLabel);
        return centerBox;
    }

    private TableView<BenchmarkResult> createResultsTable() {
        TableView<BenchmarkResult> table = new TableView<>();
        table.getStyleClass().add("comparison-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<BenchmarkResult, String> colAlgo = new TableColumn<>("Algorithm");
        colAlgo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAlgorithmName()));
        colAlgo.setPrefWidth(120);

        TableColumn<BenchmarkResult, Number> colSize = new TableColumn<>("Size (N)");
        colSize.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getDatasetSize()));
        colSize.setPrefWidth(85);

        TableColumn<BenchmarkResult, String> colPattern = new TableColumn<>("Pattern");
        colPattern.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPattern().getDisplayName()));
        colPattern.setPrefWidth(110);

        TableColumn<BenchmarkResult, Number> colTime = new TableColumn<>("Median Time");
        colTime.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getMedianTimeMillis()));
        colTime.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.doubleValue() < 0) {
                    setText("");
                } else {
                    double val = item.doubleValue();
                    if (val < 0.01) {
                        setText(String.format("%.3f ms (%.1f µs)", val, val * 1000));
                    } else {
                        setText(String.format("%.3f ms", val));
                    }
                }
            }
        });
        colTime.setPrefWidth(125);

        TableColumn<BenchmarkResult, Number> colComps = new TableColumn<>("Comparisons");
        colComps.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getComparisons()));
        colComps.setPrefWidth(110);

        TableColumn<BenchmarkResult, Number> colSwaps = new TableColumn<>("Swaps");
        colSwaps.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getSwaps()));
        colSwaps.setPrefWidth(90);

        TableColumn<BenchmarkResult, Number> colWrites = new TableColumn<>("Writes");
        colWrites.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getWrites()));
        colWrites.setPrefWidth(90);

        TableColumn<BenchmarkResult, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        colStatus.setPrefWidth(100);

        table.getColumns().addAll(colAlgo, colSize, colPattern, colTime, colComps, colSwaps, colWrites, colStatus);
        return table;
    }

    public void updateCharts(List<BenchmarkResult> results) {
        timeChart.getData().clear();
        operationsChart.getData().clear();

        if (results == null || results.isEmpty()) return;

        // Group by dataset size for time chart
        Map<Integer, XYChart.Series<String, Number>> sizeSeriesMap = new HashMap<>();

        for (BenchmarkResult res : results) {
            if (!"SUCCESS".equals(res.getStatus())) continue;

            int size = res.getDatasetSize();
            XYChart.Series<String, Number> series = sizeSeriesMap.computeIfAbsent(size, s -> {
                XYChart.Series<String, Number> ser = new XYChart.Series<>();
                ser.setName("N = " + s);
                return ser;
            });

            series.getData().add(new XYChart.Data<>(res.getAlgorithmName(), res.getMedianTimeMillis()));
        }

        timeChart.getData().addAll(sizeSeriesMap.values());

        // Operations chart: Grouped by metric for the primary/largest selected size
        int maxSelectedSize = results.stream().mapToInt(BenchmarkResult::getDatasetSize).max().orElse(100);

        XYChart.Series<String, Number> compsSeries = new XYChart.Series<>();
        compsSeries.setName("Comparisons (N=" + maxSelectedSize + ")");

        XYChart.Series<String, Number> swapsSeries = new XYChart.Series<>();
        swapsSeries.setName("Swaps (N=" + maxSelectedSize + ")");

        XYChart.Series<String, Number> writesSeries = new XYChart.Series<>();
        writesSeries.setName("Writes (N=" + maxSelectedSize + ")");

        for (BenchmarkResult res : results) {
            if (res.getDatasetSize() == maxSelectedSize && "SUCCESS".equals(res.getStatus())) {
                compsSeries.getData().add(new XYChart.Data<>(res.getAlgorithmName(), res.getComparisons()));
                swapsSeries.getData().add(new XYChart.Data<>(res.getAlgorithmName(), res.getSwaps()));
                writesSeries.getData().add(new XYChart.Data<>(res.getAlgorithmName(), res.getWrites()));
            }
        }

        operationsChart.getData().addAll(compsSeries, swapsSeries, writesSeries);
    }

    public void setRunning(boolean running) {
        runBenchmarkButton.setDisable(running);
        progressBar.setVisible(running);
        if (running) {
            progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        }
        patternComboBox.setDisable(running);
        seedTextField.setDisable(running);
        algorithmCheckBoxes.values().forEach(cb -> cb.setDisable(running));
        sizeCheckBoxes.values().forEach(cb -> cb.setDisable(running));
    }

    public void setProgress(double fraction, String status) {
        progressBar.setProgress(fraction);
        progressLabel.setText(status);
    }

    public void updateInsights(String insights) {
        insightsSummaryLabel.setText(insights);
    }

    // Getters
    public Map<SortingAlgorithm, CheckBox> getAlgorithmCheckBoxes() {
        return algorithmCheckBoxes;
    }

    public Map<Integer, CheckBox> getSizeCheckBoxes() {
        return sizeCheckBoxes;
    }

    public ComboBox<DatasetPattern> getPatternComboBox() {
        return patternComboBox;
    }

    public TextField getSeedTextField() {
        return seedTextField;
    }

    public Button getRunBenchmarkButton() {
        return runBenchmarkButton;
    }

    public TableView<BenchmarkResult> getResultsTable() {
        return resultsTable;
    }
}
