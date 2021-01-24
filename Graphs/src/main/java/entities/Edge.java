package entities;

import javafx.animation.PathTransition;
import javafx.scene.Cursor;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.Setter;
import main.Drawer;
import main.Filter;
import main.MenuManager;
import main.PopupMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an edge between 2 nodes
 */
@Getter
@Setter
public class Edge extends Line implements Undoable, Visitable,
    Serializable, Restorable {

    private static final double LABEL_GAP = 15;
    private static final Color color = Color.DIMGRAY;
    private static final Color selectedColor = Color.LIGHTBLUE;

    private Node n1;
    private Node n2;

    private boolean visited = false;

    private Distance length;
    private Color curColor = color;

    public Edge(double v1, double v2, double v3, double v4) {

        super(v1, v2, v3, v4);
        this.setStrokeWidth(1.7);

        setStroke(color);

    }

    void hide() {
        Drawer.getInstance().removeElement(this);
        Drawer.getInstance().removeElement(length);
    }

    void show() {
        Drawer.getInstance().addElem(this);
        Drawer.getInstance().addElem(length);
    }

    /**
     * Sets nodes on the ends of the edge
     *
     * @param n1 start node
     * @param n2 end node
     */
    public void setNodes(Node n1, Node n2) {
        this.n1 = n1;
        this.n2 = n2;

        setHandlers();

        length = new Distance();
        relocateLabel();
    }

    @Override
    public void restore() {

        this.setStrokeWidth(1.7);
        setHandlers();
        setStroke(color);

        curColor = color;

        Distance d = new Distance();
        d.setDistance(length.getText(), length.getValue());
        length = d;

        Drawer.getInstance().addElem(this);

    }

    /**
     * Nodes getter
     *
     * @return nodes on the ends of the edge
     */
    public Node[] getNodes() {
        return new Node[]{n1, n2};
    }

    /**
     * Returns the node on the other side of the edge
     *
     * @param n node to get neighbour for
     * @return neighbour
     */
    public Node getNeighbour(Node n) {
        return n == n1 ? n2 : n1;
    }

    /**
     * Calculates needed start and end of the edge
     * Than connects two nodes
     *
     * @param node1 first node to connect
     * @param node2 second node to connect
     */
    public void connectNodes(Node node1, Node node2) {
        double dist = getDistance(node1.getCircle().getCenterX(), node1.getCircle().getCenterY(),
            node2.getCircle().getCenterX(), node2.getCircle().getCenterY());

        double[] startCordsNode = getStartCoordinates(node1.getCircle().getCenterX(), node1.getCircle().getCenterY(),
            node2.getCircle().getCenterX(), node2.getCircle().getCenterY(), dist, node2.getCircle().getRadius());

        double[] startCordsPretender = getStartCoordinates(node2.getCircle().getCenterX(),
            node2.getCircle().getCenterY(),
            node1.getCircle().getCenterX(), node1.getCircle().getCenterY(), dist, node1.getCircle().getRadius());

        if (startCordsNode[1] > startCordsPretender[1]) {
            this.setStartX(startCordsNode[0]);
            this.setStartY(startCordsNode[1]);


            this.setEndX(startCordsPretender[0]);
            this.setEndY(startCordsPretender[1]);

        } else {
            this.setEndX(startCordsNode[0]);
            this.setEndY(startCordsNode[1]);

            this.setStartX(startCordsPretender[0]);
            this.setStartY(startCordsPretender[1]);
        }
        relocateLabel();
    }

    /**
     * @param xPos    first point's x
     * @param yPos    first point's y
     * @param centerX second point's x
     * @param centerY second point's y
     * @return distance between 2 points
     */

    public static double getDistance(double xPos, double yPos, double centerX,
                                     double centerY) {
        return Math.sqrt((xPos - centerX) * (xPos - centerX) +
            (yPos - centerY) * (yPos - centerY));
    }

    /**
     * Counts start coordinates on circle
     *
     * @param xPos     mouse x pos
     * @param yPos     mouse y pos
     * @param centerX  circle centre x pos
     * @param centerY  circle centre y pos
     * @param distance distance between mouse and circle centre
     * @return start coordinates of the line
     */
    public static double[] getStartCoordinates(double xPos, double yPos, double centerX,
                                               double centerY, double distance, double radius) {

        double xSide = xPos - centerX;
        double ySide = yPos - centerY;

        return new double[]{centerX + xSide * radius / distance,
            centerY + ySide * radius / distance};
    }

    /**
     * Properly creates the edge
     *
     * @return whether the creation was successful
     */
    @Override
    public boolean create() {

        if (this.n1.addEdge(this.n2, this)) {
            this.n2.addEdge(this.n1, this);
        } else {
            Drawer.getInstance().removeElement(this);
            length.hide();
            return false;
        }

        try {
            Drawer.getInstance().addElem(this);
            if (Graph.getInstance().areDistancesShown()) {
                length.show();
            }
            setStroke(color);
            curColor = color;
            connectNodes(n1, n2);
        } catch (IllegalArgumentException ex) {
            System.out.println("Already drawn");
        }
        return true;
    }

    /**
     * Deletes the edge
     */
    @Override
    public void remove() {
        n1.removeNeighbour(n2);
        n2.removeNeighbour(n1);
        Drawer.getInstance().removeElement(this);
        Drawer.getInstance().removeElement(length);
    }

    @Override
    public Edge clone() throws CloneNotSupportedException {
        Edge clone = (Edge) super.clone();
        clone.setNodes(this.n1, this.n2);
        return clone;
    }

    /**
     * Shows the lengths label
     */
    void showLength() {
        relocateLabel();
        length.show();
    }

    /**
     * Hides the lengths label
     */
    void hideLength() {
        length.hide();
    }

    /**
     * Resets the label to default value
     */
    void resetLength() {
        length.reset();
    }

    public void changeLength(String text, double val) {
        length.setDistance(text, val);
    }

    public double getLength() {
        return length.getValue();
    }

    String getTextLength() {
        return length.getText();
    }

    /**
     * Moves length field after the edge
     */
    private void relocateLabel() {
        if (length == null) {
            return;
        }


        double coef = (getEndX() - getStartX()) /
            getDistance(getStartX(), getStartY(), getEndX(), getEndY());
        length.setLayoutX((this.getStartX() + this.getEndX()) / 2.0 + LABEL_GAP * (Math.sqrt(1 - coef * coef)));
        length.setLayoutY((this.getStartY() + this.getEndY()) / 2.0 + LABEL_GAP * coef);

        length.toFront();
    }

    /**
     * Sets mouse events handlers
     */
    private void setHandlers() {

        setOnMouseEntered(x -> {
            this.setStroke(Color.DARKGRAY);
            setStrokeWidth(2.5);
            getScene().setCursor(Cursor.HAND);
        });
        setOnMouseExited(x ->
        {
            this.setStrokeWidth(1.7);
            this.setStroke(curColor);
            getScene().setCursor(Cursor.DEFAULT);
        });


        this.setOnContextMenuRequested(contextMenuEvent -> {
            if (Filter.isEdgeStarted()) {
                return;
            }
            // System.out.println(contextMenuEvent.getSource());
            MenuManager.getEdgeMenu().bindElem((javafx.scene.Node) contextMenuEvent.getSource());
            MenuManager.getEdgeMenu().show(n1,
                contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
        });

        addEventFilter(MouseEvent.MOUSE_CLICKED, Filter.clickFilter);
    }

}
