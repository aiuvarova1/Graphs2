package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javafx.util.Pair;
import lombok.Getter;
import main.Drawer;
import main.Invoker;
import services.AlgorithmService;

/**
 * Represents the whole graph on the pane and stores
 * the list of all nodes
 */
public class Graph implements Serializable {

    public static final int MAX_SIZE = 100;
    private static Graph instance;
    private static boolean showDistances = false;

    @Getter
    private Node selectedNode;

    @Getter
    private final ArrayList<Node> nodes = new ArrayList<>(20);

    /**
     * Singleton
     *
     * @return graph's instance
     */
    public static Graph getInstance() {
        if (instance == null) {
            instance = new Graph();
        }
        return instance;
    }

    public void selectNode(Node n) {
        if (selectedNode != null) {
            selectedNode.deselect();
        }
        selectedNode = n;
        selectedNode.select();
    }

    public void deselectNode() {
        if (selectedNode != null) {
            selectedNode.deselect();
        }
        selectedNode = null;
    }

    public int getSize() {
        return nodes.size();
    }

    public static boolean areDistancesShown() {
        return showDistances;
    }

    /**
     * Adds the given node to the graph
     *
     * @param node node to add
     */
    void addNode(Node node) {

        int num = node.getNum() - 1;
        instance.nodes.add(num, node);
        Drawer.getInstance().addElem(node);
    }

    /**
     * Removes node from the list and renews info
     *
     * @param circle node to remove
     */
    void removeNode(Node circle) {

        ArrayList<Edge> edges = circle.getEdges();
        while (edges.size() != 0) {
            edges.get(0).remove();
        }

        nodes.remove(circle);
        refreshLabels(circle);

        Drawer.getInstance().removeElement(circle);
    }

    void refreshLabels(Node circle) {
        int num = circle.getNum() - 1;
        // nodes.
        for (int i = num; i < nodes.size(); i++) {
            nodes.get(i).renewNum(i + 1);
        }
    }

    /**
     * Fills hash map edge - distance for invoker's set_all operation
     *
     * @return filled hash map
     */
    public HashMap<Edge, Pair<String, Double>> getEdgesAndDistances() {
        HashMap<Edge, Pair<String, Double>> res = new HashMap<>();
        for (Node n : nodes) {
            for (Edge e : n.getEdges()) {
                if (!res.containsKey(e)) {
                    res.put(e, new Pair<>(e.getTextLength(), e.getLength()));
                }
            }
        }
        return res;
    }

    public int getOrientedEdgesCount() {
        return nodes.stream()
            .map(Node::getNeighboursSorted)
            .map(List::size)
            .reduce(Integer::sum)
            .orElse(0);
    }

    /**
     * Sets new instance of a graph from an opened file
     *
     * @param g new graph
     */
    public static void setNew(Graph g) {
        Drawer.getInstance().clear();
        instance = Objects.requireNonNull(g);
        AlgorithmService.runDFS(Node::restore);
        for (Node n : instance.nodes) {
            for (Edge e : n.getEdges()) {
                e.connectNodes(e.getNodes()[0], e.getNodes()[1], e.getNodes()[1]);
            }
        }
    }

    /**
     * Removes all nodes from the list
     */
    public void clearGraph() {
        nodes.clear();
    }

    /**
     * Rescales all nodes in graph
     *
     * @param axis   by which axis relocate the nodes
     * @param oldVal old value of width/height
     * @param newVal new value of width/height
     */
    public void rescale(char axis, double oldVal, double newVal) {
        double scale = newVal / oldVal;

        for (Node node : instance.nodes) {
            if (axis == 'x') {
                node.rescaleX(scale);
            }
            if (axis == 'y') {
                node.rescaleY(scale);
            }
        }
    }

    /**
     * Sets lengths for all edges in graph
     */
    public void setLengths() {
        showDistances = true;
        AlgorithmService.runDFS(Node::showLengths);
    }

    /**
     * Hides lengths for all edges in graph
     */
    public void hideLengths() {
        showDistances = false;
        AlgorithmService.runDFS(Node::hideLengths);
    }

    /**
     * Sets all distances to \\infty
     */
    public void resetDistances() {
        //runDFS(Node::resetLengths);
        changeDistances("\\infty");
    }

    public void changeDistances(String input) {
        Invoker.getInstance().changeAllDistances(input);
    }

}
