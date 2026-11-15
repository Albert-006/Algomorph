# Algomorph — Milestone 3: Algorithm Comparison Lab

## 1. Overview & Objectives

Milestone 3 elevates **Algomorph** from an individual algorithm viewer into an interactive algorithm comparison and empirical benchmarking suite. Users can:
1. **Compare Algorithms Side-by-Side:** Execute multiple algorithms (2 to 5) synchronously over identical datasets with fair, unified visual scaling.
2. **Synchronized Multi-Algorithm Playback:** Control Play, Pause, Step Forward, Reset, and Speed across all active algorithms simultaneously without locking the JavaFX Application Thread.
3. **Analyze Operation Counts & Winners:** Collect exact comparisons, swaps, writes, and elapsed preparation times in a live comparison table highlighting the best-performing algorithms.
4. **Empirical Benchmark Lab:** Measure headless sorting performance over configurable dataset sizes (100, 500, 1,000, 5,000) and patterns (Random, Sorted, Reverse, Nearly Sorted, Many Duplicates) using JVM warmups and median nanosecond timing.
5. **Interactive Charts & Data Insights:** Visualize execution times and operation counts via JavaFX BarCharts and review data-driven analytical insights.

---

## 2. Architecture & Design Decisions

### 2.1 Multi-Mode Navigation Layout
The application structure was extended with a clean top-level `TabPane`:
- **Tab 1: Single Visualizer (`SortingView`)** — Preserves 100% of Milestone 1 and 2 capabilities.
- **Tab 2: Compare Algorithms (`ComparisonView`)** — Synchronized multi-panel analysis.
- **Tab 3: Benchmark Lab (`BenchmarkView`)** — Empirical benchmarking and charts.

Tab transitions automatically pause active animation timelines to prevent resource contention or stale background executions.

### 2.2 Shared Dataset Generator (`DatasetGenerator.java`)
To guarantee fair comparisons:
- A single dataset is generated per trial and provided as defensive clones (`.clone()`) to each selected algorithm.
- Supports 5 statistical distributions:
  1. `RANDOM`: Uniformly distributed integers.
  2. `ALREADY_SORTED`: Monotonically non-decreasing sorted integers.
  3. `REVERSE_SORTED`: Monotonically non-increasing reverse-sorted integers.
  4. `NEARLY_SORTED`: Sorted sequence with controlled localized swaps (~6% of elements).
  5. `MANY_DUPLICATES`: Samples chosen from a restricted pool of 3–5 discrete values.
- Supports reproducible execution when an optional seed is provided (`new Random(seed)`).

### 2.3 Fair Visual Bar Scaling (`BarChartView.java`)
In multi-algorithm comparison, visual comparison is only fair if bar heights are proportionally identical across panels:
- `BarChartView` supports `setFixedMaxAbsValue(int maxVal)`.
- `ComparisonController` computes the shared maximum absolute value across the shared dataset and applies it to all active panels.

### 2.4 Synchronized Playback State Machine (`ComparisonController.java`)
- Sorting preparation runs in a background daemon thread (`ExecutorService`) so large arrays never freeze the UI.
- JavaFX `Timeline` coordinates synchronized stepping across all active algorithms.
- Algorithms finish at different step counts; completed panels transition to the `COMPLETED` state (green bars) and remain visible while remaining algorithms continue.
- When all algorithms complete, winner badges (`★ Fewest Comparisons`, `★ Fewest Writes`, `★ Fastest Prep`) are computed and data-driven insights are displayed.

### 2.5 Pure Benchmark Mode (`BenchmarkService.java`)
Visual event generation retains array clones for replay, which incurs $\mathcal{O}(n^2)$ memory for large datasets. To benchmark sizes up to 5,000 elements without memory exhaustion or GC skew:
- `SortingAlgorithm` defines `sortPure(int[] input)` for pure, in-place sorting without visual event allocations.
- `BenchmarkService` performs JVM warmup passes before recording timing.
- Measurements are taken using `System.nanoTime()` across 5 trials per configuration and the **median** time is reported.
- Operation counts (comparisons, swaps, writes) are counted separately from timing to isolate pure execution speed.
- Sorting correctness is verified against `Arrays.sort()` on every trial.

---

## 3. Operation Count Definitions

To ensure consistency across algorithms and documentation:

| Metric | Definition | Notes |
| :--- | :--- | :--- |
| **Comparisons** | Any pair-wise value evaluation between two array elements ($arr[i] \gtrless arr[j]$). | Evaluated in loops for all comparison-based algorithms. |
| **Swaps** | An in-place exchange of values between two distinct array indices. | Utilized by Bubble Sort, Selection Sort, and Quick Sort. |
| **Writes** | An assignment of a value into an array position ($arr[k] = val$). | Utilized by Merge Sort (buffer merge) and Insertion Sort (shifts and key placement). |
| **Total Events** | Discrete visualization snapshots generated for step-by-step replay. | May encompass comparisons, swaps, writes, min-selection, and partition ranges. |

---

## 4. Benchmark Methodology

1. **Dataset Generation:** Generated once per size/pattern trial and copied to all algorithms.
2. **Correctness Check:** Every algorithm output is validated using `Arrays.equals(result, referenceSorted)`. Any divergence flags `CORRUPTION_ERROR`.
3. **Warmup Phase:** 3 unmeasured warmup trials are executed per algorithm to allow HotSpot JIT compilation and steady-state optimization.
4. **Timing Trials:** 5 measured trials using `System.nanoTime()`. The median elapsed time is reported in milliseconds (or microseconds for sub-millisecond runs).
5. **UI & Overhead Isolation:** Timed code contains zero UI callbacks, artificial delays, or event object creations.
6. **Quadratic Safety Guard:** Datasets $> 10,000$ elements automatically skip $\mathcal{O}(n^2)$ algorithms with a clear `SKIPPED_TIMEOUT_RISK` status to prevent system hangs.

---

## 5. Automated Test Suite (77 Tests)

The test suite thoroughly exercises all layers:

1. **`AlgorithmComparisonTest` (5 tests):**
   - Verified that all 5 algorithms produce identical sorted output on random arrays.
   - Preserved theoretical complexity metadata across all algorithms.
   - Tested dynamic switching between algorithms.
   - Verified that `sortPure` produces identical outputs as full `sort()` across all 5 dataset patterns.
   - Verified that all 5 algorithms sort identical copies across all patterns without mutating caller inputs.
2. **`DatasetGeneratorTest` (11 tests):**
   - Verified correct lengths across all patterns.
   - Verified deterministic seed reproducibility (`arr1 == arr2`).
   - Verified different seeds generate distinct arrays.
   - Tested sortedness of `ALREADY_SORTED` and `REVERSE_SORTED`.
   - Verified value pool constraint of `MANY_DUPLICATES`.
   - Handled edge cases: $N=0$, $N=1$, negative size exceptions, null pattern exceptions, inverted bounds.
3. **`BenchmarkServiceTest` (6 tests):**
   - Verified multi-size benchmark execution, timing validity, and progress callback progression to 1.0.
   - Verified operation counting conventions across Merge Sort, Bubble Sort, and Insertion Sort.
   - Verified rejection of empty algorithms or sizes.
   - Handled negative size errors with status reporting.
   - Verified deterministic operation counts with fixed seeds.
4. **Existing Regression Suites (55 tests):**
   - `BubbleSortTest` (11 tests)
   - `SelectionSortTest` (11 tests)
   - `InsertionSortTest` (11 tests)
   - `MergeSortTest` (11 tests)
   - `QuickSortTest` (11 tests)
