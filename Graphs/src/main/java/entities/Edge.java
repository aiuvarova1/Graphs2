package entities;

import javafx.animation.PathTransition;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Cursor;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import main.Drawer;
import main.Filter;
import main.MenuManager;
import main.PopupMessage;
import main.Visualizer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Represents an edge between 2 nodes
 */
public class Edge extends Line implements Undoable, Visitable,
        Serializable, Restorable {

    private Node n1;
    private Node n2;

    private boolean visited = false;

    private Distance length;

    private static final double LABEL_GAP = 15;
    private static final Color color = Color.DIMGRAY;
    private static final Color selectedColor = Color.LIGHTBLUE;

    private transient Color curColor = color;

    private HashMap<Node, double[]> nearestCoords;
    private transient ConcurrentHashMap<Integer, Point> pointsToProceed = new ConcurrentHashMap<>();
    private transient ConcurrentHashMap<Integer, Long> canVisualize = new ConcurrentHashMap<>();

    /**
     * Clears pointsToProceed before the new visualization
     */
    void resetProceed() {
        pointsToProceed.clear();
        canVisualize.clear();
    }

    public Edge(double v1, double v2, double v3, double v4) {

        super(v1, v2, v3, v4);
        this.setStrokeWidth(1.7);

        setStroke(color);

        nearestCoords = new HashMap<>();
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
     * Selects the edge as the beginning one
     */
    public void select() {
        setStroke(selectedColor);
        curColor = selectedColor;
    }

    /**
     * Deselects the edge as the beginning one
     */
    public void deselect() {
        setStroke(color);
        curColor = color;
    }

    /**
     * Returns the nearest to the given node edge end
     *
     * @param n node to find the end for
     * @return coordinates of the nearest edge end
     */
    public double[] getNodesNearest(Node n) {
        return nearestCoords.get(n);
    }

    private void removePoint(Point p) {
        p.hideEnabled();
        p.removePath();
        Drawer.getInstance().removeElement(p);
        Visualizer.decreasePoints();
    }

    /**
     * Renews the amplitude of the point and builds a new way (or creates the new point)
     *
     * @param n      node to which the point came
     * @param degree degree of the node n
     * @return instance of animation to proceed
     */
    PathTransition handlePoint(Node n, int degree) {

        try {
            if (InfiniteManager.canEdit() && canVisualize.containsKey(n.getNum())) {

                if (canVisualize.get(n.getNum()) + Visualizer.GAP + 20 > System.currentTimeMillis()) {
                    System.out.println(n.getNum());
                    if (pointsToProceed.containsKey(n.getNum())) {
                        System.out.println("deleted");
                        removePoint(pointsToProceed.get(n.getNum()));
                    }
                    return null;
                }
            }
            if (pointsToProceed.containsKey(n.getNum())) {

                pointsToProceed.get(n.getNum()).changeAmplitude(degree);
                if (pointsToProceed.get(n.getNum()).getAmplitude().equals("0")) {
                    pointsToProceed.get(n.getNum()).hideEnabled();
                    pointsToProceed.get(n.getNum()).removePath();
                    Drawer.getInstance().removeElement(pointsToProceed.get(n.getNum()));
                    pointsToProceed.remove(n.getNum());
                    Visualizer.decreasePoints();
                    return null;
                }
                pointsToProceed.get(n.getNum()).setDestination(getNeighbour(n));
                PathTransition p = pointsToProceed.get(n.getNum()).startPath(nearestCoords.get(n),
                        nearestCoords.get(getNeighbour(n)), length.getValue());
                pointsToProceed.remove(n.getNum());
                canVisualize.put(n.getNum(), System.currentTimeMillis());
                return p;

            } else {

                if (!Visualizer.checkOOM()) {
                    return null;
                }
                Point p = new Point(getNeighbour(n), this);

                p.setAmplitude(degree);

                if (p.getAmplitude().equals("0")) {
                    p.hideEnabled();
                    return null;
                }

                Visualizer.increasePoints();
                Drawer.getInstance().addElem(p);
                canVisualize.put(n.getNum(), System.currentTimeMillis());

                return p.startPath(nearestCoords.get(n), nearestCoords.get(getNeighbour(n)), length.getValue());
            }
        } catch (OutOfMemoryError e) {
            PopupMessage.showMessage("Not enough memory");
            Visualizer.stopVisualization();
            return null;
        }
    }

    /**
     * Adds a point to the "waiting list" for the next animation cycle
     *
     * @param n node which will accept the point
     * @param p point to accept
     */
    synchronized void addToProceed(Node n, Point p) {
        if (pointsToProceed.containsKey(n.getNum())) {
            System.out.println("DUPLICATE");
            removePoint(p);
            throw new IllegalArgumentException("Duplicate point");
        }

        pointsToProceed.put(n.getNum(), p);
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

        canVisualize.clear();
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

        pointsToProceed = new ConcurrentHashMap<>();
        canVisualize = new ConcurrentHashMap<>();


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

        nearestCoords.put(node1, startCordsPretender);
        nearestCoords.put(node2, startCordsNode);

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
            if (InfiniteManager.canEdit() && SimpleGraph.getInstance().areDistancesShown()) {
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
        if (InfiniteManager.canEdit() && SimpleGraph.getInstance().getStartEdge() == this) {
            SimpleGraph.getInstance().setStartEdge(null);
        }
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
     * @return whether the edge has been visited in dfs
     */
    public boolean isVisited() {
        return visited;
    }

    /**
     * Marks the edge as visited
     */
    public void visit() {
        visited = true;
    }

    /**
     * Marks the edge as not visited
     */
    public void unvisit() {
        visited = false;
    }


    /**
     * Moves length field after the edge
     */
    private void relocateLabel() {
        if (length == null || !InfiniteManager.canEdit()) {
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
            if (Filter.isEdgeStarted() || Visualizer.isRunning() || !InfiniteManager.canEdit()) {
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
