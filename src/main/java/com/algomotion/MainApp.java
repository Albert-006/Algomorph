package com.algomotion;

import com.algomotion.algorithm.BubbleSort;
import com.algomotion.algorithm.InsertionSort;
import com.algomotion.algorithm.MergeSort;
import com.algomotion.algorithm.QuickSort;
import com.algomotion.algorithm.SelectionSort;
import com.algomotion.algorithm.SortingAlgorithm;
import com.algomotion.controller.BenchmarkController;
import com.algomotion.controller.ComparisonController;
import com.algomotion.controller.SortingController;
import com.algomotion.view.BenchmarkView;
import com.algomotion.view.ComparisonView;
import com.algomotion.view.SortingView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;

/**
 * Main application entry point for Algomorph.
 * Boots the JavaFX runtime and arranges the three primary workspaces:
 * 1. Single Algorithm Visualizer
 * 2. Multi-Algorithm Comparison Lab
 * 3. Empirical Benchmark Lab
 */
public class MainApp extends Application {

    private SortingController singleController;
    private ComparisonController comparisonController;
    private BenchmarkController benchmarkController;

    @Override
    public void start(Stage primaryStage) {
        // Initialize all 5 supported sorting algorithms
        List<SortingAlgorithm> algorithms = List.of(
                new BubbleSort(),
                new SelectionSort(),
                new InsertionSort(),
                new MergeSort(),
                new QuickSort()
        );

        // 1. Single Visualizer Mode
        SortingView sortingView = new SortingView();
        singleController = new SortingController(algorithms, sortingView);

        // 2. Multi-Algorithm Comparison Mode
        ComparisonView comparisonView = new ComparisonView(algorithms);
        comparisonController = new ComparisonController(algorithms, comparisonView);

        // 3. Empirical Benchmark Lab
        BenchmarkView benchmarkView = new BenchmarkView(algorithms);
        benchmarkController = new BenchmarkController(algorithms, benchmarkView);

        // Top-Level Navigation Tabs
        TabPane rootTabPane = new TabPane();
        rootTabPane.getStyleClass().add("main-tab-pane");

        Tab tabSingle = new Tab("Single Visualizer", sortingView);
        tabSingle.setClosable(false);

        Tab tabCompare = new Tab("Compare Algorithms", comparisonView);
        tabCompare.setClosable(false);

        Tab tabBenchmark = new Tab("Benchmark Lab", benchmarkView);
        tabBenchmark.setClosable(false);

        rootTabPane.getTabs().addAll(tabSingle, tabCompare, tabBenchmark);

        // Clean lifecycle: Pause active animations when switching away from a tab
        rootTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (oldTab == tabSingle && singleController != null) {
                singleController.pause();
            } else if (oldTab == tabCompare && comparisonController != null) {
                comparisonController.pause();
            }
        });

        // Build Scene
        Scene scene = new Scene(rootTabPane, 1140, 760);

        // Load CSS styling
        URL stylesheetUrl = getClass().getResource("/styles/app.css");
        if (stylesheetUrl != null) {
            scene.getStylesheets().add(stylesheetUrl.toExternalForm());
        }

        // Configure Window / Stage
        primaryStage.setTitle("Algomorph — Transforming Algorithms into Visual Experiences");
        primaryStage.setMinWidth(920);
        primaryStage.setMinHeight(640);
        primaryStage.setScene(scene);

        // Clean up any background timers/threads when window closes
        primaryStage.setOnCloseRequest(event -> {
            if (singleController != null) singleController.pause();
            if (comparisonController != null) comparisonController.shutdown();
            if (benchmarkController != null) benchmarkController.shutdown();
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
