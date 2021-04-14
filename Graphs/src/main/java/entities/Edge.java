package entities;

import java.io.Serializable;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.StrokeLineCap;
import lombok.Getter;
import lombok.Setter;
import main.Drawer;
import main.Filter;
import main.MenuManager;

/**
 * Represents an edge between 2 nodes
 */
@Getter
@Setter
public class Edge extends QuadCurve implements Undoable, Visitable,
    Serializable, Restorable {

    private static final double LABEL_GAP = 15;
    private static final Color color = Color.DIMGRAY;
    private static final Color selectedColor = Color.LIGHTBLUE;

    private Node n1;
    private Node n2;

    private boolean visited = false;

    private Distance length;
    private Anchor anchor;

    private transient Color curColor = color;

    public Edge(double v1, double v2, double v3, double v4) {

        setStartX(v1);
        setStartY(v2);
        setEndX(v3);
        setEndY(v4);

        setStroke(color);
        setStrokeWidth(1.7);

        setStrokeLineCap(StrokeLineCap.ROUND);
        setFill(null);
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
        relocateAnchor();
    }

    @Override
    public void restore() {

        this.setStrokeWidth(1.7);
        setHandlers();
        setStroke(color);
        setStrokeLineCap(StrokeLineCap.ROUND);
        setFill(null);

        curColor = color;

        Distance d = new Distance();
        createAnchor();

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
     * Than connects two nodes, ORDER IS IMPORTANT
     *
     * @param node1 first node to connect
     * @param node2 second node to connect
     */
    public void connectNodes(Node node1, Node node2, Node movingNode) {
        if (n1.equals(n2)) {
            connectLoop();
            return;
        }

        double dist = getDistance(node1.getCircle().getCenterX(), node1.getCircle().getCenterY(),
            node2.getCircle().getCenterX(), node2.getCircle().getCenterY());

        if (dist == 0) {
            return;
        }

        double[] startCordsNode = getStartCoordinates(node1.getCircle().getCenterX(), node1.getCircle().getCenterY(),
            node2.getCircle().getCenterX(), node2.getCircle().getCenterY(), dist, node2.getCircle().getRadius());

        double[] startCordsPretender = getStartCoordinates(
            node2.getCircle().getCenterX(),
            node2.getCircle().getCenterY(),
            node1.getCircle().getCenterX(),
            node1.getCircle().getCenterY(),
            dist,
            node1.getCircle().getRadius()
        );

        double oldStartX = getStartX();
        double oldStartY = getStartY();

        double oldEndX = getEndX();
        double oldEndY = getEndY();

        this.setStartX(startCordsNode[0]);
        this.setStartY(startCordsNode[1]);

        this.setEndX(startCordsPretender[0]);
        this.setEndY(startCordsPretender[1]);

        if (anchor != null) {
            double difX = 0, difY = 0, coefX = 0, coefY = 0;
            if (movingNode.equals(n1)) {

                System.out.println("end");
                difX = getEndX() - oldEndX;
                difY = getEndY() - oldEndY;

                coefX = Math.abs(anchor.getCenterX() - oldStartX) / Math.abs(oldEndX - oldStartX);
                coefY = Math.abs(anchor.getCenterY() - oldStartY) / Math.abs(oldEndY - oldStartY);


            } else {
                System.out.println("start");
                difX = getStartX() - oldStartX;
                difY = getStartY() - oldStartY;

                coefX = Math.abs(anchor.getCenterX() - oldEndX) / Math.abs(oldEndX - oldStartX);
                coefY = Math.abs(anchor.getCenterY() - oldEndY) / Math.abs(oldEndY - oldStartY);
            }
            relocateAnchor(anchor.getCenterX() + difX * coefX, anchor.getCenterY() + difY * coefY);
        } else {
            relocateLabel();
        }
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

        return new double[]{
            centerX + xSide * radius / distance,
            centerY + ySide * radius / distance
        };
    }

    /**
     * Properly creates the edge
     *
     * @return whether the creation was successful
     */
    @Override
    public boolean create() {

        if (this.n1.addEdge(this.n2, this)) {
            if (n1.getNum() != n2.getNum()) {
                this.n2.addEdge(this.n1, this);
            }
        } else {
            Drawer.getInstance().removeElement(this);
            Drawer.getInstance().removeElement(anchor);
            length.hide();
            return false;
        }

        try {
            Drawer.getInstance().addElem(this);
            Drawer.getInstance().addElem(anchor);
            if (Graph.areDistancesShown()) {
                length.show();
            }
            setStroke(color);
            curColor = color;
            connectNodes(n1, n2, n1);
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
        Drawer.getInstance().removeElement(anchor);
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

    public void relocateAnchor() {
        relocateAnchor((getStartX() + getEndX()) / 2.0, (getStartY() + getEndY()) / 2.0);
    }

    public void relocateAnchorWithCoefficient(double xCoef, double yCoef) {
        relocateAnchor((getStartX() + getEndX()) / 2.0, (getStartY() + getEndY()) / 2.0, xCoef, yCoef);
    }

    public void relocateAnchor(double centerX, double centerY) {
        relocateAnchor(centerX, centerY, 0.5, 0.5);
    }

    private void relocateAnchor(double centerX, double centerY, double xCoef, double yCoef) {
        double v1 = getStartX();
        double v3 = getEndX();
        double v2 = getStartY();
        double v4 = getEndY();

        if (
            Double.isNaN(centerX) || Double.isInfinite(centerX)
        ) {
            centerX = (v1 + v3) / 2.0;
        }

        if (Double.isNaN(centerY) || Double.isInfinite(centerY)) {
            centerY = (v2 + v4) / 2.0;
        }

        System.out.println("Relocate " + centerX + " " + centerY);


        setControlX(centerX / xCoef - v1 * xCoef - v3 * xCoef);
        setControlY(centerY / yCoef - v2 * yCoef - v4 * yCoef);

        if (anchor != null) {
            anchor.setNewCoordinatesSafe(centerX, centerY);
            relocateLabel();
        }
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

    public String getTextLength() {
        return length.getText();
    }

    public boolean isLoop() {
        return n1.getNum() == n2.getNum();
    }

    public void createAnchor() {
        this.anchor = new Anchor(
            controlXProperty(),
            controlYProperty(),
            this::relocateAnchor
        );
        Drawer.getInstance().addElem(anchor);
        if (isLoop()) {
//            this.toFront();
            anchor.setCenterX(n1.getCircle().getCenterX());
            anchor.setCenterY(n1.getCircle().getCenterY() - 5 * Node.RADIUS);
//            relocateAnchor((getStartX() + getEndX()) / 2.0, getStartY() - 2*Node.RADIUS);
        } else {
            relocateAnchor();
        }
    }

    private void connectLoop() {
        setStartX(300);
        setStartY(200);

        setEndX(400);
        setEndY(200);

//        System.out.println(n1.getCircle().getCenterX());
//        System.out.println(n1.getCircle().getCenterY());
//        double centerY = (n1.getCircle().getCenterY() - 4 * Node.RADIUS);
//
        System.out.println("set new");
        setControlX(350);
        setControlY(200);
//
//        System.out.println(getControlX());
//        System.out.println(getControlY());
//        setControlX(n1.getCircle().getCenterX() / 0.5 - getStartX() * 0.5 - getEndX() * 0.5);
//        setControlY(n1.getCircle().getCenterY() / 0.5 - getStartY() * 0.5 - getEndY() * 0.5);

//        if (anchor != null) {
//            anchor.setCenterX(n1.getCircle().getCenterX());
//            anchor.setCenterY(centerY);
//        }
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

        double x, y;

        if (anchor != null) {
            x = anchor.getCenterX();
            y = anchor.getCenterY();
        } else {
            x = (this.getStartX() + this.getEndX()) / 2.0;
            y = (this.getStartY() + this.getEndY()) / 2.0;
        }

        length.setLayoutX(x + LABEL_GAP * (Math.sqrt(1 - coef * coef)));
        length.setLayoutY(y + LABEL_GAP * coef);

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
            if (anchor != null) {
                anchor.show();
            }
        });
        setOnMouseExited(x ->
        {
            this.setStrokeWidth(1.7);
            this.setStroke(curColor);
            getScene().setCursor(Cursor.DEFAULT);
            if (anchor != null) {
                anchor.hide();
            }
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
