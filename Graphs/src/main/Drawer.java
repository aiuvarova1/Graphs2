package main;

import entities.*;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.util.ArrayList;


/**
 * Draws Nodes and stores some needed references connected
 * with drawing
 */
public class Drawer {

    public static final int BOUNDS_GAP = 25;

    public static final String NODE_TEXT = "-fx-font-family: \"Pristina\";" +
            "-fx-font-size: 24px;";

    private static Drawer instance;
    private AnchorPane pane;
    private StackPane dialog;


    /**
     * Singleton
     *
     * @return static instance
     */
    public static Drawer getInstance() {
        if (instance == null) {
            instance = new Drawer();
        }
        return instance;
    }

    public void clear() {
        pane.getChildren().removeIf(x -> x instanceof Edge ||
                x instanceof Distance || x instanceof Node);
    }

    public WritableImage takeSnap() {
        return pane.snapshot(null, null);
    }


    /**
     * Removes element from the drawing pane
     *
     * @param node node to remove
     */
    public void removeElement(javafx.scene.Node node) {
        pane.getChildren().remove(node);
    }

    void setFocus() {
        pane.requestFocus();
    }

    void setPane(AnchorPane pane, StackPane dialog) {
        this.pane = pane;
        this.dialog = dialog;
    }

    public void enableDialog(boolean enable) {
        dialog.setDisable(!enable);
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
    public void addElem(javafx.scene.Node el) {
        pane.getChildren().add(el);
    }

    /**
     * Removes all points from the graph
     */
    void removePoints() {

        ArrayList<Point> lst = new ArrayList<>();
        pane.getChildren().removeIf(x ->
        {
            if (x instanceof Point) {
                lst.add((Point) x);
            }
            return x instanceof Point;
        });

        for (Point p : lst) {
            p.hideEnabled();
        }

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
    Node drawNode(MouseEvent ev) {
        double[] cors = checkBounds(ev.getX(), ev.getY());
        return createLayout(cors[0], cors[1]);
    }

    public Node drawInfiniteNode(double xPos, double yPos, int num, double radius, boolean needText) {

        Circle node = new Circle(xPos, yPos, radius, Color.WHITE);
        node.setStroke(Color.BLACK);

        Node layout = new Node(num);
        layout.getChildren().add(node);
        if(needText) {
            Text numText = new Text("" + (num + 1));
            numText.setStyle(NODE_TEXT);
            layout.getChildren().add(numText);
        }
        layout.fixPosition(xPos - radius, yPos - radius);
        return layout;
    }

    /**
     * Creates node's screen representation
     *
     * @param xPos x coordinate of center
     * @param yPos y coordinate of center
     * @return node's representation on the screen (Circle + text united in stack pane)
     */
    private Node createLayout(double xPos, double yPos) {

       // System.out.println("Node in " + xPos + " " + yPos);

        Circle node = new Circle(xPos, yPos, Node.RADIUS, Color.WHITE);

        node.addEventFilter(MouseEvent.MOUSE_DRAGGED, Filter.dragFilter);
        node.setStroke(Color.BLACK);

        Node layout = new Node(SimpleGraph.getInstance().getSize() + 1);
        Text numText = new Text("" + (SimpleGraph.getInstance().getSize() + 1));

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
