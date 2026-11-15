# Algomorph — Milestone 1: JavaFX Sorting Visualizer

## 1. Overview & Objectives

Milestone 1 establishes the architectural foundation of **Algomorph**, an educational algorithm visualization desktop application. The primary goal of this milestone is to deliver a robust, beginner-friendly, and portfolio-grade implementation of **Bubble Sort** visual playback using Java 21 and JavaFX.

Key capabilities achieved:
- Decoupled sorting algorithm execution that generates an immutable sequence of sorting events.
- Responsive JavaFX vertical bar chart rendering with clean layout and scaling.
- Comprehensive playback controls: Play, Pause, Next Step, Reset, Randomize, Array Size, and Dynamic Speed adjustments.
- Real-time tracking of comparison counts, swap counts, event progress, and array size.
- Clear visual differentiation of active elements (comparing vs swapping vs sorted).
- High-coverage JUnit 5 unit tests validating algorithm correctness, edge cases, and immutability.

---

## 2. Architecture & Design Decisions

### 2.1 Decoupling Algorithm from JavaFX
A common pitfall in educational visualizers is mixing GUI calls (or `Thread.sleep`) directly within the algorithm's loop. In Algomorph:
- `BubbleSort` has **zero dependencies** on JavaFX classes.
- It takes an `int[]` and executes standard ascending sorting logic.
- During execution, it emits immutable `SortEvent` snapshots recording:
  - Event type (`COMPARE`, `SWAP`, `COMPLETED`)
  - The pair of indices involved (`firstIndex`, `secondIndex`)
  - A defensive clone of the array state at that exact moment
  - Cumulative metrics (comparisons count, swap count)
  - A human-readable description of the operation
- This makes the algorithm **100% testable** in headless test environments (CI/CD, automated build pipelines) with standard JUnit 5 assertions.

### 2.2 Defensive Copying & State Immutability
To guarantee that user actions (such as resetting or scrubbing) cannot cause stale references or mutation artifacts:
1. `BubbleSort.sort(int[] input)` immediately clones the input array via `input.clone()`. The caller's array is never altered.
2. Each `SortEvent` stores its own cloned snapshot of the array at that step.
3. Accessors (`getSnapshot()`, `sortedArray()`) return fresh defensive clones.

### 2.3 Non-blocking Playback Architecture
JavaFX utilizes a single-threaded event loop (the JavaFX Application Thread). Blocking this thread freezes the UI and window manager.
- We utilize `javafx.animation.Timeline` with periodic `KeyFrame` callbacks matching the user's selected delay (in milliseconds).
- The controller coordinates an explicit state machine:
  - `READY`: Array generated or reset; ready to start.
  - `RUNNING`: Periodic `Timeline` actively stepping forward through events.
  - `PAUSED`: Animation halted; user can step manually or resume.
  - `COMPLETED`: Array fully sorted; final state reached.
- Controls that would introduce race conditions (such as modifying array size or stepping while playing) are safely guarded or disabled during active playback.
- If the user adjusts the speed slider during playback, the `Timeline` updates dynamically without needing a manual pause and restart.
- Calling `reset()` or `randomize()` stops and nullifies any running `Timeline`, guaranteeing no stale callbacks can fire on subsequent ticks.

---

## 3. Component Breakdown

| Package | Class | Role |
| :--- | :--- | :--- |
| `com.algomotion` | `MainApp` | JavaFX Application bootstrap; configures Scene, Window properties, and CSS. |
| `com.algomotion.algorithm` | `BubbleSort` | Headless Bubble Sort engine producing deterministic event history. |
| `com.algomotion.model` | `SortEvent` | Immutable record of a discrete step (compare, swap, or completed) with snapshot. |
| `com.algomotion.model` | `SortStatistics` | Immutable metrics container (comparisons, swaps, total events). |
| `com.algomotion.controller` | `SortingController` | Playback state machine; drives `Timeline` scheduling and view updates. |
| `com.algomotion.view` | `SortingView` | Top-level dashboard combining metrics, legend, chart, and toolbar. |
| `com.algomotion.view` | `BarChartView` | Custom responsive `Pane` rendering `Rectangle` bars scaled to dimensions. |

---

## 4. Complexity Analysis: Bubble Sort

| Case | Time Complexity | Comparisons | Swaps | Notes |
| :--- | :--- | :--- | :--- | :--- |
| **Best Case** | $\mathcal{O}(n)$ | $n - 1$ | $0$ | Achieved via early-exit pass flag when array is already sorted. |
| **Average Case** | $\mathcal{O}(n^2)$ | $\approx \frac{n(n-1)}{4}$ | $\approx \frac{n(n-1)}{4}$ | Elements randomly distributed. |
| **Worst Case** | $\mathcal{O}(n^2)$ | $\frac{n(n-1)}{2}$ | $\frac{n(n-1)}{2}$ | Array is sorted in reverse order. |
| **Auxiliary Space** | $\mathcal{O}(1)$ | — | — | Fundamental in-place sorting logic (excluding visualization event history). |

---

## 5. Test Suite Summary

The unit test suite (`BubbleSortTest.java`) provides comprehensive automated verification:
1. `testOrdinaryUnsortedArray`: Verifies correct ascending ordering and completed status.
2. `testAlreadySortedArray`: Confirms early-exit optimization triggers after 1 pass ($n-1$ comparisons, 0 swaps).
3. `testDescendingArray`: Validates worst-case comparison and swap counts on reverse input.
4. `testDuplicateValues`: Tests correct handling of duplicated integers.
5. `testEmptyArray`: Validates graceful execution without `IndexOutOfBoundsException`.
6. `testSingleElementArray`: Validates trivial single-element arrays.
7. `testNegativeValues`: Confirms correct ascending sorting of negative and mixed numbers.
8. `testComparisonAndSwapCounts`: Verifies that comparison and swap counters increment accurately.
9. `testEventOrderingAndSnapshots`: Verifies sequence order (`COMPARE` $\to$ `SWAP` $\to$ `COMPLETED`) and valid snapshots.
10. `testCallerInputNotModified`: Verifies caller-owned array is preserved.
11. `testNullInputThrowsException`: Ensures defensive contract enforcement against null inputs.
