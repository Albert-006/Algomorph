# Algomorph

> *Transforming Algorithms into Visual Experiences.*

Algomorph is an interactive algorithm visualization and learning platform built with Java and JavaFX. It allows users to visualize sorting algorithms, compare their performance, understand execution steps, and explore algorithmic concepts through interactive animations and statistics.

---

## Key Features

- **Interactive Single-Algorithm Visualizer:** Step-by-step inspection of sorting algorithms with color-coded states (comparing, swapping, shifting, writing, pivot selection, and sorted).
- **Synchronized Multi-Algorithm Comparison Lab:** Run between 2 and 5 algorithms simultaneously on identical datasets with fair, unified vertical bar scaling.
- **Live Operation Metrics:** Real-time tracking of comparisons, swaps, array writes, total steps, and preparation duration.
- **Empirical Benchmark Lab:** Headless execution measuring wall-clock performance across dataset sizes (100 to 5,000 items) with JVM warmups and multi-trial median nanosecond timing.
- **Visual Analytics:** Interactive JavaFX bar charts displaying execution times and comparative operation counts, supplemented with data-driven insights.
- **Configurable Dataset Patterns:** Built-in generators for Random, Already Sorted, Reverse Sorted, Nearly Sorted, and Many Duplicates datasets, including optional custom seed support for deterministic reproducibility.
- **Full Playback Controls:** Play, pause, single-step forward, reset, restart, variable animation speed, and adjustable array size.
- **Decoupled Architecture:** Algorithm implementations are completely independent of JavaFX, allowing isolated testing and headless benchmarking.

---

## Supported Algorithms

| Algorithm | Best Time | Average Time | Worst Time | Space Complexity | Visualization Strategy |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Bubble Sort** | $\mathcal{O}(n)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(1)$ | Adjacent pair comparisons with early-exit optimization |
| **Selection Sort** | $\mathcal{O}(n^2)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(1)$ | Unsorted suffix scanning, candidate minimum tracking, and in-place swaps |
| **Insertion Sort** | $\mathcal{O}(n)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(1)$ | Key extraction, rightward element shifting, and direct target insertion |
| **Merge Sort** | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n)$ | Recursive divide-and-conquer with in-place visual buffer writes |
| **Quick Sort** | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(\log n)$ avg stack | Lomuto partitioning with active pivot and range highlighting |

*Note: Space complexity reflects operational memory during execution (such as merge buffers or recursion stacks). Event snapshot history for animation replay is maintained separately.*

---

## Architecture Overview

Algomorph follows a strict separation of concerns:

- **Algorithm Engine (`com.algomotion.algorithm`):** Implements `SortingAlgorithm`. Provides `sort()` for generating deterministic immutable `SortEvent` sequences and `sortPure()` for high-speed benchmarking without memory overhead. Algorithms never mutate caller-provided arrays.
- **Event Model (`com.algomotion.model`):** Encapsulates immutable discrete operations (`COMPARE`, `SWAP`, `SELECT_MIN`, `HIGHLIGHT_KEY`, `SHIFT`, `WRITE`, `PARTITION`, `COMPLETED`), metrics counters, and defensive array snapshots.
- **Presentation Layer (`com.algomotion.view`):** Responsive JavaFX layout featuring custom `BarChartView` panes with dynamic bar scaling, custom metric cards, tabbed workspaces, and CSS dark theme.
- **Execution Coordination (`com.algomotion.controller`):** Utilizes `javafx.animation.Timeline` for non-blocking UI animation and background daemon threads (`ExecutorService`) for heavy data preparation and benchmark computations.

---

## Project Structure

