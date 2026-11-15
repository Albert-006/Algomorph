package com.algomotion.controller;

import com.algomotion.algorithm.BubbleSort;
import com.algomotion.algorithm.InsertionSort;
import com.algomotion.algorithm.MergeSort;
import com.algomotion.algorithm.QuickSort;
import com.algomotion.algorithm.SelectionSort;
import com.algomotion.algorithm.SortingAlgorithm;
import com.algomotion.model.SortEvent;
import com.algomotion.model.SortResult;
import com.algomotion.view.SortingView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Controller coordinating algorithm execution, UI playback, and user interactions.
 *
 * Implements a robust state machine (READY, RUNNING, PAUSED, COMPLETED) and uses
 * a JavaFX {@link Timeline} to ensure non-blocking, periodic playback on the JavaFX Application Thread.
 */
public class SortingController {

    public enum PlaybackState {
        READY,
        RUNNING,
        PAUSED,
        COMPLETED
    }

    private final List<SortingAlgorithm> algorithms;
    private final SortingView view;
    private final Random random;

    private SortingAlgorithm currentAlgorithm;
    private int[] initialArray;
    private SortResult currentResult;
    private int currentEventIndex;
    private PlaybackState state;

    private Timeline timeline;
    private double speedDelayMs = 100.0;
    private int arraySize = 20;

    /**
     * Primary constructor accepting a list of available algorithms and the view.
     */
    public SortingController(List<SortingAlgorithm> algorithms, SortingView view) {
        if (algorithms == null || algorithms.isEmpty()) {
            throw new IllegalArgumentException("Algorithms list must not be empty");
        }
        this.algorithms = List.copyOf(algorithms);
        this.view = Objects.requireNonNull(view, "SortingView must not be null");
        this.random = new Random();
        this.state = PlaybackState.READY;
        this.currentAlgorithm = this.algorithms.getFirst();

        setupAlgorithms();
        setupEventHandlers();
        randomize();
    }

    /**
     * Backward-compatible convenience constructor.
     */
    public SortingController(BubbleSort bubbleSort, SortingView view) {
        this(List.of(
                bubbleSort != null ? bubbleSort : new BubbleSort(),
                new SelectionSort(),
                new InsertionSort(),
                new MergeSort(),
                new QuickSort()
        ), view);
    }

    private void setupAlgorithms() {
        view.getAlgorithmSelector().getItems().setAll(algorithms);
        view.getAlgorithmSelector().setValue(currentAlgorithm);
        view.updateAlgorithmInfo(currentAlgorithm);

        view.getAlgorithmSelector().valueProperty().addListener((obs, oldAlgo, newAlgo) -> {
            if (newAlgo != null && newAlgo != currentAlgorithm) {
                switchAlgorithm(newAlgo);
            }
        });
    }

