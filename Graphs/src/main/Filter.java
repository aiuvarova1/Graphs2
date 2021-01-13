package main;

import entities.*;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

/**
 * Filters clicks and some other user's input
 */
public class Filter {

    private static String SELECTED_BUTTON = "-fx-background-color: #ebebeb;" + "-fx-font-size: 18px;"
            + "-fx-font-family: \"Constantia\";";

    private static String UNSELECTED_BUTTON = "-fx-background-color: #f5f5f5;" + "-fx-font-size: 17px;"
            + "-fx-font-family: \"Constantia\";";

    private static boolean dragging = false;

    private static boolean edgeStarted = false;
    private static boolean editing = false;

    private static Node pretender;
    private static Edge edgePretender;

    private static final int CURSOR_GAP = 5;

    public static boolean isEdgeStarted(){
        return edgeStarted;
    }

    public static boolean isEditing(){
        return editing;
    }

    public static void endEdit(){
        editing = false;
    }
    /**
     * Distinguishes dragging from pane click
     */
    public static final EventHandler<MouseEvent> dragFilter = event -> {

        if (editing || Visualizer.isRunning() || !InfiniteManager.canEdit()) {
            return;
        }
        if (event.getEventType() == MouseEvent.MOUSE_DRAGGED &&
                event.getButton() == MouseButton.PRIMARY) {
            dragging = true;
        } else if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            if (dragging) {
                event.consume();
            }
            dragging = false;
        }
    };

    /**
     * Distinguishes pane clicks from node clicks
     */

    public static final EventHandler<MouseEvent> clickFilter = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
//            System.out.println(event.getSource().getClass());
//            System.out.println(event.getTarget());

            if (Visualizer.isRunning() || !InfiniteManager.canEdit()) {
                event.consume();
                return;
            }

            if (editing && (event.getTarget().getClass() != Text.class ||
                    event.getSource().getClass() == Node.class)) {
                event.consume();
                Drawer.getInstance().setFocus();
                return;
            }

            if (event.getSource().getClass() == Node.class) {
                event.consume();

                if (event.getButton() == MouseButton.PRIMARY) {
                    if (!edgeStarted) {
                        if (MenuManager.getEdgeMenu().isShowing()) return;

                        edgeStarted = true;
                        pretender = (Node) event.getSource();

                        edgePretender = new Edge(0, 0, 0, 0);
                        edgePretender.setVisible(false);
                        Drawer.getInstance().setMoveHandler(edgeMoveHandler);
                        Drawer.getInstance().addElem(edgePretender);
                    } else {
                        //System.out.println("here");
                        event.consume();
                        edgeStarted = false;
                        Node node = (Node) event.getSource();
                        if (node == pretender) {
                            Drawer.getInstance().removeMoveHandler();
                            return;
                        }

                        edgePretender.setNodes(node, pretender);
                        edgePretender.connectNodes(node, pretender);
                        //Graph.getInstance().connectNodes(node, pretender, edgePretender);

                        Invoker.getInstance().createElement(edgePretender);
                        //edgePretender.create();
                        Drawer.getInstance().removeMoveHandler();
                    }
                }
            } else if (edgeStarted && event.getTarget().getClass() == AnchorPane.class) {

                // System.out.println(event.getTarget().getClass() + " target");
                event.consume();
                removeStartedEdge();

            } else if (event.getSource().getClass() == Edge.class) {
                event.consume();

            } else if (event.getSource().getClass() == Distance.class) {
                event.consume();
                System.out.println("and here");

                Distance curDist = (Distance) event.getSource();
                curDist.showInput();
                editing = true;
                if (edgeStarted)
                    removeStartedEdge();
            }
        }
    };

    private static void removeStartedEdge(){
        edgeStarted = false;

        edgePretender.setVisible(false);
        Drawer.getInstance().removeElement(edgePretender);

        Drawer.getInstance().removeMoveHandler();
    }


    /**
     * Controls potential edge's movements
     */
    private static final EventHandler<MouseEvent> edgeMoveHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {

            double xPos = event.getX();
            double yPos = event.getY();

            Bounds b = Drawer.getInstance().getBounds();

            double dist = Edge.getDistance(xPos, yPos, pretender.getCircle().getCenterX(),
                    pretender.getCircle().getCenterY());

            if (dist > Node.RADIUS + CURSOR_GAP && isInBounds(event.getX(), event.getY(), b)) {
                double[] cords = Edge.getStartCoordinates(xPos, yPos, pretender.getCircle().getCenterX(),
                        pretender.getCircle().getCenterY(), dist, pretender.getCircle().getRadius());

                int signX = cords[0] <= event.getX() ? -1 : 1;
                int signY = cords[1] <= event.getY() ? -1 : 1;
                edgePretender.setStartX(cords[0]);
                edgePretender.setEndX(event.getX() + CURSOR_GAP * signX);

                edgePretender.setStartY(cords[1]);
                edgePretender.setEndY(event.getY() + CURSOR_GAP * signY);

                edgePretender.setVisible(true);
            } else
                edgePretender.setVisible(false);
        }

        private boolean isInBounds(double x, double y, Bounds b) {
            return x > b.getMinX() && x < b.getMaxX() &&
                    y > b.getMinY() && y < b.getMaxY();
        }
    };

    /**
     * Controls button style on mouse enter
     */
    static final EventHandler<MouseEvent> buttonEnterHandler = event -> {

        Button b = (Button) event.getSource();
        b.setStyle(SELECTED_BUTTON);
        ((Button) event.getSource()).getScene().setCursor(Cursor.HAND);
    };

    /**
     * Controls button style on mouse exit
     */
    static final EventHandler<MouseEvent> buttonExitHandler = event -> {

        Button b = (Button) event.getSource();
        b.setStyle(UNSELECTED_BUTTON);
        ((Button) event.getSource()).getScene().setCursor(Cursor.DEFAULT);
    };


}
