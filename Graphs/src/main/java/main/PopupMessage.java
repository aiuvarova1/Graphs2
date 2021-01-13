package main;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * A popup which notifies the user about incorrect input or other events
 */
public class PopupMessage {
    private static Label popup;
    private static FadeTransition ft;

    /**
     * Sets the parameters of the popup window
     *
     * @param label popup label
     */
    public static void setPopup(Label label) {
        popup = label;
        ft = new FadeTransition(Duration.millis(3000), popup);
        ft.setFromValue(0.9);
        ft.setToValue(0);
        ft.setDelay(Duration.millis(3000));
        ft.setOnFinished((x) -> popup.setVisible(false));
    }

    public static void showMessage(String mes) {
        ft.stop();
        ft.setDelay(Duration.millis(3000));
        popup.setVisible(true);
        popup.setText(mes);
        popup.setOpacity(0.9);
        popup.toFront();
        ft.play();
    }

    /**
     * Fixes the message until it is not unfixed
     *
     * @param mes message to fix
     */
    public static void fixMessage(String mes) {
        ft.stop();

        ft.setDelay(Duration.millis(0));
        popup.setVisible(true);
        popup.setText(mes);
        popup.setOpacity(0.9);
        popup.toFront();
    }

    /**
     * Unfixes the message
     */
    public static void unfixMessage() {
        ft.play();
    }
}
