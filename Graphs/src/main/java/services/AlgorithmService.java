package services;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import entities.Graph;
import entities.Node;
import exceptions.IsAlreadyVisitedException;

public class AlgorithmService {

    private static final Stack<Node> dfsStack = new Stack<>();

    public static int[][] findAllMinDistances() {
        int n = Graph.getInstance().getSize();
        int[][] matrix = new int[n][n];

        Arrays.stream(matrix).forEach(arr -> Arrays.fill(arr, Integer.MAX_VALUE));
        for (int i = 0; i < n; i++) {
            findMinDistance(Graph.getInstance().getNodes().get(i), matrix[i]);
        }

        return matrix;
    }

    //no loops and multiple edges for now
    public static int[][] findAdjacencyMatrix() {
        int n = Graph.getInstance().getSize();
        int[][] A = new int[n][n];

        List<Node> sorted = Graph.getInstance().getNodes().stream().sorted(
            Comparator.comparing(Node::getNum)
        ).collect(Collectors.toList());

        for (Node node : sorted) {
            node.getNeighbours().forEach(neighbour ->
                A[node.getNum() - 1][neighbour.getNum() - 1] = 1
            );
        }

        return A;
    }

    public static int[][] findDiagonalMatrix() {
        int n = Graph.getInstance().getSize();

        int[][] Q = new int[n][n];

        List<Node> sorted = Graph.getInstance().getNodes().stream().sorted(
            Comparator.comparing(Node::getNum)
        ).collect(Collectors.toList());

        IntStream.range(0, n).forEach(i -> Q[i][i] = sorted.get(i).getDegree() - 1);

        return Q;

    }

    public static boolean hasCycles() {
        boolean isCycled = false;
        try {
            for (Node n : Graph.getInstance().getNodes()) {
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

        if (Graph.getInstance().getSize() == 0) {
            return 0;
        }

        int components = 0;
        for (Node n : Graph.getInstance().getNodes()) {
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
        for (Node node : Graph.getInstance().getNodes()) {
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
        for (Node n : Graph.getInstance().getNodes()) {
            n.unvisit();
        }
    }
}
