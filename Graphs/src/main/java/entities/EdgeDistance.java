package entities;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import main.*;

import java.io.Serializable;

/**
 * Control with distance text label and input field
 */
public class EdgeDistance extends Pane implements Serializable {
    public static final int MAX_LENGTH = 70;
    private static boolean MUST_CALCULATE = false;

    private transient TexLabel texLabel;
    private transient TextField inputField;

    private double value = Double.MAX_VALUE;
    private String textValue = TexLabel.DEFAULT;

    public static void setCalc(boolean val) {
        MUST_CALCULATE = val;
    }

    public EdgeDistance() {
        texLabel = new TexLabel();
        inputField = new TextField();

        inputField.setOnAction(actionEvent -> {
            showLabel();
            EventFilter.endEdit();
        });
        inputField.focusedProperty().addListener((observableValue, old, newVal) -> {
            if (!newVal) {
                showLabel();
                EventFilter.endEdit();
            }
        });

        inputField.setOnKeyTyped(event -> {
            String string = inputField.getText();

            if (string.length() > MAX_LENGTH) {
                inputField.setText(string.substring(0, MAX_LENGTH));
                inputField.positionCaret(string.length());
            }
        });

        this.setHeight(texLabel.getHeight());
        this.setWidth(texLabel.getWidth());

        this.getChildren().add(texLabel);
        inputField.setDisable(true);

        if (Graph.areDistancesShown()) {
            toScreen();
        }

        this.addEventFilter(MouseEvent.MOUSE_CLICKED, EventFilter.clickFilter);
    }

    /**
     * Hides label and shows input field
     */
    public void showInput() {
        if (EventFilter.isEditing()) {
            return;
        }

        this.getChildren().add(inputField);
        inputField.setDisable(false);
        inputField.requestFocus();
        this.getChildren().remove(texLabel);
        inputField.toFront();
    }

    /**
     * Hides input field and returns label
     */
    private void showLabel() {
        if (!EventFilter.isEditing()) {
            return;
        }
//        setDistance(input.getText());

        try {
            value = Parser.parseDistance(inputField.getText());

            if (!MUST_CALCULATE) {
                Invoker.getInstance().setSingleLength(this, inputField.getText(), value);
            } else {
                Invoker.getInstance().setSingleLength(this, Formatter.format(value), value);
            }
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            PopupMessage.showPopup(ex.getMessage());
        } finally {
            this.getChildren().add(texLabel);
            this.getChildren().remove(inputField);
            texLabel.toFront();
            inputField.setDisable(true);
        }
    }

    public void toScreen() {
        if (!MUST_CALCULATE || value == Double.MAX_VALUE
            || value == Double.MIN_VALUE) {
            textValue = texLabel.setText(textValue);
        } else {
            texLabel.setText(Formatter.format(value));
        }
        DrawingAreaController.getInstance().addNode(this);
    }

    public void fromScreen() {
        DrawingAreaController.getInstance().hideNode(this);
    }

    public void setDistance(String text, double val) {
        if (!MUST_CALCULATE || val == Double.MAX_VALUE
            || value == Double.MIN_VALUE) {
            textValue = texLabel.setText(text);
        } else {
            texLabel.setText(Formatter.format(val));
        }
        value = val;
    }

    public String getText() {
        return textValue;
    }

    /**
     * Calculates the length in input
     */
    public void showNumeric() {
        if (value != Double.MAX_VALUE && value != Double.MIN_VALUE) {
            texLabel.setText(Formatter.format(value));
        }
    }

    /**
     * Returns the length to the initial state (before any computations)
     */
    public void showText() {
        if (value != Double.MAX_VALUE && value != Double.MIN_VALUE) {
            texLabel.setText(textValue);
        }
    }

    /**
     * Resets the length to infinity
     */
    public void toInfty() {
        value = Double.MAX_VALUE;
        textValue = texLabel.setText("\\infty");
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



