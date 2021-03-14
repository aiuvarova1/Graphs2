package entities;

import java.io.Serializable;
import java.util.function.BiConsumer;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import main.Drawer;
import main.Filter;

public class Anchor extends Circle implements Serializable {

    public static final Double RADIUS = 9.0;
    private static final Color BASE_COLOR = Color.DARKBLUE;
    private static final Color LIGHT_COLOR = BASE_COLOR.deriveColor(0, 1, 1, 0.5);
    private static final Color TRANSPARENT = Color.TRANSPARENT;

    public Anchor(
        DoubleProperty controlX,
        DoubleProperty controlY,
        BiConsumer<Double, Double> anchorManager
    ) {
        super(controlX.get(), controlY.get(), RADIUS);

        hide();

        setStrokeWidth(1);

        enableDrag(anchorManager);
    }

    private void enableDrag(BiConsumer<Double, Double> anchorManager) {

        addEventFilter(MouseEvent.MOUSE_DRAGGED, Filter.dragFilter);
        addEventFilter(MouseEvent.MOUSE_CLICKED, Filter.clickFilter);

        setOnMousePressed(mouseEvent -> getScene().setCursor(Cursor.MOVE));
        setOnMouseReleased(mouseEvent -> getScene().setCursor(Cursor.HAND));

        setOnMouseDragged(mouseEvent -> {
            double newX = mouseEvent.getX();
            double newY = mouseEvent.getY();

            setNewCoordinatesSafe(newX, newY);
            anchorManager.accept(getCenterX(), getCenterY());

        });
        setOnMouseEntered(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.HAND);
                show();
                toFront();
            }
        });
        setOnMouseExited(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.DEFAULT);
                hide();
            }
        });
    }

    public void setNewCoordinatesSafe(double newX, double newY) {
        Bounds b = Drawer.getInstance().getBounds();

        if (newX > 0 && getTranslateX() + newX + 2 * RADIUS + getLayoutX() < b.getMaxX()) {
            setCenterX(newX);
        }
        if (
            getTranslateY() + newY - RADIUS + getLayoutY() > b.getMinY() &&
                getTranslateY() + newY + 2 * RADIUS + getLayoutY() < b.getMaxY()
        ) {
            setCenterY(newY);
        }
    }

    public void show() {
        setFill(LIGHT_COLOR);
        setStroke(BASE_COLOR);
    }

    public void hide() {
        setFill(TRANSPARENT);
        setStroke(TRANSPARENT);
    }
}
