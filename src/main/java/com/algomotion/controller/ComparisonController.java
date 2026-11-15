package com.algomotion.controller;

import com.algomotion.algorithm.SortingAlgorithm;
import com.algomotion.model.ComparisonRowModel;
import com.algomotion.model.DatasetGenerator;
import com.algomotion.model.DatasetPattern;
import com.algomotion.model.SortEvent;
import com.algomotion.model.SortResult;
import com.algomotion.view.AlgorithmComparisonPanel;
import com.algomotion.view.ComparisonView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller orchestrating multi-algorithm synchronization, background preparation,
 * shared timeline playback, and comparison metric aggregation.
 */
public class ComparisonController {

    public enum PlaybackState {
        READY,
        RUNNING,
        PAUSED,
        COMPLETED
    }

    private final List<SortingAlgorithm> allAlgorithms;
    private final ComparisonView view;
    private final ExecutorService backgroundExecutor;

    private int[] sharedInputArray;
    private int sharedArraySize = 20;
    private DatasetPattern currentPattern = DatasetPattern.RANDOM;
    private Long customSeed = null;

    private final List<SortingAlgorithm> activeAlgorithms = new ArrayList<>();
    private final Map<SortingAlgorithm, SortResult> algorithmResults = new HashMap<>();
    private final Map<SortingAlgorithm, Integer> algorithmCurrentSteps = new HashMap<>();
    private final Map<SortingAlgorithm, AlgorithmComparisonPanel> algorithmPanels = new HashMap<>();
    private final Map<SortingAlgorithm, ComparisonRowModel> tableRowModels = new HashMap<>();

    private PlaybackState state = PlaybackState.READY;
    private Timeline timeline;
    private double speedDelayMs = 100.0;

