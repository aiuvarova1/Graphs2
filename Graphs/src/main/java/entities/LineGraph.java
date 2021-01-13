package entities;

import java.util.ArrayDeque;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.scene.Group;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import main.Drawer;
import main.Visualizer;

import static entities.Node.RADIUS;

public class LineGraph extends InfiniteGraph {

    private static final int NUM_OF_NODES = 20;
    private static final double START_X = 50;
    private static final double START_Y = Drawer.getInstance().getBounds().getHeight()/2;

    private Group graph = new Group();
    private PathTransition transition;
    private FadeTransition ft;
    private FadeTransition edgeFt;

    private final LineTo line = new LineTo();
    private final MoveTo move = new MoveTo();
    private final Path path = new Path();

    private Point hero = new Point();


    @Override
    public void stop() {
        Drawer.getInstance().removeElement(hero);
        hero.hideEnabled();
        transition.stop();
        ft.stop();
        edgeFt.stop();
        Visualizer.stopLineVisualization();
    }

    public LineGraph(){
        line.setY(START_Y);
        move.setY(START_Y);

        transition = new PathTransition();
        nodes = new ArrayDeque<>();
        Node prevNode = Drawer.getInstance().drawInfiniteNode(0, START_Y,0, RADIUS, true);
        nodes.add(prevNode);
        graph.getChildren().add(prevNode);

        for (int i = 1; i < NUM_OF_NODES; i++)
            prevNode = addNode(prevNode);

        graph.toBack();
        Drawer.getInstance().addElem(graph);
        graph.setLayoutX(START_X);
    }

    private Node addNode(Node prevNode){

        Node node = Drawer.getInstance().drawInfiniteNode(prevNode.getLayoutX() + EDGE_LENGTH, START_Y,prevNode.getNum() + 1, RADIUS, true);
        nodes.add(node);

        Edge edge = new Edge(0,0,0,0);
        graph.getChildren().add(node);
        graph.getChildren().add(edge);

        edge.setNodes(prevNode, node);
        edge.connectNodes(prevNode, node);

        prevNode.addEdge(node, edge);
        node.addEdge(prevNode, edge);

        //Drawer.getInstance().addElem(edge);
        edge.hideLength();

        return node;
    }

    @Override
    public void redraw() {

    }

    @Override
    public void visualize() {
        hero = new Point();

        hero.setCenterX(START_X + EDGE_LENGTH - RADIUS);
        hero.setCenterY(START_Y);

        hero.showAttributes();

        Drawer.getInstance().addElem(hero);
        Visualizer.runLineVisualization(hero, this::animate);
    }

    @Override
    public void erase() {
        Drawer.getInstance().removeElement(graph);
    }

    private void animate(){
        path.getElements().clear();

        line.setX(graph.getLayoutX() + graph.getLayoutBounds().getWidth()/2 - EDGE_LENGTH + RADIUS);
        move.setX(graph.getLayoutX() + graph.getLayoutBounds().getWidth()/2);

        path.getElements().add(move);
        path.getElements().add(line);

        transition.setPath(path);

        transition.setDuration(Duration.millis(2000));
        transition.setNode(graph);
        transition.setDelay(Duration.millis(0));

        transition.setOnFinished(event -> {

            final Node top = ((ArrayDeque<Node>)nodes).pollFirst();
            graph.getChildren().remove(top);
            final Edge toRemove = ((ArrayDeque<Node>) nodes).getFirst().getEdges().get(0);
            toRemove.remove();
            graph.getChildren().remove(toRemove);

            addNode(((ArrayDeque<Node>) nodes).getLast());

            animate();
        });

        final Node first = ((ArrayDeque<Node>)nodes).getFirst();
        ft = new FadeTransition(Duration.millis(2000), first);
        ft.setFromValue(1.0);
        ft.setToValue(0);

        final Edge firstEdge = first.getEdges().get(0);

        edgeFt = new FadeTransition(Duration.millis(2000), firstEdge);
        edgeFt.setFromValue(1.0);
        edgeFt.setToValue(0);

        transition.play();
        ft.play();
        edgeFt.play();
    }


}
