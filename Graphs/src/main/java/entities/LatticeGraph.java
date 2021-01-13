package entities;


import java.util.ArrayList;

import javafx.geometry.Bounds;
import main.Drawer;
import main.Visualizer;

public class LatticeGraph extends InfiniteGraph {


    private static final int ITERATIONS = 10;
    private static final int NEIGHBOURS = 3;
    public static final int startRadius = 7;

    private int counter = 0;

    private Node root;

    private double startLength = 70;

    public LatticeGraph() {
        redraw();
    }


    private void drawLevel(int curLevel, int curNodesNum, double curStart, double curLength) {
        if (curLevel == ITERATIONS) {
            return;
        }
        curLength = curLength + startLength * (ITERATIONS - curLevel) / (float) ITERATIONS;

        int neighbours = NEIGHBOURS * (int) Math.pow(NEIGHBOURS - 1, curLevel - 1);

        double curAngle = curStart - (360f / neighbours) / 2;
        for (int i = 0; i < neighbours; i++) {

            double[] coords = getAngle(root.getCircle().getCenterX(), root.getCircle().getCenterY(),
                    curAngle, curLength);

            Node neighbour = addNode(coords[0], coords[1], startRadius * (ITERATIONS - curLevel) / (float) ITERATIONS);
            addEdge(((ArrayList<Node>) nodes).get(curNodesNum), neighbour);

            curAngle += 360f / neighbours;

            if (i % 2 != 0) {
                curNodesNum++;
            }
        }

        drawLevel(curLevel + 1, curNodesNum, curAngle, curLength);

    }

    @Override
    public void redraw() {

        if(nodes != null)
            erase();
        counter = 0;
        final Bounds bounds = Drawer.getInstance().getBounds();
        startLength = Math.min(bounds.getWidth(), bounds.getHeight()) / ITERATIONS;
        double START_X = bounds.getWidth() / 2;
        double START_Y = bounds.getHeight() / 2;

        nodes = new ArrayList<>();

        root = addNode(START_X, START_Y, startRadius);
        Node neighbour;

        double curAngle = 0;
        for (int i = 0; i < NEIGHBOURS; i++) {
            double[] coords = getAngle(root.getCircle().getCenterX(), root.getCircle().getCenterY(),
                    curAngle, startLength);

            neighbour = addNode(coords[0], coords[1], startRadius);
            addEdge(root, neighbour);

            curAngle += 360f / NEIGHBOURS;

        }

        drawLevel(2, 1, 0, startLength);
    }

    private double[] getAngle(double startX, double startY, double angle, double length) {
        return new double[]{startX + Math.cos(Math.toRadians(angle)) * length,
                startY + Math.sin(Math.toRadians(angle)) * length};
    }

    private Node addNode(double x, double y, double radius) {
        final Node node = Drawer.getInstance().drawInfiniteNode(x, y, counter++, radius, false);
        nodes.add(node);
        Drawer.getInstance().addElem(node);

        return node;
    }

    private void addEdge(Node n1, Node n2) {
        Edge edge = new Edge(0, 0, 0, 0);
        Drawer.getInstance().addElem(edge);

        edge.setNodes(n1, n2);
        edge.connectNodes(n1, n2);

        n1.addEdge(n2, edge);
        n2.addEdge(n1, edge);

        edge.hideLength();
    }

    @Override
    public void erase() {
        for (Node node : nodes) {
            Drawer.getInstance().removeElement(node);
            for (Edge e : node.getEdges()) {
                try {
                    Drawer.getInstance().removeElement(e);
                } catch (IllegalArgumentException ex) {
                }
            }
        }
    }

    @Override
    public void stop() {
        Visualizer.stopVisualization();
    }

    @Override
    public void visualize() {
        Visualizer.startVisualization(root.getEdges().get(0), root);
    }


}