    private void setupEventHandlers() {
        // Play / Pause toggle
        view.getPlayPauseButton().setOnAction(e -> handlePlayPause());

        // Step forward
        view.getStepButton().setOnAction(e -> stepForward());

        // Reset to beginning of current array
        view.getResetButton().setOnAction(e -> reset());

        // Generate new random array
        view.getRandomizeButton().setOnAction(e -> randomize());

        // Array size slider
        view.getSizeSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            int newSize = newVal.intValue();
            view.getSizeValueDisplayLabel().setText(String.valueOf(newSize));
            if (newSize != arraySize) {
                this.arraySize = newSize;
                randomize();
            }
        });

        // Speed / Delay slider
        view.getSpeedSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            this.speedDelayMs = newVal.doubleValue();
            view.getSpeedValueLabel().setText(String.format("%.0f ms", speedDelayMs));

            // Dynamically adjust running animation speed without requiring pause
            if (state == PlaybackState.RUNNING) {
                startTimeline();
            }
        });
    }

    public void handlePlayPause() {
        if (state == PlaybackState.RUNNING) {
            pause();
        } else {
            play();
        }
    }

    /**
     * Switches the active algorithm, stops playback, and prepares a new event queue.
     */
    public void switchAlgorithm(SortingAlgorithm newAlgorithm) {
        stopTimeline();
        this.currentAlgorithm = Objects.requireNonNull(newAlgorithm);
        view.updateAlgorithmInfo(currentAlgorithm);

        if (initialArray != null) {
            this.currentResult = currentAlgorithm.sort(initialArray);
            this.currentEventIndex = 0;
            this.state = PlaybackState.READY;
            view.displayInitialState(initialArray, currentResult.events().size());
        }
    }

    /**
     * Starts or resumes playback of sorting events.
     */
    public void play() {
        if (state == PlaybackState.COMPLETED) {
            // Replay from start if finished
            reset();
        }

        this.state = PlaybackState.RUNNING;
        view.updateStatus("SORTING...", "badge-running");
        view.setPlayButtonRunning(true);
        view.setPlaybackControlsDisabled(true);

        startTimeline();
    }

    /**
     * Pauses active playback and allows step or manual control.
     */
    public void pause() {
        stopTimeline();
        if (state == PlaybackState.RUNNING) {
            this.state = PlaybackState.PAUSED;
            view.updateStatus("PAUSED", "badge-paused");
            view.setPlayButtonRunning(false);
            view.setPlaybackControlsDisabled(false);
        }
    }

    /**
     * Advances exactly one sorting event and ensures the player remains paused.
     */
    public void stepForward() {
        stopTimeline();

        if (state == PlaybackState.COMPLETED) {
            return;
        }

        executeSingleStep();

        if (state != PlaybackState.COMPLETED) {
            this.state = PlaybackState.PAUSED;
            view.updateStatus("PAUSED", "badge-paused");
            view.setPlayButtonRunning(false);
            view.setPlaybackControlsDisabled(false);
        }
    }

    /**
     * Resets the visualizer to the original unsorted array state.
     * Cancels any active animation timeline.
     */
    public void reset() {
        stopTimeline();
        this.currentEventIndex = 0;
        this.currentResult = currentAlgorithm.sort(initialArray);
        this.state = PlaybackState.READY;

        int totalEvents = currentResult.events().size();
        view.displayInitialState(initialArray, totalEvents);
    }

    /**
     * Generates a new random array, cancels active playback, and prepares a new event queue.
     */
    public void randomize() {
        stopTimeline();
        this.initialArray = generateRandomArray(arraySize);
        this.currentResult = currentAlgorithm.sort(initialArray);
        this.currentEventIndex = 0;
        this.state = PlaybackState.READY;

        int totalEvents = currentResult.events().size();
        view.displayInitialState(initialArray, totalEvents);
    }

    private void executeSingleStep() {
        if (currentResult == null || currentResult.events().isEmpty()) {
            return;
        }

        if (currentEventIndex >= currentResult.events().size()) {
            onSortingFinished();
            return;
        }

        SortEvent event = currentResult.events().get(currentEventIndex);
        currentEventIndex++;

        int totalEvents = currentResult.events().size();
        view.displayEvent(event, currentEventIndex, totalEvents, initialArray.length);

        if (event.getType() == SortEvent.Type.COMPLETED || currentEventIndex >= totalEvents) {
            onSortingFinished();
        }
    }

    private void onSortingFinished() {
        stopTimeline();
        this.state = PlaybackState.COMPLETED;
        view.displayCompleted(currentResult.sortedArray(), currentResult.statistics());
    }

    private void startTimeline() {
        stopTimeline();

        timeline = new Timeline(new KeyFrame(Duration.millis(speedDelayMs), event -> {
            if (state == PlaybackState.RUNNING) {
                executeSingleStep();
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

    /**
     * Generates random integers between 5 and 100 for visual clarity.
     */
    private int[] generateRandomArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            // Values between 5 and 100
            arr[i] = 5 + random.nextInt(96);
        }
        return arr;
    }

    // --- State Getters for Diagnostics & Testing ---

    public PlaybackState getState() {
        return state;
    }

    public int getCurrentEventIndex() {
        return currentEventIndex;
    }

    public int[] getInitialArray() {
        return initialArray.clone();
    }

    public SortingAlgorithm getCurrentAlgorithm() {
        return currentAlgorithm;
    }

    public List<SortingAlgorithm> getAlgorithms() {
        return algorithms;
    }

    public SortResult getCurrentResult() {
        return currentResult;
    }
}
