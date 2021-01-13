package main;

import entities.Edge;
import entities.InfiniteManager;
import entities.SimpleGraph;
import entities.Node;
import entities.Point;
import javafx.animation.PathTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.control.Label;


import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;


/**
 * Class that starts and stops amplitudes' distribution
 */

public class Visualizer {

    private static final int MAX_POINTS = 2000;
    public static int GAP = 300;

    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    private static HashSet<PathTransition> animations = new HashSet<>();

    private static volatile boolean isRunning = false;
    private static boolean enabledGIF = false;

    private static boolean numeric = true;
    private static boolean colour = false;
    private static boolean arrows = false;

    private static IntegerProperty curNumOfPoints = new SimpleIntegerProperty(1);
    private static boolean needStartPeriod = false;
    private static long startTime;
    private static long periodTime = -1;

    private static int timeout;
    private static Node globalStart;

    private static ChangeListener<Number> observer =
            (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
            {
                if ((int) newValue == 1) {
                    needStartPeriod = true;
                }
            };

    public static void bindPointsLabel(Label label) {
        curNumOfPoints.addListener((observable, oldValue, newValue) -> {
            label.setText("Points: " + curNumOfPoints.get());
        });
    }

    /**
     * Current lowest possible value of the amplitude
     **/
    private static LongProperty lowerBound = new SimpleLongProperty(0);

    /**
     * Current biggest possible value of the amplitude
     **/
    private static LongProperty upperBound = new SimpleLongProperty(1);

    public static void enableGif(boolean enable){
        enabledGIF = enable;
    }


    /**
     * Submits new task to the common pool
     *
     * @param t task to submit
     */
    public static void runTask(Task t) {
        threadPool.submit(t);
    }

    public static int getTimeout(){
        return timeout;
    }

    static void setNumeric(boolean val) {
        numeric = val;
    }

    public static boolean isNumeric() {
        return numeric;
    }

    static void setColour(boolean val) {
        colour = val;
    }

    public static boolean isColoured() {
        return colour;
    }

    static void setArrows(boolean val) {
        arrows = val;
    }

    public static boolean isArrows() {
        return arrows;
    }


    public static void increasePoints(){
        curNumOfPoints.set(curNumOfPoints.get() + 1);
    }

    public static void decreasePoints(){
        curNumOfPoints.set(curNumOfPoints.get() - 1);
    }

    /**
     * Controls the number of points in order not to fail with
     * OutOfMemoryException.
     *
     * @return whether the limit of points is exceeded
     */
    public static boolean checkOOM() {
        return curNumOfPoints.get() < MAX_POINTS;
    }

    public static LongProperty getLowerBound() {
        return lowerBound;
    }

    public static LongProperty getUpperBound() {
        return upperBound;
    }


    /**
     * Checks whether an upper/lower bound changes after a new amplitude
     * appearance.
     *
     * @param val       new amplitude to check
     * @param pretender whether the point has amplitude 1
     */
    public static void checkMinMaxAmplitudes(double val, boolean pretender, Node destination) {

        //val = Math.round(val);

        if (val > upperBound.get()) {
            upperBound.set((long) (val + 1));
        }

        if (val < lowerBound.get()) {
            lowerBound.set((long) (val - 1));
        }

        if (needStartPeriod && pretender && destination.equals(globalStart)) {

            if (periodTime < 0) {
                periodTime = (System.currentTimeMillis() - startTime) / 1000;
            }
            System.out.println("period " + periodTime);
            PopupMessage.showMessage("The period lasted " + periodTime + " sec");
            if (enabledGIF && GIFMaker.isTimeDefault()) {
                GIFMaker.stopTimer();
            }
            needStartPeriod = false;
        }
    }

    /**
     * Binds corresponding colour labels to properties
     *
     * @param lower lower bound of amplitudes
     * @param upper upper bound of amplitudes
     */
    static void bindBounds(Label lower, Label upper) {
        lowerBound.addListener((observable, oldValue, newValue) ->
                lower.setText(Long.toString((newValue.intValue()))));
        upperBound.addListener((observable, oldValue, newValue) ->
                upper.setText(Long.toString((newValue.intValue()))));
    }


    /**
     * Prepares and starts the distribution
     *
     * @param startEdge edge from which the first point goes
     * @param startNode node from which the first point goes
     */
    public static void startVisualization(Edge startEdge, Node startNode) {

        if(InfiniteManager.getCurrentType() == InfiniteManager.Type.SIMPLE)
            GAP = 250;
        else
            GAP = 350;

       // System.out.println("start v");
        globalStart = startNode;
        needStartPeriod = false;
        periodTime = -1;

        curNumOfPoints.set(1);

        lowerBound.set(0);
        upperBound.set(1);

        isRunning = true;
        double[] start;
        double[] end;

        start = startEdge.getNodesNearest(startNode);
        end = startEdge.getNodesNearest(startEdge.getNeighbour(startNode));

        Point point = new Point(startEdge.getNeighbour(startNode), startEdge);
        Drawer.getInstance().addElem(point);

        PathTransition par = point.startPath(start, end, startEdge.getLength());
        animations.add(par);

        long now = System.currentTimeMillis();

        timeout =(int) now%1000;
        par.play();
        startTime = System.currentTimeMillis();
        System.out.println("Start " + now + " timeout " + timeout);
        curNumOfPoints.addListener(observer);

        if(enabledGIF)
            GIFMaker.takeSnapshots();

    }

    public static void runLineVisualization(Point p, Runnable supplier)
    {
        isRunning = true;

//        PathTransition transition = new PathTransition();
//        transition.setNode(p);
//
//        LineTo line = new LineTo(startX, startY);
//        MoveTo move = new MoveTo()

        supplier.run();

        if(enabledGIF)
            GIFMaker.takeSnapshots();
    }

    public static void stopLineVisualization(){
        isRunning = false;

        if(enabledGIF) {
            GIFMaker.createGif();
            Drawer.getInstance().enableDialog(false);
            PopupMessage.unfixMessage();
        }

        enabledGIF = false;
    }

    /**
     * Adds animation of the new point to the list
     *
     * @param p animation to add
     */
    public static void addPath(PathTransition p) {
        animations.add(p);
    }

    public static void removePath(PathTransition p)
    {
        animations.remove(p);
    }


    /**
     * Stops the visualization and refreshes the needed data
     */
    public static void stopVisualization() {
        isRunning = false;
        curNumOfPoints.removeListener(observer);

        for (PathTransition p : animations)
            p.stop();
        animations.clear();

        threadPool.shutdownNow();
        threadPool = Executors.newCachedThreadPool();

        Drawer.getInstance().removePoints();
        InfiniteManager.resetNodes();

        if(enabledGIF) {
            GIFMaker.createGif();
            Drawer.getInstance().enableDialog(false);
            PopupMessage.unfixMessage();
        }

        enabledGIF = false;
    }

    /**
     * Whether the visualization is running now
     *
     * @return isRunning
     */
    public static boolean isRunning() {
        return isRunning;
    }

}
