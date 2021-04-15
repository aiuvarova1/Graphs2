package main;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * A popup which notifies the user about incorrect input or other events
 */
public class PopupMessage {
    private static Label messageLabel;
    private static FadeTransition transition;

    /**
     * Sets the parameters of the popup window
     *
     * @param label popup label
     */
    public static void setMessageLabel(Label label) {
        messageLabel = label;
        transition = new FadeTransition(Duration.millis(3000), messageLabel);
        transition.setFromValue(0.9);
        transition.setToValue(0);
        transition.setDelay(Duration.millis(3000));
        transition.setOnFinished((x) -> messageLabel.setVisible(false));
    }

    public static void showPopup(String message) {
        transition.stop();
        transition.setDelay(Duration.millis(3000));
        messageLabel.setVisible(true);
        messageLabel.setText(message);
        messageLabel.setOpacity(0.9);
        messageLabel.toFront();
        transition.play();
    }

    /**
     * Fixes the message until it is not unfixed
     *
     * @param mes message to fix
     */
    public static void fixMessage(String mes) {
        transition.stop();

        transition.setDelay(Duration.millis(0));
        messageLabel.setVisible(true);
        messageLabel.setText(mes);
        messageLabel.setOpacity(0.9);
        messageLabel.toFront();
    }

    /**
     * Unfixes the message
     */
    public static void unfixMessage() {
        transition.play();
    }
}
