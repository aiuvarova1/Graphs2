package entities;


import javafx.util.Pair;
import main.Drawer;
import main.Invoker;
import main.PopupMessage;
import main.Visualizer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * Represents the whole graph on the pane and stores
 * the list of all nodes
 */
public class SimpleGraph
        implements Serializable, Graph {

    public static final int MAX_SIZE = 100;
    private ArrayList<Node> nodes = new ArrayList<>(20);
    private static SimpleGraph instance;
    private Stack<Node> dfsStack = new Stack<>();

    private static boolean showDistances = false;

    private Edge startEdge;
    private Node startNode;

    private double curMinEdge = 100000;

    public Edge getStartEdge() {
        return startEdge;
    }

    public Node getStartNode(){
        return startNode;
    }

    public Node getNode(int i){
        return nodes.get(i);
    }

    public void setStartEdge(Edge start) {
        startEdge = start;
    }

    public void setStartNode(Node start) {
        startNode = start;
    }

    public void setMin(double pretender) {
        curMinEdge = Math.min(pretender, curMinEdge);
    }

    public  double getCurMinEdge() {
        return curMinEdge;
    }

    /**
     * Singleton
     *
     * @return graph's instance
     */
    public static SimpleGraph getInstance() {
        if (instance == null) {
            instance = new SimpleGraph();
        }
        return instance;
    }

    public int getSize() {
        return nodes.size();
    }

    public static boolean areDistancesShown() {
        return showDistances;
    }

    public static void hideGraph() {
        for (Node node : instance.nodes) {
            Drawer.getInstance().removeElement(node);
            for (Edge edge : node.getEdges()) {
                try {
                    edge.hide();
                } catch (IllegalArgumentException ex) {
                }
            }
        }
    }

    public static void showGraph() {
        for (Node node : instance.nodes) {
            Drawer.getInstance().addElem(node);
            for (Edge edge : node.getEdges()) {
                try {
                    edge.show();
                } catch (IllegalArgumentException ex) {
                }
            }
        }
    }

    /**
     * Adds the given node to the graph
     *
     * @param node node to add
     */
    void addNode(Node node) {

        int num = Integer.parseInt(node.getId()) - 1;
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
        while (edges.size() != 0)
            edges.get(0).remove();

        nodes.remove(circle);
        refreshLabels(circle);

        if (startNode == circle)
            startNode = null;
        Drawer.getInstance().removeElement(circle);
    }

    void refreshLabels(Node circle) {
        int num = Integer.parseInt(circle.getId()) - 1;
        // nodes.
        for (int i = num; i < nodes.size(); i++)
            nodes.get(i).renewNum(i + 1);
    }

    /**
     * Fills hash map edge - distance for invoker's set_all operation
     * @return filled hash map
     */
    public HashMap<Edge, Pair<String,Double>> getEdgesAndDistances(){
        HashMap<Edge, Pair<String,Double>> res = new HashMap<>();
        for (Node n : nodes)
        {
            for (Edge e: n.getEdges()) {
                if(!res.keySet().contains(e))
                    res.put(e, new Pair<>(e.getTextLength(),e.getLength()));
            }
        }
        return res;
    }

    /**
     * Sets new instance of a graph from an opened file
     *
     * @param g new graph
     */
    public static void setNew(SimpleGraph g) {
        Drawer.getInstance().clear();
        instance = Objects.requireNonNull(g);
        instance.runDFS(Node::restore);
        for (Node n : instance.nodes) {
            for (Edge e : n.getEdges()) {
                e.connectNodes(e.getNodes()[0], e.getNodes()[1]);
            }
        }
        if (instance.getStartEdge() != null) {
            instance.getStartEdge().select();
        }
        if(instance.getStartNode() != null)
            instance.getStartNode().select();
    }


    /**
     * Removes all nodes from the list
     */
    public void clearGraph() {
        startEdge = null;
        startNode = null;
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
            if (axis == 'y')
                node.rescaleY(scale);
        }
    }

    /**
     * Resets nodes' visualization info
     */
    public void resetNodes(){
        for(Node n: nodes){
            n.resetNode();
        }
    }

    /**
     * Sets lengths for all edges in graph
     */
    public void setLengths() {
        showDistances = true;
        runDFS(Node::showLengths);
    }

    /**
     * Hides lengths for all edges in graph
     */
    public void hideLengths() {
        showDistances = false;
        runDFS(Node::hideLengths);
    }

    /**
     * Sets all distances to \\infty
     */
    public void resetDistances() {
        //runDFS(Node::resetLengths);
        changeDistances("\\infty");
    }

    public void changeDistances(String input){
        Invoker.getInstance().changeAllDistances(input);
    }

    /**
     * Checks data correctness and starts the distribution
     */
    public void visualizeAmplitudes() {
        curMinEdge = 100000;
        try {
            if (runDFS(Node::checkMinEdge) > 1) {
                PopupMessage.showMessage("The graph is not connected");
                Visualizer.enableGif(false);
                return;
            }
        } catch (IllegalArgumentException ex) {
            PopupMessage.showMessage(ex.getMessage());
            Visualizer.enableGif(false);
            return;
        }

        if (startNode == null) {
            PopupMessage.showMessage("The beginning node is not selected");
            Visualizer.enableGif(false);
            return;
        }

        if(startEdge == null)
        {
            Visualizer.enableGif(false);
            PopupMessage.showMessage("The beginning edge is not selected");
            return;
        }

        Visualizer.startVisualization(startEdge, startNode);
    }


    /**
     * Runs DFS for one node
     *
     * @param handler method to handle with each node
     */
    private void DFS(Consumer<Node> handler) {
        Node curNode;
        while (!dfsStack.isEmpty()) {
            curNode = dfsStack.pop();
            if (!curNode.isVisited()) {
                curNode.visit();
                if (handler != null)
                    handler.accept(curNode);
                for (Node n : curNode.getNeighbours()) {
                    if (!n.isVisited())
                        dfsStack.push(n);
                }
            }
        }
    }

    /**
     * Runs dfs for each node and counts components
     *
     * @param handler method to handle depending on what we need
     * @return num of components
     */
    private int runDFS(Consumer<Node> handler) {

        if (nodes.size() == 0) return 0;

        int components = 0;
        for (Node n : nodes) {
            if (!n.isVisited()) {
                components++;
                dfsStack.push(n);
                DFS(handler);
            }
        }

        resetDFS();

        return components;
    }

    /**
     * Marks all nodes unvisited after dfs
     */
    private void resetDFS() {
        for (Node n : nodes)
            n.unvisit();
    }

}
