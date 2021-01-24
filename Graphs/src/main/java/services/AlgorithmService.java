package services;

import java.util.Stack;
import java.util.function.Consumer;

import entities.Graph;
import entities.Node;

public class AlgorithmService {

    private static final Stack<Node> dfsStack = new Stack<>();
    private static final Graph graph = Graph.getInstance();

    /**
     * Runs dfs for each node and counts components
     *
     * @param handler method to handle depending on what we need
     * @return num of components
     */
    public static int runDFS(Consumer<Node> handler) {

        if (graph.getSize() == 0) {
            return 0;
        }

        int components = 0;
        for (Node n : graph.getNodes()) {
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
     * Runs DFS for one node
     *
     * @param handler method to handle with each node
     */
    private static void DFS(Consumer<Node> handler) {
        Node curNode;
        while (!dfsStack.isEmpty()) {
            curNode = dfsStack.pop();
            if (!curNode.isVisited()) {
                curNode.visit();
                if (handler != null) {
                    handler.accept(curNode);
                }
                for (Node n : curNode.getNeighbours()) {
                    if (!n.isVisited()) {
                        dfsStack.push(n);
                    }
                }
            }
        }
    }

    /**
     * Marks all nodes unvisited after dfs
     */
    private static void resetDFS() {
        for (Node n : graph.getNodes()) {
            n.unvisit();
        }
    }
}
