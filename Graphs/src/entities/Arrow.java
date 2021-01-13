package entities;

import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import main.Drawer;
import main.Visualizer;

enum Position {
    POSITIVE, NEGATIVE
}

/**
 * Represents an arrow (vector) of an amplitude
 */
class Arrow extends Line {

    private static final double BASE_LENGTH = 35;
    private Position curPos = Position.POSITIVE;

    private Polygon triangle;

    Arrow(double x1, double y1) {

        super(x1, y1, x1, y1 - BASE_LENGTH);
        triangle = new Polygon();
        drawTriangle(1);
    }

    /**
     * Redraws an arrow according to the updated amplitude's value
     * @param newVal new amplitude
     */
    void redrawArrow(double newVal) {

        if (curPos.equals(Position.NEGATIVE) && newVal > 0) {
            startYProperty().set(startYProperty().get() - 2 * Point.RADIUS);
            curPos = Position.POSITIVE;
        } else if (curPos.equals(Position.POSITIVE) && newVal < 0) {
            startYProperty().set(startYProperty().get() + 2 * Point.RADIUS);
            curPos = Position.NEGATIVE;
        }

        setEndY(getStartY() - BASE_LENGTH * (newVal) / getAbsMax());
        drawTriangle(newVal);
    }


    /**
     * Adds an arrow on the screen
     */
    void addArrow() {
        Drawer.getInstance().addElem(this);
        Drawer.getInstance().addElem(triangle);
    }

    /**
     * Removes an arrow from the screen
     */
    void removeArrow() {
        Drawer.getInstance().removeElement(this);
        Drawer.getInstance().removeElement(triangle);
    }


    /**
     * Binds arrow's location to the one of the point
     * @param x new x coordinate of the point
     */
    void setArrowTranslateX(double x) {
        setTranslateX(x);
        triangle.setTranslateX(x);
    }

    /**
     * Binds arrow's location to the one of the point
     * @param y new y coordinate of the point
     */
    void setArrowTranslateY(double y) {
        setTranslateY(y);
        triangle.setTranslateY(y);
    }

    /**
     * Redraws the triangle of an arrow according to the new amplitude
     * @param newVal new amplitude's value
     */
    private void drawTriangle(double newVal) {

        triangle.getPoints().clear();
        triangle.getPoints().addAll(getEndX(), getEndY(), getEndX() - Point.RADIUS / 2.0,
                getEndY() + Point.RADIUS * 5 / 4.0 * (newVal) / (getAbsMax()), getEndX() + Point.RADIUS / 2.0,
                getEndY() + Point.RADIUS * 5 / 4.0 * (newVal) / getAbsMax());

    }

    /**
     * Maximum modulo of an amplitude on the screen
     * @return max modulo
     */
    private long getAbsMax() {
        return Math.max(Math.abs(Visualizer.getLowerBound().get()),
                Visualizer.getUpperBound().get());
    }

}