```
Algomorph/
├── pom.xml
├── README.md
├── .gitignore
├── docs/
│   ├── milestone-1.md
│   ├── milestone-2.md
│   └── milestone-3.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── algomotion/
    │   │           ├── MainApp.java
    │   │           ├── algorithm/
    │   │           │   ├── SortingAlgorithm.java
    │   │           │   ├── BubbleSort.java
    │   │           │   ├── SelectionSort.java
    │   │           │   ├── InsertionSort.java
    │   │           │   ├── MergeSort.java
    │   │           │   └── QuickSort.java
    │   │           ├── model/
    │   │           │   ├── BenchmarkResult.java
    │   │           │   ├── BenchmarkService.java
    │   │           │   ├── ComparisonRowModel.java
    │   │           │   ├── DatasetGenerator.java
    │   │           │   ├── DatasetPattern.java
    │   │           │   ├── SortEvent.java
    │   │           │   ├── SortResult.java
    │   │           │   └── SortStatistics.java
    │   │           ├── controller/
    │   │           │   ├── BenchmarkController.java
    │   │           │   ├── ComparisonController.java
    │   │           │   └── SortingController.java
    │   │           └── view/
    │   │               ├── AlgorithmComparisonPanel.java
    │   │               ├── BarChartView.java
    │   │               ├── BenchmarkView.java
    │   │               ├── ComparisonView.java
    │   │               └── SortingView.java
    │   └── resources/
    │       └── styles/
    │           └── app.css
    └── test/
        └── java/
            └── com/
                └── algomotion/
                    ├── algorithm/
                    │   ├── AlgorithmComparisonTest.java
                    │   ├── BubbleSortTest.java
                    │   ├── InsertionSortTest.java
                    │   ├── MergeSortTest.java
                    │   ├── QuickSortTest.java
                    │   └── SelectionSortTest.java
                    └── model/
                        ├── BenchmarkServiceTest.java
                        └── DatasetGeneratorTest.java
```

---

## Prerequisites

- **Java Development Kit (JDK):** Version 17 or newer (Java 21 LTS recommended).
- **Apache Maven:** Version 3.8 or newer.
- **Operating System:** Windows, macOS, or Linux.

Verify your installed tools:
```bash
java -version
mvn -version
```

---

## Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Albert-006/Algomorph.git
   cd Algomorph
   ```

2. **Verify dependencies and compile:**
   ```bash
   mvn clean compile
   ```

---

## Build & Execution Instructions

### Run the Application
Launch the desktop application directly with the JavaFX Maven plugin:
```bash
mvn javafx:run
```

### Build Executable Package
Compile, test, and package into a JAR:
```bash
mvn clean package
```

---

## Testing Instructions

Run the complete automated test suite:
```bash
mvn clean test
```

The test suite contains **77 unit tests** covering:
- Correct sorting across all algorithms on all distribution patterns.
- Caller-owned array immutability and defensive copy verification.
- Event sequence ordering and snapshot replay fidelity.
- Dataset distribution properties and custom seed reproducibility.
- Headless benchmark timing validity, warmup runs, and error handling.

---

## Screenshots

```
+--------------------------------------------------------------------------+
|  Algomorph — Transforming Algorithms into Visual Experiences              |
|  [Single Visualizer]    [Compare Algorithms]    [Benchmark Lab]          |
|--------------------------------------------------------------------------|
|  Algorithm: [ Quick Sort v ]   Status: [ SORTING... ]                    |
|  Comparisons: 42   Swaps: 18   Writes: 0   Events: 60 / 72  Size: 20     |
|                                                                          |
|       ||       ||                                                        |
|   ||  ||   ||  ||   ||   ||       ||   ||                                |
|   ||  ||   ||  ||   ||   ||   ||  ||   ||                                |
|                                                                          |
|  [Play] [Pause] [Step] [Reset] [Randomize] | Delay: [---o---] 100 ms     |
+--------------------------------------------------------------------------+
```

---

## Future Roadmap

- Additional sorting algorithms (Heap Sort, Shell Sort, Radix Sort).
- Graph and pathfinding algorithms (Dijkstra, A*, Breadth-First, Depth-First).
- Custom user-defined array inputs.
- Sound effects and acoustic pitch mapping based on element values.
- Step-by-step pseudocode execution tracking.

---

## Contribution Guidelines

Contributions are welcome! To contribute:

1. Fork the repository on GitHub.
2. Create a descriptive feature branch (`git checkout -b feature/amazing-feature`).
3. Commit your changes with clear messages (`git commit -m "Add feature"`).
4. Verify all tests pass cleanly (`mvn clean test`).
5. Push to your branch (`git push origin feature/amazing-feature`).
6. Open a Pull Request detailing your enhancements.

---

## License

This project is open source and available under the terms of the MIT License.
