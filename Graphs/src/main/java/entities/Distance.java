package entities;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import main.*;

import java.io.Serializable;


/**
 * Control with distance text label and input field
 */
public class Distance extends Pane implements Serializable {
    private transient TexLabel label;
    private transient TextField input;

    private double value = Double.MAX_VALUE;
    private String curText = TexLabel.DEFAULT;

    public static final int MAX_LENGTH = 70;
    private static boolean isCalculated = false;


    public static void setCalc(boolean val) {
        isCalculated = val;
    }


    public Distance() {

        if (!InfiniteManager.canEdit()) {
            value = 1;
            return;
        }
        label = new TexLabel();
        input = new TextField();

        input.setOnAction(actionEvent -> {
            showLabel();
            Filter.endEdit();
        });
        input.focusedProperty().addListener((observableValue, old, newVal) -> {
            if (!newVal) {
                showLabel();
                Filter.endEdit();
            }
        });

        input.setOnKeyTyped(event -> {
            String string = input.getText();

            if (string.length() > MAX_LENGTH) {
                input.setText(string.substring(0, MAX_LENGTH));
                input.positionCaret(string.length());
            }
        });

        this.setHeight(label.getHeight());
        this.setWidth(label.getWidth());

        this.getChildren().add(label);
        input.setDisable(true);

        if (SimpleGraph.areDistancesShown()) {
            show();
        }

        this.addEventFilter(MouseEvent.MOUSE_CLICKED, Filter.clickFilter);
    }



    /**
     * Hides label and shows input field
     */
    public void showInput() {
        if (Filter.isEditing()) return;

        this.getChildren().add(input);
        input.setDisable(false);
        input.requestFocus();
        this.getChildren().remove(label);
        input.toFront();
    }

    /**
     * Hides input field and returns label
     */
    private void showLabel() {
        if (!Filter.isEditing()) return;
//        setDistance(input.getText());

        try {
            value = Parser.parseDistance(input.getText());

            if (!isCalculated)
                Invoker.getInstance().changeDistance(this, input.getText(), value);
            else
                Invoker.getInstance().changeDistance(this, Formatter.format(value), value);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            PopupMessage.showMessage(ex.getMessage());
        } finally {
            this.getChildren().add(label);
            this.getChildren().remove(input);
            label.toFront();
            input.setDisable(true);
        }
    }

    void show() {
        if (!isCalculated || value == Double.MAX_VALUE
                || value == Double.MIN_VALUE)
            curText = label.setText(curText);
        else
            label.setText(Formatter.format(value));
        Drawer.getInstance().addElem(this);
    }

    void hide() {
        Drawer.getInstance().removeElement(this);
    }

    public void setDistance(String text, double val) {
        if (!isCalculated || val == Double.MAX_VALUE
                || value == Double.MIN_VALUE)
            curText = label.setText(text);
        else
            label.setText(Formatter.format(val));
        value = val;
    }

    public String getText() {
        return curText;
    }

    /**
     * Calculates the length in input
     */
    public void calculate() {
        if (value != Double.MAX_VALUE && value != Double.MIN_VALUE)
            label.setText(Formatter.format(value));
    }

    /**
     * Returns the length to the initial state (before any computations)
     */
    public void decalculate() {
        if (value != Double.MAX_VALUE && value != Double.MIN_VALUE)
            label.setText(curText);
    }

    /**
     * Resets the length to infinity
     */
    void reset() {
        value = Double.MAX_VALUE;
        curText = label.setText("\\infty");
    }

    /**
     * @return Is length infinite
     */
    public boolean isInfty() {
        return value == Double.MAX_VALUE || value == Double.MIN_VALUE;
    }

    public double getValue() {
        return value;
    }

}



