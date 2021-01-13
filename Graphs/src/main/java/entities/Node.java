package entities;

import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import main.Drawer;
import main.Filter;
import main.MenuManager;
import main.Visualizer;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Represents one node of the graph
 */
public class Node extends StackPane implements
        Undoable, Visitable, Serializable, Restorable {

    public static final double RADIUS = 16;

    private int num;
    private boolean visited;

    private ArrayList<Edge> edges;
    private double[] initialPosition;
    private double[] curPosition = new double[2];

    private static final Color color = Color.WHITE;
    private static final Color selectedColor = Color.LIGHTBLUE;

    private transient Color curColor = color;

    public Circle getCircle() {
        return (Circle) getChildren().get(0);
    }

    volatile transient BooleanProperty processed;
    volatile transient AtomicInteger guests = new AtomicInteger(0);
    private volatile double amplitudesSum = 0;


    double getAmplitudesSum() {
        return amplitudesSum;
    }

    synchronized void increaseAmplitudesSum(double amplitude) {
        amplitudesSum += amplitude;
    }


    public Node(int num) {

        edges = new ArrayList<>(5);
        this.num = num;
        initialPosition = new double[]{getLayoutX(), getLayoutY()};

        setId("" + num);
        setHandlers();

    }


    /**
     * Resets info needed for visualization
     */
    void resetNode() {
        guests.set(0);
        processed.set(false);
        amplitudesSum = 0;
        for (Edge e : edges)
            e.resetProceed();

    }

    void checkMinEdge(){
        for (Edge e : edges) {
            SimpleGraph.getInstance().setMin(e.getLength());
            if (e.getLength() == Double.MAX_VALUE) {
                throw new IllegalArgumentException("Not all distances are set");
            }
        }
    }

    public int getNum(){
        return num;
    }


    /**
     * Selects the node as the start one
     */
    public void select() {
        getCircle().setFill(selectedColor);
        curColor = selectedColor;
    }

    /**
     * Deselects the node as the start one
     */
    public void deselect() {
        getCircle().setFill(color);
        curColor = color;
    }



    /**
     * Gets list of neighbours through passing the list of edges
     *
     * @return list of neighbour nodes
     */
    Set<Node> getNeighbours() {
        Set<Node> nodes = new HashSet<>(edges.size());
        for (Edge e : edges) {
            nodes.add(e.getNeighbour(this));
        }
        return nodes;
    }

    /**
     * @return the list of nodes' edges
     */
    public ArrayList<Edge> getEdges() {
        return edges;
    }

    /**
     * Renews number label
     *
     * @param num new number
     */
    void renewNum(int num) {
        this.num = num;
        setId("" + num);
        setText();
    }

    /**
     * Rescales the node by x-axis
     *
     * @param scale scale param
     */
    void rescaleX(double scale) {

        Bounds b = Drawer.getInstance().getBounds();

        double oldX = getLayoutX();


        if (getLayoutX() * scale > b.getMaxX())
            setLayoutX(b.getMaxX() - 2 * RADIUS - 2 * Drawer.BOUNDS_GAP);
        else
            setLayoutX(getLayoutX() * scale);

        relocate(getLayoutX(), getLayoutY());
        relocateCircleCenter(getLayoutX(), getLayoutY());

        if (!(oldX == getLayoutX())) {
            recalculateEdges();
        }
    }


    /**
     * Rescales the node by y-axis
     *
     * @param scale scale param
     */
    void rescaleY(double scale) {
        Bounds b = Drawer.getInstance().getBounds();

        double oldY = getLayoutY();
        //System.out.println("scale" + circle.getLayoutY() + " " + b.getMaxY());
        if (getLayoutY() * scale > b.getMaxY())
            setLayoutY(b.getMaxY() - 2 * RADIUS - 2 * Drawer.BOUNDS_GAP);
        else
            setLayoutY(getLayoutY() * scale);

        relocate(getLayoutX(), getLayoutY());
        relocateCircleCenter(getLayoutX(), getLayoutY());

        if (!(oldY == getLayoutY())) {
            recalculateEdges();
        }
    }


    /**
     * @return Nodes' text on the label
     */
    public Text getText() {
        return (Text) getChildren().get(1);
    }

    /**
     * Adds an edge to the list
     *
     * @param neighbour node on the other end of the edge
     * @param edge      edge to add
     * @return whether the adding was successful
     */
    Boolean addEdge(Node neighbour, Edge edge) {
        //NO MULTIPLE EDGES
        for (Edge e : edges) {

            if (e.getNeighbour(this).equals(neighbour)) {
                return false;
            }
        }

        edges.add(edge);
        return true;
    }

    /**
     * Removes the node from the neighbours and destructs edges
     *
     * @param n number of the node to remove
     */
    void removeNeighbour(Node n) {

        Edge toRemove = null;
        for (Edge e : edges) {
            if (e.getNeighbour(this) == n) {
                toRemove = e;
            }
        }
        edges.remove(toRemove);
    }

    /**
     * Returns the number of the node
     *
     * @return node number
     */
    @Override
    public String toString() {
        return "" + num;
    }

    /**
     * Sets circle centre to the given point
     *
     * @param x center's x
     * @param y center's y
     */
    private void relocateCircleCenter(double x, double y) {
        getCircle().setCenterX(x + getCircle().getRadius());
        getCircle().setCenterY(y + getCircle().getRadius());
    }


    /**
     * Fixes node's position after dragging
     *
     * @param xPos x final coordinate
     * @param yPos y final coordinate
     */
    public void fixPosition(double xPos, double yPos) {
        setLayoutX(xPos);
        setTranslateX(0);

        setLayoutY(yPos);
        setTranslateY(0);

        curPosition[0] = xPos;
        curPosition[1] = yPos;

        relocateCircleCenter(getLayoutX(), getLayoutY());
    }

    /**
     * Removes the node from the drawing ares
     */
    @Override
    public void remove() {

        if (SimpleGraph.getInstance().getStartNode() == this) {
            SimpleGraph.getInstance().setStartNode(null);
        }

        SimpleGraph.getInstance().removeNode(this);
    }


    /**
     * (Re)creates the node
     *
     * @return whether succeeded in creation
     */
    @Override
    public boolean create() {
        SimpleGraph.getInstance().addNode(this);

        try {
            Drawer.getInstance().addElem(this);
        } catch (IllegalArgumentException ex) {
            System.out.println("Already drawn node");
        }
        SimpleGraph.getInstance().refreshLabels(this);
        getCircle().setFill(color);
        curColor = color;

        return true;
    }

    public boolean isVisited() {
        return visited;
    }

    public void visit() {
        visited = true;
    }

    /**
     * Marks the node as not visited (for dfs)
     */
    public void unvisit() {
        visited = false;

        for (Edge e : edges)
            e.unvisit();
    }

    /**
     * Shows labels of all edges
     */
    void showLengths() {
        handleEdges(Edge::showLength);
    }

    /**
     * Hides labels of all edges
     */
    void hideLengths() {
        handleEdges(Edge::hideLength);
    }

    /**
     * Resets edges' lengths
     */
    void resetLengths() {
        handleEdges(Edge::resetLength);
    }

    @Override
    public void restore(){

        Circle circle = new Circle(RADIUS, Color.WHITE);
        circle.setStroke(Color.BLACK);
        circle.addEventFilter(MouseEvent.MOUSE_DRAGGED, Filter.dragFilter);

        this.getChildren().add(circle);

        Text numText =new Text("" + num);
        numText.setStyle(Drawer.NODE_TEXT);

        this.getChildren().add(numText);
        curColor = color;

        setHandlers();
        fixPosition(curPosition[0], curPosition[1]);

        Drawer.getInstance().addElem(this);
        handleEdges(Edge::restore);
    }

    /**
     * Visits all edges and handles them
     *
     * @param handler method to handle with each edge
     */
    private void handleEdges(Consumer<Edge> handler) {
        for (Edge e : edges) {
            if (!e.isVisited()) {
                e.visit();
                handler.accept(e);
            }
        }
    }

    /**
     * Calls redrawing for all edges
     */
    private void recalculateEdges() {
        for (Edge e : edges) {
            e.connectNodes(this, e.getNeighbour(this));
        }
    }


    /**
     * Checks whether the node will cross the bounds of the drawing area after moving
     * on cursor position
     *
     * @param event contains info about cursor
     * @return returns {xBound is crossed, yBound is crossed}
     */
    private boolean[] checkBoundsCrossed(MouseEvent event) {

        Bounds b = Drawer.getInstance().getBounds();

        boolean crossedBoundsX = false;
        boolean crossedBoundsY = false;
        if (getTranslateX() + event.getX() - RADIUS + getLayoutX() < 0) {
//            System.out.println("bounds " + b.getMinX() + " " + circle.getTranslateX() + " " + circle.getLayoutX());
            setLayoutX(0);
            setTranslateX(0);
            crossedBoundsX = true;

            //was 2.5
        } else if (getTranslateX() + event.getX() + 2 * RADIUS + getLayoutX() > b.getMaxX()) {
            setLayoutX(b.getMaxX() - 2 * RADIUS - Drawer.BOUNDS_GAP);
            setTranslateX(0);
            //System.out.println("crossed " + b.getMaxX() + " " + circle.getLayoutX() + " " + circle.getTranslateX());
            crossedBoundsX = true;
        }

        if (getTranslateY() + event.getY() - RADIUS + getLayoutY() < b.getMinY()) {
            setLayoutY(10);
            setTranslateY(0);
            crossedBoundsY = true;
        } else if (getTranslateY() + event.getY() + 2 * RADIUS + getLayoutY() > b.getMaxY()) {

            setLayoutY(b.getMaxY() - 2 * RADIUS - Drawer.BOUNDS_GAP);
            setTranslateY(0);
            // System.out.println("crossed " + b.getMaxY() + " " + circle.getLayoutY() + " " + circle.getTranslateY());
            crossedBoundsY = true;
        }

        return new boolean[]{crossedBoundsX, crossedBoundsY};
    }

    /**
     * Sets new text according to the changed node number
     */
    private void setText() {
        //Text numText = (Text) circle.getChildren().get(1);
        getText().setText("" + num);

    }


    /**
     * Sets filters and handlers for mouse events
     * (dragging, clicking, etc)
     */
    private void setHandlers() {
        addEventFilter(MouseEvent.MOUSE_CLICKED, Filter.clickFilter);
        addEventFilter(MouseEvent.MOUSE_DRAGGED, Filter.dragFilter);

        setOnMousePressed(event -> {

            if (Filter.isEdgeStarted()
                    || Filter.isEditing()
                    || event.isSecondaryButtonDown()
                    || Visualizer.isRunning()
                    || !InfiniteManager.canEdit()) {
                return;
            }

            initialPosition[0] = getLayoutX();
            initialPosition[1] = getLayoutY();
            getScene().setCursor(Cursor.MOVE);
            toFront();

            MenuManager.getNodeMenu().hide();
        });

        this.setOnContextMenuRequested(contextMenuEvent -> {
            if (Filter.isEdgeStarted() || Visualizer.isRunning() || !InfiniteManager.canEdit()) {
                return;
            }
            MenuManager.getNodeMenu().bindElem((javafx.scene.Node) contextMenuEvent.getSource());
            MenuManager.getNodeMenu().show((javafx.scene.Node) contextMenuEvent.getSource(),
                    contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
        });


        setOnMouseReleased(mouseEvent -> {
            if (mouseEvent.getButton() != MouseButton.PRIMARY ||
                    Visualizer.isRunning() || !InfiniteManager.canEdit()) {
                return;
            }

            getScene().setCursor(Cursor.HAND);
          //  Node n = (Node) mouseEvent.getSource();
            // Invoker.getInstance().moveElement(n, initialPosition,new double[]{getLayoutX() + getTranslateX(),
            // getLayoutY() + getTranslateY()});
            fixPosition(getLayoutX() + getTranslateX(), getLayoutY() + getTranslateY());

        });
        setOnMouseDragged(event -> {

            if (Filter.isEdgeStarted() || event.getButton() != MouseButton.PRIMARY
                    || Visualizer.isRunning() || !InfiniteManager.canEdit()) {
                return;
            }

            boolean[] crossedBounds = checkBoundsCrossed(event);
            if (!crossedBounds[0])
                setTranslateX(getTranslateX() + event.getX() - RADIUS);
            if (!crossedBounds[1])
                setTranslateY(getTranslateY() + event.getY() - RADIUS);

            recalculateEdges();

            relocateCircleCenter(getLayoutX() + getTranslateX(),
                    getLayoutY() + getTranslateY());
        });
        setOnMouseEntered(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.HAND);
            }
            getCircle().setFill(Color.AZURE);
        });
        setOnMouseExited(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.DEFAULT);
            }
            getCircle().setFill(curColor);
        });

        processed = new SimpleBooleanProperty(false);
        guests = new AtomicInteger(0);

        processed.addListener((observable, oldValue, newValue) -> {
            if (oldValue) return;
            try {

                Visualizer.runTask(new Task() {
                    @Override
                    protected Object call() throws Exception {

                        synchronized (Node.this) {
                            try {
                                System.err.println(java.time.LocalDateTime.now());
                                Node.this.wait(Visualizer.GAP - 5);
                            } catch (InterruptedException ex) {
                                System.out.println("Interrupted in waiting points");
                                return null;
                            }
                            Platform.runLater(() -> {
                                handlePoints();
                                processed.setValue(false);
                                amplitudesSum = 0;
                            });


                        }
                        return null;
                    }
                });

            } catch (IllegalStateException e) {
                System.out.println("illegal state");
            }
        });

    }

    /**
     * Proceeds all points which came to the node and restarts their animations
     *
     */
    private void handlePoints() {

        if (!Visualizer.isRunning())
            return;

        ArrayList<PathTransition> toPlay = new ArrayList<>();
        PathTransition p;
        for (Edge e : edges) {
            p = e.handlePoint(this, edges.size());
            if(p!=null) {
                Visualizer.addPath(p);
                toPlay.add(p);
            }
        }
        if (!Visualizer.isRunning())
            return;

        for (PathTransition path : toPlay) {
            path.play();
        }
        System.out.println(LocalDateTime.now() + " all points started");

    }
}