    public ComparisonController(List<SortingAlgorithm> allAlgorithms, ComparisonView view) {
        this.allAlgorithms = List.copyOf(Objects.requireNonNull(allAlgorithms, "allAlgorithms must not be null"));
        this.view = Objects.requireNonNull(view, "view must not be null");

        this.backgroundExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "Algomorph-Comparison-Worker");
            t.setDaemon(true);
            return t;
        });

        // Initialize with all algorithms active
        this.activeAlgorithms.addAll(this.allAlgorithms);

        setupEventHandlers();
        generateNewDataset();
    }

    private void setupEventHandlers() {
        // Playback buttons
        view.getPlayPauseButton().setOnAction(e -> handlePlayPause());
        view.getStepButton().setOnAction(e -> stepForward());
        view.getResetButton().setOnAction(e -> reset());
        view.getRestartButton().setOnAction(e -> restart());

        // New dataset button
        view.getNewDatasetButton().setOnAction(e -> generateNewDataset());

        // Pattern selector
        view.getPatternComboBox().valueProperty().addListener((obs, oldPat, newPat) -> {
            if (newPat != null && newPat != currentPattern) {
                this.currentPattern = newPat;
                generateNewDataset();
            }
        });

        // Size slider
        view.getSizeSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            int newSize = newVal.intValue();
            view.getSizeValueLabel().setText(String.valueOf(newSize));
            if (newSize != sharedArraySize) {
                this.sharedArraySize = newSize;
                generateNewDataset();
            }
        });

        // Speed slider
        view.getSpeedSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            this.speedDelayMs = newVal.doubleValue();
            view.getSpeedValueLabel().setText(String.format("%.0f ms", speedDelayMs));
            if (state == PlaybackState.RUNNING) {
                startTimeline();
            }
        });

        // Algorithm Checkboxes
        for (SortingAlgorithm algo : allAlgorithms) {
            view.getAlgorithmCheckBoxes().get(algo).selectedProperty().addListener((obs, wasSel, isSel) -> {
                onAlgorithmCheckboxToggled(algo, isSel);
            });
        }
    }

    private void onAlgorithmCheckboxToggled(SortingAlgorithm algo, boolean isSelected) {
        if (!isSelected && activeAlgorithms.size() <= 2) {
            // Prevent deselecting below the minimum requirement of 2
            view.getAlgorithmCheckBoxes().get(algo).setSelected(true);
            view.updateInsights("At least 2 algorithms must remain active for side-by-side comparison.");
            return;
        }

        stopTimeline();
        if (isSelected && !activeAlgorithms.contains(algo)) {
            activeAlgorithms.add(algo);
        } else if (!isSelected) {
            activeAlgorithms.remove(algo);
        }

        prepareComparisonData();
    }

    public void handlePlayPause() {
        if (state == PlaybackState.RUNNING) {
            pause();
        } else {
            play();
        }
    }

    public void play() {
        if (state == PlaybackState.COMPLETED) {
            reset();
        }

        this.state = PlaybackState.RUNNING;
        view.setPlayButtonRunning(true);
        view.setPlaybackControlsDisabled(true);

        for (SortingAlgorithm algo : activeAlgorithms) {
            int step = algorithmCurrentSteps.getOrDefault(algo, 0);
            SortResult res = algorithmResults.get(algo);
            if (res != null && step < res.events().size()) {
                algorithmPanels.get(algo).updateStatus("SORTING", "badge-running");
                tableRowModels.get(algo).setStatus("SORTING");
            }
        }

        startTimeline();
    }

    public void pause() {
        stopTimeline();
        if (state == PlaybackState.RUNNING) {
            this.state = PlaybackState.PAUSED;
            view.setPlayButtonRunning(false);
            view.setPlaybackControlsDisabled(false);

            for (SortingAlgorithm algo : activeAlgorithms) {
                int step = algorithmCurrentSteps.getOrDefault(algo, 0);
                SortResult res = algorithmResults.get(algo);
                if (res != null && step < res.events().size()) {
                    algorithmPanels.get(algo).updateStatus("PAUSED", "badge-paused");
                    tableRowModels.get(algo).setStatus("PAUSED");
                }
            }
        }
    }

    public void stepForward() {
        stopTimeline();
        if (state == PlaybackState.COMPLETED) {
            return;
        }

        executeSingleSynchronizedStep();

        if (state != PlaybackState.COMPLETED) {
            this.state = PlaybackState.PAUSED;
            view.setPlayButtonRunning(false);
            view.setPlaybackControlsDisabled(false);
        }
    }

    public void reset() {
        stopTimeline();
        this.state = PlaybackState.READY;
        view.setPlayButtonRunning(false);
        view.setPlaybackControlsDisabled(false);

        int maxAbs = computeMaxAbs(sharedInputArray);

        for (SortingAlgorithm algo : activeAlgorithms) {
            algorithmCurrentSteps.put(algo, 0);
            SortResult res = algorithmResults.get(algo);
            AlgorithmComparisonPanel panel = algorithmPanels.get(algo);
            if (panel != null && res != null) {
                panel.setFixedMaxAbsValue(maxAbs);
                panel.displayInitialState(sharedInputArray, res.events().size());
            }

            ComparisonRowModel row = tableRowModels.get(algo);
            if (row != null && res != null) {
                row.setComparisons(0);
                row.setSwaps(0);
                row.setWrites(0);
                row.setTotalEvents(res.events().size());
                row.setStatus("READY");
                row.setHighlights("");
            }
        }

        view.updateInsights("Reset complete. Click 'Play' or 'Next Step' to begin synchronized analysis.");
    }

    public void restart() {
        reset();
        play();
    }

    public void generateNewDataset() {
        stopTimeline();

        // Parse optional seed
        String seedText = view.getSeedTextField().getText().trim();
        try {
            this.customSeed = seedText.isEmpty() ? null : Long.parseLong(seedText);
        } catch (NumberFormatException e) {
            this.customSeed = null;
        }

        this.sharedInputArray = DatasetGenerator.generate(sharedArraySize, currentPattern, customSeed);
        prepareComparisonData();
    }

    /**
     * Executes sorting in background worker, then publishes prepared results to UI thread.
     */
    private void prepareComparisonData() {
        stopTimeline();
        this.state = PlaybackState.READY;
        view.setPlayButtonRunning(false);
        view.setPlaybackControlsDisabled(false);

        final int[] datasetCopy = sharedInputArray.clone();
        final List<SortingAlgorithm> targetAlgos = List.copyOf(activeAlgorithms);

        backgroundExecutor.submit(() -> {
            Map<SortingAlgorithm, SortResult> results = new HashMap<>();
            for (SortingAlgorithm algo : targetAlgos) {
                SortResult res = algo.sort(datasetCopy);
                results.put(algo, res);
            }

            Platform.runLater(() -> applyPreparedResults(results, datasetCopy));
        });
    }

    private void applyPreparedResults(Map<SortingAlgorithm, SortResult> results, int[] dataset) {
        algorithmResults.clear();
        algorithmResults.putAll(results);
        algorithmCurrentSteps.clear();
        algorithmPanels.clear();
        tableRowModels.clear();

        int maxAbs = computeMaxAbs(dataset);
        List<AlgorithmComparisonPanel> panelsList = new ArrayList<>();
        List<ComparisonRowModel> rowsList = new ArrayList<>();

        for (SortingAlgorithm algo : activeAlgorithms) {
            algorithmCurrentSteps.put(algo, 0);
            SortResult res = algorithmResults.get(algo);

            AlgorithmComparisonPanel panel = new AlgorithmComparisonPanel(algo);
            panel.setFixedMaxAbsValue(maxAbs);
            if (res != null) {
                panel.displayInitialState(dataset, res.events().size());
            }
            algorithmPanels.put(algo, panel);
            panelsList.add(panel);

            ComparisonRowModel row = new ComparisonRowModel(
                    algo.getName(),
                    0, 0, 0,
                    res != null ? res.events().size() : 0,
                    res != null ? res.getExecutionTimeMillis() : 0.0,
                    "READY",
                    ""
            );
            tableRowModels.put(algo, row);
            rowsList.add(row);
        }

        view.arrangePanels(panelsList);
        view.getResultsTable().getItems().setAll(rowsList);
        view.updateInsights(String.format("Shared dataset initialized with %d elements (%s). All panels scaled consistently.",
                dataset.length, currentPattern.getDisplayName()));
    }

    private void executeSingleSynchronizedStep() {
        boolean allFinished = true;

        for (SortingAlgorithm algo : activeAlgorithms) {
            SortResult res = algorithmResults.get(algo);
            if (res == null) continue;

            int currentStep = algorithmCurrentSteps.getOrDefault(algo, 0);
            AlgorithmComparisonPanel panel = algorithmPanels.get(algo);
            ComparisonRowModel row = tableRowModels.get(algo);

            if (currentStep < res.events().size()) {
                allFinished = false;
                SortEvent event = res.events().get(currentStep);
                currentStep++;
                algorithmCurrentSteps.put(algo, currentStep);

                panel.displayEvent(event, currentStep, res.events().size(), sharedInputArray.length);

                if (row != null) {
                    row.setComparisons(event.getComparisonsSoFar());
                    row.setSwaps(event.getSwapsSoFar());
                    row.setWrites(event.getWritesSoFar());
                    row.setStatus("SORTING");
                }

                if (currentStep >= res.events().size() || event.getType() == SortEvent.Type.COMPLETED) {
                    panel.displayCompleted(res.sortedArray(), res.statistics());
                    if (row != null) {
                        row.setStatus("COMPLETED");
                    }
                }
            } else {
                panel.updateStatus("COMPLETED", "badge-completed");
                if (row != null) {
                    row.setStatus("COMPLETED");
                }
            }
        }

        if (allFinished) {
            onAllAlgorithmsCompleted();
        }
    }

    private void onAllAlgorithmsCompleted() {
        stopTimeline();
        this.state = PlaybackState.COMPLETED;
        view.setPlayButtonRunning(false);
        view.setPlaybackControlsDisabled(false);

        computeAnalysisAndHighlights();
    }

    private void computeAnalysisAndHighlights() {
        if (activeAlgorithms.isEmpty()) return;

        // Determine best performers
        SortingAlgorithm minComparisonsAlgo = null;
        int minComps = Integer.MAX_VALUE;

        SortingAlgorithm minWritesAlgo = null;
        int minWrites = Integer.MAX_VALUE;

        SortingAlgorithm minTimeAlgo = null;
        double minTime = Double.MAX_VALUE;

        for (SortingAlgorithm algo : activeAlgorithms) {
            SortResult res = algorithmResults.get(algo);
            if (res != null) {
                int comps = res.statistics().comparisons();
                if (comps < minComps) {
                    minComps = comps;
                    minComparisonsAlgo = algo;
                }

                int writes = res.statistics().writes();
                if (writes < minWrites) {
                    minWrites = writes;
                    minWritesAlgo = algo;
                }

                double time = res.getExecutionTimeMillis();
                if (time < minTime) {
                    minTime = time;
                    minTimeAlgo = algo;
                }
            }
        }

        // Apply badges
        for (SortingAlgorithm algo : activeAlgorithms) {
            ComparisonRowModel row = tableRowModels.get(algo);
            if (row == null) continue;

            List<String> badges = new ArrayList<>();
            if (algo == minComparisonsAlgo) badges.add("★ Fewest Comparisons (" + minComps + ")");
            if (algo == minWritesAlgo && minWrites > 0) badges.add("★ Fewest Writes (" + minWrites + ")");
            if (algo == minTimeAlgo) badges.add("★ Fastest Prep (" + String.format("%.2f", minTime) + " ms)");

            row.setHighlights(String.join(" • ", badges));
        }

        // Generate data-driven educational insights text
        StringBuilder insights = new StringBuilder();
        if (minComparisonsAlgo != null) {
            insights.append(String.format("Result: %s performed the fewest comparisons (%d). ",
                    minComparisonsAlgo.getName(), minComps));
        }
        if (currentPattern == DatasetPattern.ALREADY_SORTED) {
            insights.append("On already-sorted input, algorithms with early termination (Bubble/Insertion) perform in linear O(n) comparisons.");
        } else if (currentPattern == DatasetPattern.REVERSE_SORTED) {
            insights.append("On reverse-sorted input, divide-and-conquer algorithms (Merge Sort) significantly outperform quadratic algorithms.");
        } else {
            insights.append("Observe how Merge Sort uses pure writes while Bubble/Selection/Quick rely primarily on swaps.");
        }

        view.updateInsights(insights.toString());
    }

    private void startTimeline() {
        stopTimeline();
        timeline = new Timeline(new KeyFrame(Duration.millis(speedDelayMs), e -> {
            if (state == PlaybackState.RUNNING) {
                executeSingleSynchronizedStep();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void stopTimeline() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    private int computeMaxAbs(int[] arr) {
        int max = 1;
        for (int v : arr) {
            max = Math.max(max, Math.abs(v));
        }
        return max;
    }

    // Diagnostics / testing accessors
    public PlaybackState getState() {
        return state;
    }

    public List<SortingAlgorithm> getActiveAlgorithms() {
        return List.copyOf(activeAlgorithms);
    }

    public int[] getSharedInputArray() {
        return sharedInputArray != null ? sharedInputArray.clone() : new int[0];
    }

    public void shutdown() {
        stopTimeline();
        backgroundExecutor.shutdownNow();
    }
}
