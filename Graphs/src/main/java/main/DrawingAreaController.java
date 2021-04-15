package main;

import entities.*;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

/**
 * Draws Nodes and stores some needed references connected
 * with drawing
 */
public class DrawingAreaController {

    public static final int BOUNDS_GAP = 25;

    public static final String NODE_TEXT = "-fx-font-family: \"Pristina\";" +
        "-fx-font-size: 24px;";

    private static DrawingAreaController instance;
    private AnchorPane pane;
    private StackPane fileDialog;

    /**
     * Singleton
     *
     * @return static instance
     */
    public static DrawingAreaController getInstance() {
        if (instance == null) {
            instance = new DrawingAreaController();
        }
        return instance;
    }

    public void clear() {
        pane.getChildren().removeIf(x -> x instanceof Edge ||
            x instanceof EdgeDistance || x instanceof Node);
    }

    /**
     * Removes element from the drawing pane
     *
     * @param node node to remove
     */
    public void hideNode(javafx.scene.Node node) {
        pane.getChildren().remove(node);
    }

    void setFocus() {
        pane.requestFocus();
    }

    void setPane(AnchorPane pane, StackPane dialog) {
        this.pane = pane;
        this.fileDialog = dialog;
    }

    public void enableDialog(boolean enable) {
        fileDialog.setDisable(!enable);
    }

    /**
     * Sets handler for the drawing pane
     *
     * @param h handler to set
     */
    void setMoveHandler(EventHandler h) {
        pane.setOnMouseMoved(h);
    }

    /**
     * Removes onMouseMove handler
     */
    void removeMoveHandler() {
        pane.setOnMouseMoved(null);
    }

    /**
     * Adds element to the pane
     *
     * @param el element to add
     */
    public void addNode(javafx.scene.Node el) {
        pane.getChildren().add(el);
    }

    /**
     * @return bounds of the drawing area
     */
    public Bounds getBounds() {
        return instance.pane.getBoundsInLocal();
    }

    /**
     * Draws the node by calling needed methods and adds it to the scene
     *
     * @param ev parameters of a click
     * @return Screen representation of the node
     */
    public Node createNodeByClick(MouseEvent ev) {
        double[] cors = checkBounds(ev.getX(), ev.getY());
        return drawNodeLayout(cors[0], cors[1]);
    }

    /**
     * Creates node's screen representation
     *
     * @param xPos x coordinate of center
     * @param yPos y coordinate of center
     * @return node's representation on the screen (Circle + text united in stack pane)
     */
    private Node drawNodeLayout(double xPos, double yPos) {

        // System.out.println("Node in " + xPos + " " + yPos);

        Circle node = new Circle(xPos, yPos, Node.RADIUS, Color.WHITE);

        node.addEventFilter(MouseEvent.MOUSE_DRAGGED, EventFilter.dragFilter);
        node.setStroke(Color.BLACK);

        Node layout = new Node(Graph.getInstance().getSize() + 1);
        Text numText = new Text("" + (Graph.getInstance().getSize() + 1));

        numText.setStyle(NODE_TEXT);

        layout.getChildren().addAll(node, numText);
        layout.fixPosition(xPos - Node.RADIUS, yPos - Node.RADIUS);

        return layout;
    }

    /**
     * Checks whether the click position crosses the bounds and changes it if needed
     *
     * @param xPos x-coordinate of a click
     * @param yPos y-coordinate of a click
     * @return renewed (if needed) coordinates
     */
    private double[] checkBounds(double xPos, double yPos) {

        Bounds bounds = (instance.pane.getBoundsInLocal());

        if (xPos - Node.RADIUS < bounds.getMinX()) {
            xPos = bounds.getMinX() + Node.RADIUS;
        } else if (xPos + Node.RADIUS > bounds.getMaxX()) {
            xPos = bounds.getMaxX() - Node.RADIUS - BOUNDS_GAP;
        }

        if (yPos - Node.RADIUS < bounds.getMinY()) {
            yPos = bounds.getMinY() + Node.RADIUS + BOUNDS_GAP;
        } else if (yPos + Node.RADIUS > bounds.getMaxY()) {
            yPos = bounds.getMaxY() - Node.RADIUS - BOUNDS_GAP;
        }

        return new double[]{xPos, yPos};
    }

}
