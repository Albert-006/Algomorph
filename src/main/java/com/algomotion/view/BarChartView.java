package com.algomotion.view;

import com.algomotion.model.SortEvent;
import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Responsive vertical bar chart component for displaying array elements.
 * Uses JavaFX {@link Rectangle} nodes scaled dynamically to the component bounds.
 */
public class BarChartView extends Pane {

    private int[] currentArray = new int[0];
    private int firstActiveIndex = -1;
    private int secondActiveIndex = -1;
    private int pivotIndex = -1;
    private int rangeStart = -1;
    private int rangeEnd = -1;
    private SortEvent.Type activeEventType = null;
    private boolean isCompleted = false;
    private int fixedMaxAbsValue = -1;

    public BarChartView() {
        getStyleClass().add("bar-chart-pane");

        // Redraw on window/pane resize to keep bars responsive and evenly spaced
        widthProperty().addListener((obs, oldW, newW) -> draw());
        heightProperty().addListener((obs, oldH, newH) -> draw());
    }

    /**
     * Updates the chart to show an initial or reset array with all bars in default state.
     */
    public void displayArray(int[] array) {
        this.currentArray = array != null ? array.clone() : new int[0];
        this.firstActiveIndex = -1;
        this.secondActiveIndex = -1;
        this.pivotIndex = -1;
        this.rangeStart = -1;
        this.rangeEnd = -1;
        this.activeEventType = null;
        this.isCompleted = false;
        draw();
    }

    /**
     * Updates the chart to reflect the state of a specific sorting event.
     */
    public void displayEvent(SortEvent event) {
        if (event == null) {
            return;
        }
        this.currentArray = event.getSnapshot();
        this.firstActiveIndex = event.getFirstIndex();
        this.secondActiveIndex = event.getSecondIndex();
        this.pivotIndex = event.getPivotIndex();
        this.rangeStart = event.getRangeStart();
        this.rangeEnd = event.getRangeEnd();
        this.activeEventType = event.getType();
        this.isCompleted = (event.getType() == SortEvent.Type.COMPLETED);
        draw();
    }

    /**
     * Updates the chart to show the final sorted state where all bars are marked completed.
     */
    public void displayCompleted(int[] finalArray) {
        this.currentArray = finalArray != null ? finalArray.clone() : new int[0];
        this.firstActiveIndex = -1;
        this.secondActiveIndex = -1;
        this.pivotIndex = -1;
        this.rangeStart = -1;
        this.rangeEnd = -1;
        this.activeEventType = SortEvent.Type.COMPLETED;
        this.isCompleted = true;
        draw();
    }

    public void setFixedMaxAbsValue(int fixedMaxAbsValue) {
        this.fixedMaxAbsValue = fixedMaxAbsValue;
        draw();
    }

    /**
     * Redraws all bars and value labels based on the current dimensions and state.
     */
    private void draw() {
        getChildren().clear();

        int n = currentArray.length;
        if (n == 0) {
            return;
        }

        double width = getWidth();
        double height = getHeight();

        // Avoid rendering if bounds are not yet established
        if (width <= 0 || height <= 0) {
            return;
        }

        double paddingHorizontal = 24.0;
        double paddingTop = 36.0;
        double paddingBottom = 28.0;

        double availableWidth = Math.max(10.0, width - (2 * paddingHorizontal));
        double availableHeight = Math.max(10.0, height - paddingTop - paddingBottom);

        // Find max absolute value to scale bar heights relative to the largest value
        int maxAbsValue = (fixedMaxAbsValue > 0) ? fixedMaxAbsValue : 1;
        if (fixedMaxAbsValue <= 0) {
            for (int val : currentArray) {
                maxAbsValue = Math.max(maxAbsValue, Math.abs(val));
            }
        }

        double slotWidth = availableWidth / n;
        // Spacing: proportionally smaller gap for large arrays, keeping minimum bar thickness
        double gap = Math.max(2.0, Math.min(6.0, slotWidth * 0.15));
        double barWidth = Math.max(3.0, slotWidth - gap);

        boolean showValueLabels = (n <= 35) && (barWidth >= 14.0);

        for (int i = 0; i < n; i++) {
            int value = currentArray[i];
            double normalizedHeight = (double) Math.abs(value) / maxAbsValue;
            double barHeight = Math.max(4.0, normalizedHeight * availableHeight);

            double x = paddingHorizontal + (i * slotWidth) + ((slotWidth - barWidth) / 2.0);
            double y = (height - paddingBottom) - barHeight;

            Rectangle bar = new Rectangle(x, y, barWidth, barHeight);
            bar.setArcWidth(Math.min(6.0, barWidth / 2.0));
            bar.setArcHeight(Math.min(6.0, barWidth / 2.0));

            // Assign CSS style classes according to current operation state
            bar.getStyleClass().add("sort-bar");

            if (isCompleted) {
                bar.getStyleClass().add("bar-sorted");
            } else if (i == pivotIndex) {
                bar.getStyleClass().add("bar-pivot");
            } else if (activeEventType == SortEvent.Type.SELECT_MIN) {
                if (i == secondActiveIndex) {
                    bar.getStyleClass().add("bar-min");
                } else if (i == firstActiveIndex) {
                    bar.getStyleClass().add("bar-compared");
                } else {
                    bar.getStyleClass().add("bar-default");
                }
            } else if (activeEventType == SortEvent.Type.HIGHLIGHT_KEY && i == firstActiveIndex) {
                bar.getStyleClass().add("bar-key");
            } else if (activeEventType == SortEvent.Type.WRITE && i == firstActiveIndex) {
                bar.getStyleClass().add("bar-write");
            } else if (activeEventType == SortEvent.Type.SHIFT && (i == firstActiveIndex || i == secondActiveIndex)) {
                bar.getStyleClass().add("bar-write");
            } else if (i == firstActiveIndex || i == secondActiveIndex) {
                if (activeEventType == SortEvent.Type.SWAP) {
                    bar.getStyleClass().add("bar-swapped");
                } else if (activeEventType == SortEvent.Type.COMPARE) {
                    bar.getStyleClass().add("bar-compared");
                } else {
                    bar.getStyleClass().add("bar-default");
                }
            } else if (activeEventType == SortEvent.Type.PARTITION && i >= rangeStart && i <= rangeEnd) {
                bar.getStyleClass().add("bar-in-range");
            } else {
                bar.getStyleClass().add("bar-default");
            }

            getChildren().add(bar);

            // Optional numerical value labels above bars when spacing permits
            if (showValueLabels) {
                Text valText = new Text(String.valueOf(value));
                valText.getStyleClass().add("bar-value-label");
                valText.setTextOrigin(VPos.BOTTOM);
                valText.setTextAlignment(TextAlignment.CENTER);

                // Dynamically fit font size to bar width
                double fontSize = Math.max(9.0, Math.min(12.0, barWidth * 0.65));
                valText.setFont(Font.font("Segoe UI", fontSize));

                double textX = x + (barWidth / 2.0) - (valText.getLayoutBounds().getWidth() / 2.0);
                double textY = y - 4.0;
                valText.setX(textX);
                valText.setY(textY);

                getChildren().add(valText);
            }
        }
    }
}
