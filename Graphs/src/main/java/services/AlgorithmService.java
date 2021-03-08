package services;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.function.Consumer;

import entities.Graph;
import entities.Node;
import exceptions.IsAlreadyVisitedException;

public class AlgorithmService {

    private static final Stack<Node> dfsStack = new Stack<>();
    private static final Graph graph = Graph.getInstance();

    public static int[][] findAllMinDistances() {
        int n = graph.getSize();
        int[][] matrix = new int[n][n];

        Arrays.stream(matrix).forEach(arr -> Arrays.fill(arr, Integer.MAX_VALUE));
        for (int i = 0; i < n; i++) {
            findMinDistance(graph.getNodes().get(i), matrix[i]);
        }

        return matrix;
    }

    public static boolean hasCycles() {
        boolean isCycled = false;
        try {
            for (Node n : graph.getNodes()) {
                if (!n.isVisited()) {
                    hasCycle(n, n);
                }
            }
        } catch (IsAlreadyVisitedException ex) {
            isCycled = true;
        }
        resetDFS();
        return isCycled;
    }

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

    private static void findMinDistance(Node n, int[] distances) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(
            Comparator.comparing(Node::getDijkstraDistance).reversed()
        );
        for (Node node : graph.getNodes()) {
            if (node.equals(n)) {
                node.setDijkstraDistance(0);
            } else {
                node.setDijkstraDistance(Integer.MAX_VALUE);
            }

            priorityQueue.add(node);
        }

        while (!priorityQueue.isEmpty()) {
            Node minNode = priorityQueue.poll();
            int distance = minNode.getDijkstraDistance();
            distances[minNode.getNum() - 1] = distance == Integer.MAX_VALUE ? -1 : distance;
            for (Map.Entry<Node, Double> entry : minNode.getNeighboursAndDistances().entrySet()) {
                int curVal = minNode.getDijkstraDistance() + entry.getValue().intValue();
                Node node = entry.getKey();
                if (curVal > 0 && node.getDijkstraDistance() > curVal) {
                    node.setDijkstraDistance(curVal);
                    priorityQueue.remove(node);
                    priorityQueue.add(node);
                }
            }
        }

    }

    private static void hasCycle(Node n, Node parent) {
        if (n.isProcessed()) {
            throw new IsAlreadyVisitedException("Cycle detected");
        }

        n.setProcessed(true);
        for (Node neighbour : n.getNeighbours()) {
            if (!neighbour.equals(parent) && !neighbour.isVisited()) {
                hasCycle(neighbour, n);
            }
        }

        n.setProcessed(false);
        n.setVisited(true);
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
                curNode.setVisited(true);
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
