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
public class EventFilter {

    private static boolean dragging = false;

    private static boolean edgeStarted = false;
    private static boolean editing = false;

    private static Node nodeWithStartedEdge;
    private static Edge startedEdge;

    private static final int CURSOR_GAP = 5;

    public static boolean isEdgeStarted() {
        return edgeStarted;
    }

    public static boolean isEditing() {
        return editing;
    }

    public static void endEdit() {
        editing = false;
    }

    /**
     * Distinguishes dragging from pane click
     */
    public static final EventHandler<MouseEvent> dragFilter = event -> {

        if (editing) {
            return;
        }
        if (event.getEventType() == MouseEvent.MOUSE_DRAGGED &&
            event.getButton() == MouseButton.PRIMARY
        ) {
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

    public static final EventHandler<MouseEvent> clickFilter = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

            if (editing && (event.getTarget().getClass() != Text.class ||
                event.getSource().getClass() == Node.class)) {
                event.consume();
                DrawingAreaController.getInstance().setFocus();
                return;
            }

            if (event.getSource().getClass() == Node.class) {
                event.consume();

                if (event.getButton() == MouseButton.PRIMARY) {
                    if (!edgeStarted) {
                        if (MenuManager.getEdgeMenu().isShowing()) {
                            return;
                        }

                        edgeStarted = true;
                        nodeWithStartedEdge = (Node) event.getSource();

                        startedEdge = new Edge(0, 0, 0, 0);
                        startedEdge.setVisible(false);
                        DrawingAreaController.getInstance().setMoveHandler(edgeMoveHandler);
                        DrawingAreaController.getInstance().addNode(startedEdge);
                    } else {
                        event.consume();
                        edgeStarted = false;
                        Node node = (Node) event.getSource();
//                       TODO: uncomment if no loops!

                        if (node == nodeWithStartedEdge) {
                            DrawingAreaController.getInstance().removeMoveHandler();
                            return;
                        }

                        startedEdge.setNodes(nodeWithStartedEdge, node);
                        startedEdge.connectNodes(nodeWithStartedEdge, node, nodeWithStartedEdge);
                        startedEdge.createAnchor();
                        //Graph.getInstance().connectNodes(node, pretender, edgePretender);

                        Invoker.getInstance().create(startedEdge);
                        DrawingAreaController.getInstance().removeMoveHandler();
                    }
                }
            } else if (edgeStarted && event.getTarget().getClass() == AnchorPane.class) {

                // System.out.println(event.getTarget().getClass() + " target");
                event.consume();
                deleteNotEndedEdge();

            } else if (event.getSource().getClass() == Edge.class) {
                event.consume();

            } else if (event.getSource().getClass() == EdgeDistance.class) {
                event.consume();
                System.out.println("and here");

                EdgeDistance curDist = (EdgeDistance) event.getSource();
                curDist.showInput();
                editing = true;
                if (edgeStarted) {
                    deleteNotEndedEdge();
                }
            }
        }
    };

    private static void deleteNotEndedEdge() {
        edgeStarted = false;

        startedEdge.setVisible(false);
        DrawingAreaController.getInstance().hideNode(startedEdge);

        DrawingAreaController.getInstance().removeMoveHandler();
    }

    /**
     * Controls potential edge's movements
     */
    private static final EventHandler<MouseEvent> edgeMoveHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

            double xPos = event.getX();
            double yPos = event.getY();

            Bounds b = DrawingAreaController.getInstance().getBounds();

            double dist = Edge.getDistance(xPos, yPos, nodeWithStartedEdge.getCircle().getCenterX(),
                nodeWithStartedEdge.getCircle().getCenterY());

            if (dist > Node.RADIUS + CURSOR_GAP && isInBounds(event.getX(), event.getY(), b)) {
                double[] cords = Edge.getStartCoordinates(xPos, yPos, nodeWithStartedEdge.getCircle().getCenterX(),
                    nodeWithStartedEdge.getCircle().getCenterY(), dist, nodeWithStartedEdge.getCircle().getRadius());

                int signX = cords[0] <= event.getX() ? -1 : 1;
                int signY = cords[1] <= event.getY() ? -1 : 1;
                startedEdge.setStartX(cords[0]);
                startedEdge.setEndX(event.getX() + CURSOR_GAP * signX);

                startedEdge.setStartY(cords[1]);
                startedEdge.setEndY(event.getY() + CURSOR_GAP * signY);

                startedEdge.relocateAnchor();

                startedEdge.setVisible(true);
            } else {
                startedEdge.setVisible(false);
            }
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
        String SELECTED_BUTTON = "-fx-background-color: #ebebeb;" + "-fx-font-size: 18px;"
            + "-fx-font-family: \"Constantia\";";
        b.setStyle(SELECTED_BUTTON);
        ((Button) event.getSource()).getScene().setCursor(Cursor.HAND);
    };

    /**
     * Controls button style on mouse exit
     */
    static final EventHandler<MouseEvent> buttonExitHandler = event -> {

        Button b = (Button) event.getSource();
        String UNSELECTED_BUTTON = "-fx-background-color: #f5f5f5;" + "-fx-font-size: 17px;"
            + "-fx-font-family: \"Constantia\";";
        b.setStyle(UNSELECTED_BUTTON);
        ((Button) event.getSource()).getScene().setCursor(Cursor.DEFAULT);
    };


}
