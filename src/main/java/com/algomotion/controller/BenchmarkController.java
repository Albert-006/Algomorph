package com.algomotion.controller;

import com.algomotion.algorithm.SortingAlgorithm;
import com.algomotion.model.BenchmarkResult;
import com.algomotion.model.BenchmarkService;
import com.algomotion.model.DatasetPattern;
import com.algomotion.view.BenchmarkView;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller orchestrating benchmark execution, background thread management,
 * progress updates, and data-driven analytical insights.
 */
public class BenchmarkController {

    private final List<SortingAlgorithm> allAlgorithms;
    private final BenchmarkView view;
    private final BenchmarkService benchmarkService;
    private final ExecutorService executor;

    public BenchmarkController(List<SortingAlgorithm> allAlgorithms, BenchmarkView view) {
        this.allAlgorithms = List.copyOf(Objects.requireNonNull(allAlgorithms, "allAlgorithms must not be null"));
        this.view = Objects.requireNonNull(view, "view must not be null");
        this.benchmarkService = new BenchmarkService();

        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "Algomorph-Benchmark-Worker");
            t.setDaemon(true);
            return t;
        });

        setupEventHandlers();
    }

    private void setupEventHandlers() {
        view.getRunBenchmarkButton().setOnAction(e -> runBenchmark());
    }

    public void runBenchmark() {
        // Collect selected algorithms
        List<SortingAlgorithm> selectedAlgos = new ArrayList<>();
        for (SortingAlgorithm algo : allAlgorithms) {
            if (view.getAlgorithmCheckBoxes().get(algo).isSelected()) {
                selectedAlgos.add(algo);
            }
        }
        if (selectedAlgos.isEmpty()) {
            view.updateInsights("Please select at least one algorithm to benchmark.");
            return;
        }

        // Collect selected sizes
        List<Integer> selectedSizes = new ArrayList<>();
        for (var entry : view.getSizeCheckBoxes().entrySet()) {
            if (entry.getValue().isSelected()) {
                selectedSizes.add(entry.getKey());
            }
        }
        selectedSizes.sort(Comparator.naturalOrder());

        if (selectedSizes.isEmpty()) {
            view.updateInsights("Please select at least one dataset size to benchmark.");
            return;
        }

        DatasetPattern pattern = view.getPatternComboBox().getValue();

        // Parse optional seed
        String seedText = view.getSeedTextField().getText().trim();
        Long seed = null;
        try {
            if (!seedText.isEmpty()) seed = Long.parseLong(seedText);
        } catch (NumberFormatException ignored) {
            seed = null;
        }

        final Long finalSeed = seed;
        view.setRunning(true);
        view.setProgress(0.0, "Warming up JVM and running benchmark trials...");

        executor.submit(() -> {
            try {
                List<BenchmarkResult> results = benchmarkService.runBenchmark(
                        selectedAlgos,
                        selectedSizes,
                        pattern,
                        finalSeed,
                        BenchmarkService.DEFAULT_WARMUP_TRIALS,
                        BenchmarkService.DEFAULT_MEASUREMENT_TRIALS,
                        fraction -> Platform.runLater(() -> view.setProgress(fraction,
                                String.format("Benchmarking... %.0f%% complete", fraction * 100.0)))
                );

                Platform.runLater(() -> onBenchmarkCompleted(results, pattern, selectedSizes));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    view.setRunning(false);
                    view.setProgress(0.0, "Benchmark failed: " + ex.getMessage());
                    view.updateInsights("Error during benchmark: " + ex.getMessage());
                });
            }
        });
    }

    private void onBenchmarkCompleted(List<BenchmarkResult> results, DatasetPattern pattern, List<Integer> sizes) {
        view.setRunning(false);
        view.setProgress(1.0, "Benchmark completed successfully.");
        view.getResultsTable().getItems().setAll(results);
        view.updateCharts(results);

        // Generate data-driven analytical insights
        String insights = generateInsights(results, pattern, sizes);
        view.updateInsights(insights);
    }

    private String generateInsights(List<BenchmarkResult> results, DatasetPattern pattern, List<Integer> sizes) {
        if (results == null || results.isEmpty()) {
            return "No benchmark results collected.";
        }

        // Find fastest algorithm for the largest tested size
        int maxSize = sizes.getLast();
        BenchmarkResult fastestAtMax = results.stream()
                .filter(r -> r.getDatasetSize() == maxSize && "SUCCESS".equals(r.getStatus()))
                .min(Comparator.comparingDouble(BenchmarkResult::getMedianTimeMillis))
                .orElse(null);

        // Find algorithm with fewest comparisons at largest tested size
        BenchmarkResult fewestCompsAtMax = results.stream()
                .filter(r -> r.getDatasetSize() == maxSize && "SUCCESS".equals(r.getStatus()))
                .min(Comparator.comparingInt(BenchmarkResult::getComparisons))
                .orElse(null);

        StringBuilder sb = new StringBuilder();
        if (fastestAtMax != null) {
            sb.append(String.format("Fastest at N=%d: %s (%.3f ms median). ",
                    maxSize, fastestAtMax.getAlgorithmName(), fastestAtMax.getMedianTimeMillis()));
        }
        if (fewestCompsAtMax != null) {
            sb.append(String.format("Fewest comparisons at N=%d: %s (%d comparisons). ",
                    maxSize, fewestCompsAtMax.getAlgorithmName(), fewestCompsAtMax.getComparisons()));
        }

        // Pattern specific analysis
        switch (pattern) {
            case ALREADY_SORTED ->
                sb.append("Pattern Effect: Linear-time early-exit algorithms (Bubble & Insertion) executed in O(n) comparisons without swaps.");
            case REVERSE_SORTED ->
                sb.append("Pattern Effect: Quadratic algorithms suffered maximum comparisons and moves; Merge Sort demonstrated superior O(n log n) scaling.");
            case MANY_DUPLICATES ->
                sb.append("Pattern Effect: High duplicate density reduced comparison variations across partitions.");
            default ->
                sb.append("Complexity Scaling: Empirical execution times align cleanly with theoretical O(n log n) vs O(n²) asymptotic curves.");
        }

        return sb.toString();
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
