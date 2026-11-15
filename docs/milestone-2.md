# Algomorph — Milestone 2: Advanced Sorting Algorithms

## 1. Overview & Objectives

Milestone 2 extends **Algomorph** from a single-algorithm demo into a comprehensive sorting visualizer. Building upon the decoupled event-driven architecture established in Milestone 1, this milestone integrates four additional foundational sorting algorithms:
1. **Selection Sort**
2. **Insertion Sort**
3. **Merge Sort**
4. **Quick Sort**

All algorithms adhere to a unified [`SortingAlgorithm`](file:///d:/Files/Game/AlgoMotion/src/main/java/com/algomotion/algorithm/SortingAlgorithm.java) interface, emit immutable events to an extended [`SortEvent`](file:///d:/Files/Game/AlgoMotion/src/main/java/com/algomotion/model/SortEvent.java) model, and can be seamlessly selected from the GUI.

---

## 2. Algorithm Architecture & Theoretical Complexity

Each algorithm provides static theoretical metadata and an independent execution method that leaves caller-owned input arrays unmodified:

| Algorithm | Best Time | Average Time | Worst Time | Auxiliary Space (Sorting Logic) | Visual Replay Memory |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Bubble Sort** | $\mathcal{O}(n)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(1)$ | $\mathcal{O}(n^2)$ events |
| **Selection Sort** | $\mathcal{O}(n^2)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(1)$ | $\mathcal{O}(n^2)$ events |
| **Insertion Sort** | $\mathcal{O}(n)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(1)$ | $\mathcal{O}(n^2)$ events |
| **Merge Sort** | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n)$ (buffer) | $\mathcal{O}(n \log n)$ events |
| **Quick Sort** | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(\log n)$ avg / $\mathcal{O}(n)$ worst stack | $\mathcal{O}(n \log n)$ avg events |

> [!NOTE]
> The auxiliary space complexity refers strictly to the algorithm's runtime memory overhead during sorting (e.g. merge buffers or call stack). Retaining the event history for visualization introduces additional memory proportional to the event count.

---

## 3. Extended Event Model

The [`SortEvent.Type`](file:///d:/Files/Game/AlgoMotion/src/main/java/com/algomotion/model/SortEvent.java) enum was extended to support specialized visual semantics across all algorithms without breaking backward compatibility:

| Event Type | Used By | Visual Representation | Semantic Meaning |
| :--- | :--- | :--- | :--- |
| `COMPARE` | All | Amber bars (`#fbbf24`) | Compares elements at two indices. |
| `SWAP` | Bubble, Selection, Quick | Rose Coral bars (`#f43f5e`) | Swaps two elements in place. |
| `SELECT_MIN` | Selection Sort | Purple bar (`#c084fc`) | Highlights the current candidate minimum in unsorted suffix. |
| `HIGHLIGHT_KEY` | Insertion Sort | Orange bar (`#fb923c`) | Highlights the key element selected for insertion. |
| `SHIFT` | Insertion Sort | Pink bar (`#ec4899`) | Shifts a sorted element to the right to make room for key. |
| `WRITE` | Merge Sort, Insertion Sort | Pink bar (`#ec4899`) | Writes a value directly into a designated array index. |
| `PARTITION` | Quick Sort | Gold bar (`#facc15`) & Blue range (`#0ea5e9`) | Highlights chosen pivot and partition boundaries `[low..high]`. |
| `COMPLETED` | All | Emerald Green bars (`#34d399`) | Marks all elements as sorted upon algorithm completion. |

---

## 4. Algorithm Implementations

### 4.1 Selection Sort (`SelectionSort.java`)
- **Scan & Minimum Tracking:** Iterates across the unsorted suffix ($j = i + 1 \dots n - 1$), emitting `SELECT_MIN` events whenever a smaller element is detected.
- **In-Place Swap:** Performs at most one swap per outer pass $i$, moving the true minimum into position $i$.

### 4.2 Insertion Sort (`InsertionSort.java`)
- **Key Extraction:** Selects `key = arr[i]` and emits a `HIGHLIGHT_KEY` event.
- **Right Shifts:** While preceding sorted elements exceed the key, values shift right (`SHIFT` event, incrementing the write counter).
- **Target Placement:** The key is written into its final slot (`WRITE` event).

### 4.3 Merge Sort (`MergeSort.java`)
- **Divide and Conquer:** Recursively divides the array into subarrays down to base singletons.
- **Buffer & Merge Writes:** During merge passes, elements are compared and written directly into `arr[k]`. Every write produces a full snapshot clone, ensuring intermediate array states are visually accurate during replay.
- **Metrics Accuracy:** Increments comparisons and writes; swap count remains 0.

### 4.4 Quick Sort (`QuickSort.java`)
- **Lomuto Partitioning:** The last element of the partition (`arr[high]`) is designated as the pivot.
- **Pivot & Range Highlights:** Emits a `PARTITION` event highlighting the pivot index and range `[low..high]`.
- **Partition Swaps:** Elements smaller than or equal to the pivot move to the left; the pivot is placed in its final position via a concluding swap.

---

## 5. UI Enhancements

1. **Algorithm Selector ComboBox:**
   - Lets the user switch between Bubble Sort, Selection Sort, Insertion Sort, Merge Sort, and Quick Sort.
   - Automatically pauses active playback, clears old state, and generates a fresh event queue for the current array.
2. **Complexity Information Panel:**
   - Displays real-time theoretical metrics: Best Time, Average Time, Worst Time, and Auxiliary Space.
3. **Writes Metric Counter:**
   - Added alongside Comparisons, Swaps, and Array Size to clearly explain algorithms that perform array writes rather than swaps (Merge Sort and Insertion Sort).
4. **Enhanced Legend:**
   - Clearly documents colors for Default, Comparing, Swap, Write/Shift, Pivot/Min, and Sorted states.

---

## 6. Automated Test Coverage

The test suite contains **58 unit tests** across 6 test classes:
- [`BubbleSortTest`](file:///d:/Files/Game/AlgoMotion/src/test/java/com/algomotion/algorithm/BubbleSortTest.java) (11 tests) — Milestone 1 regression suite.
- [`SelectionSortTest`](file:///d:/Files/Game/AlgoMotion/src/test/java/com/algomotion/algorithm/SelectionSortTest.java) (11 tests) — Correctness, duplicate handling, min selection.
- [`InsertionSortTest`](file:///d:/Files/Game/AlgoMotion/src/test/java/com/algomotion/algorithm/InsertionSortTest.java) (11 tests) — Linear best-case, shift/write verification.
- [`MergeSortTest`](file:///d:/Files/Game/AlgoMotion/src/test/java/com/algomotion/algorithm/MergeSortTest.java) (11 tests) — Intermediate array write verification, $O(n \log n)$ performance.
- [`QuickSortTest`](file:///d:/Files/Game/AlgoMotion/src/test/java/com/algomotion/algorithm/QuickSortTest.java) (11 tests) — Lomuto partition and pivot verification.
- [`AlgorithmComparisonTest`](file:///d:/Files/Game/AlgoMotion/src/test/java/com/algomotion/algorithm/AlgorithmComparisonTest.java) (3 tests) — Consistency across all 5 algorithms on identical datasets, complexity metadata validation, and switching tests.
