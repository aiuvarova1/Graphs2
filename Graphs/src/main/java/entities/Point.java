package entities;

import javax.swing.BoundedRangeModel;

import javafx.animation.PathTransition;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.util.Duration;
import main.Drawer;
import main.Formatter;
import main.Visualizer;


/**
 * Represents the point moving along the edge
 */
public class Point extends Circle {

    static final int RADIUS = 9;
    static int LATTICE_RADIUS = 5;
    private static final int SHIFT = 10;
    private static final Color BASE_COLOR = Color.GRAY;

    private float amplitude;
    private Node destination;

    /**Animation object**/
    private PathTransition pathTransition;

    /**Edge on which the point exists**/
    private Edge edge;

    private LineTo line;
    private Path path;
    private MoveTo move;

    /**Text representation of the amplitude**/
    private Text numAmplitude;

    private Arrow arrow;

    public Point() {

        super(RADIUS, BASE_COLOR);

        numAmplitude = new Text();
        arrow = new Arrow(getCenterX(), getCenterY() - RADIUS);

        setStroke(Color.BLACK);
        amplitude = 1;

        pathTransition = new PathTransition();
        line = new LineTo(0, 0);
        path = new Path();
        move = new MoveTo();

        numAmplitude.setText("1");
        numAmplitude.getStyleClass().add("pointLabel");

        if(Visualizer.isNumeric())
            showNumbers();

        if(Visualizer.isColoured())
            showColour();

        if(Visualizer.isArrows())
            showArrow();

        setBindings();
    }

    public Point(Node n, Edge e) {
        this();
        destination = n;
        this.edge = e;

        if(InfiniteManager.getCurrentType() == InfiniteManager.Type.LATTICE)
            setRadius(LATTICE_RADIUS*n.getCircle().getRadius()/LatticeGraph.startRadius);
    }

    public String getAmplitude(){
        return numAmplitude.getText();
    }


    public void showAttributes(){
        numAmplitude.setTranslateX(this.getCenterX() + SHIFT);
        arrow.setArrowTranslateX(this.getCenterX());

        numAmplitude.setTranslateY(this.getCenterY() + SHIFT);
        arrow.setArrowTranslateY(this.getCenterY());
    }
    /**
     * Shows numeric value of the amplitude
     */
    public void showNumbers() {
        Drawer.getInstance().addElem(numAmplitude);
    }

    /**
     * Hides numeric value of the amplitude
     */
    public void hideNumbers() {
        Drawer.getInstance().removeElement(numAmplitude);
    }

    /**
     * Applies corresponding color to the point
     */
    public void showColour() {
        fillProperty().set(calculateInterpolation());

        fillProperty().bind(Bindings.createObjectBinding(this::calculateInterpolation,
                Visualizer.getLowerBound(), Visualizer.getUpperBound(),numAmplitude.textProperty()));
    }

    /**
     * Returns base point's color
     */
    public void hideColour() {
        fillProperty().unbind();
        fillProperty().set(BASE_COLOR);
    }

    /**
     * Shows a vector of an amplitude
     */
    public void showArrow() {
        arrow.addArrow();
        arrow.redrawArrow(amplitude);
    }

    /**
     * Hides the vector of an amplitude
     */
    public void hideArrow() {
        arrow.removeArrow();
    }

    /**
     * Hides amplitude's number and arrow
     */
    public void hideEnabled(){
        hideNumbers();
        hideArrow();
    }


    /**
     * Sets the new destination node
     *
     * @param n node that will be reached in the end of the way
     */
    void setDestination(Node n) {
        destination = n;
    }

    /**
     * Changes amplitude with the given rule
     *
     * @param degree degree of the node
     */
    void changeAmplitude(int degree) {
        if (degree == 1)
            amplitude = amplitude != 0 ? -amplitude : 0;
        else
            amplitude = ( 2.0f / degree - 1) * amplitude
                    + (float)(destination.getAmplitudesSum() - amplitude) * (2.0f / degree);
        updateInfo(destination);
    }

    /**
     * Sets the amplitude for the new point
     *
     * @param degree degree of the node
     */
    void setAmplitude(int degree) {
        amplitude = (float)edge.getNeighbour(destination).getAmplitudesSum() * (2.0f / degree);
        updateInfo(edge.getNeighbour(destination));
    }

    public void removePath(){
        Visualizer.removePath(pathTransition);
    }


    /**
     * Creates the animation instance for the point
     *
     * @param start start coordinates
     * @param end   end coordinates
     * @return instance of animation
     */
    public PathTransition startPath(double[] start, double[] end, double startEdge) {

        // pathTransition.setDuration(Duration.millis(2000));
        path.getElements().clear();


        line.setX(end[0]);
        line.setY(end[1]);

        move.setX(start[0]);
        move.setY(start[1]);

        numAmplitude.setTranslateX(start[0]);
        numAmplitude.setTranslateY(start[1]);

        path.getElements().add(move);
        path.getElements().add(line);

        pathTransition.setPath(path);

        Duration forSimple = Duration.millis(startEdge / SimpleGraph.getInstance().getCurMinEdge() * 2000 - Visualizer.GAP);
        final Duration duration = InfiniteManager.canEdit() ?
                forSimple :
                new Duration(2000 - Visualizer.GAP);
        pathTransition.setDuration(duration);

        pathTransition.setNode(this);

        translateXProperty().setValue(start[0]);
        translateYProperty().setValue(start[1]);

        return pathTransition;
    }


    /**
     * Notifies the edge that this point must be proceeded
     */
    private void setPointToEdge() {
        edge.addToProceed(destination, this);
    }

    /**
     * Calculates corresponding to the amplitude Color
     * @return current amplitude's color
     */
    private Color calculateInterpolation(){
        return Color.ROYALBLUE.interpolate(Color.INDIANRED,
                (amplitude - Visualizer.getLowerBound().get())/
                        (Visualizer.getUpperBound().get() -
                                Visualizer.getLowerBound().get()));
    }

    /**
     * Binds needed properties
     */
    private void setBindings(){

        pathTransition.setOnFinished(event -> {

            Visualizer.runTask(new Task() {
                @Override
                protected Object call() {

                    System.out.println("arrival to " + destination.getNum() + " at " + java.time.LocalDateTime.now());
                    if (!destination.processed.get())
                        destination.processed.setValue(true);
                    try {
                        setPointToEdge();
                    }catch(IllegalArgumentException ex)
                    {
                        System.err.println("Duplicate point");
                        return null;
                    }
                    destination.increaseAmplitudesSum(amplitude);
                    //destination.guests.incrementAndGet();
                    return null;
                }
            });
        });

        translateXProperty().addListener(((observable, oldValue, newValue) ->
        {
            numAmplitude.setTranslateX(newValue.doubleValue() + SHIFT);
            arrow.setArrowTranslateX(newValue.doubleValue());
        }));

        translateYProperty().addListener(((observable, oldValue, newValue) ->
        {
            numAmplitude.setTranslateY(newValue.doubleValue() + SHIFT);
            arrow.setArrowTranslateY(newValue.doubleValue());
        }));

        numAmplitude.textProperty().addListener(((observable, oldValue, newValue) ->
        {
            if(Visualizer.isArrows())
                arrow.redrawArrow(Double.valueOf(newValue));
        }
        ));
    }

    /**
     * Updates all needed information after amplitude's updating
     */
    private void updateInfo(Node toCheck){
        numAmplitude.setText(Formatter.format(amplitude));

        Visualizer.checkMinMaxAmplitudes(amplitude, numAmplitude.getText().equals("1"), toCheck);

    }
}
