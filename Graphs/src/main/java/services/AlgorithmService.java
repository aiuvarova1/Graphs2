package services;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import entities.Graph;
import entities.Node;
import exceptions.IsAlreadyVisitedException;
import javafx.util.Pair;
import main.Parser;

public class AlgorithmService {

    private static final Stack<Node> dfsStack = new Stack<>();

    public static String[][] findAllMinDistances() {
        int n = Graph.getInstance().getSize();

        String[][] matrix = new String[n][n];

        for (int i = 0; i < n; i++) {
            findMinDistance(Graph.getInstance().getNodes().get(i), matrix[i]);
        }

        return matrix;
    }

    //no loops for now
    public static int[][] findAdjacencyMatrix() {
        int n = Graph.getInstance().getSize();
        int[][] A = new int[n][n];

        List<Node> sorted = getSorted();

        for (Node node : sorted) {
            node.getNeighboursAndCounts().forEach((neighbour, count) ->
                A[node.getNum() - 1][neighbour.getNum() - 1] = count
            );
        }

        return A;
    }

    //TODO: multiple edges? take min for now
    public static String[][] findWeightedMatrix() {
        int n = Graph.getInstance().getSize();
        String[][] W = new String[n][n];

        Arrays.stream(W).forEach(row -> Arrays.fill(row, "0"));

        Graph.getInstance().getNodes()
            .forEach(node -> {
                HashMap<Node, Pair<Double, String>> neighboursAndDistances = node.getNeighboursAndDistances();
                neighboursAndDistances.forEach(
                    (neighbour, pairDist) ->
                        W[node.getNum() - 1][neighbour.getNum() - 1] = Parser.texToSympy(pairDist.getValue())
                );
            });

        return W;
    }

    public static int[][] findDiagonalMatrix() {
        int n = Graph.getInstance().getSize();

        int[][] Q = new int[n][n];

        List<Node> sorted = getSorted();

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
    public static int runDFS(@Nullable Consumer<Node> handler) {

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

    private static List<Node> getSorted() {
        return Graph.getInstance().getNodes().stream().sorted(
            Comparator.comparing(Node::getNum)
        ).collect(Collectors.toList());
    }

    private static void findMinDistance(Node n, String[] distances) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(
            Comparator.comparing(Node::getDijkstraDistance).reversed()
        );
        for (Node node : Graph.getInstance().getNodes()) {
            if (node.equals(n)) {
                node.setDijkstraDistance(0);
            } else {
                node.setDijkstraDistance(Double.MAX_VALUE);
            }
            node.getDijkstraTexTokens().clear();

            priorityQueue.add(node);
        }

        while (!priorityQueue.isEmpty()) {
            Node minNode = priorityQueue.poll();
            double distance = minNode.getDijkstraDistance();

            distances[minNode.getNum() - 1] = n.equals(minNode) ? "0" : distance == Double.MAX_VALUE ?
                "-1" :
                Parser.parseTexToSympy(minNode.getDijkstraTexTokens());


            for (Map.Entry<Node, Pair<Double, String>> entry : minNode.getNeighboursAndDistances().entrySet()) {
                double curVal = minNode.getDijkstraDistance() + entry.getValue().getKey();

//                String curTextVal = minNode.getDijkstraTexDistance().equals("") ?
//                    entry.getValue().getValue() :
//                    minNode.getDijkstraTexDistance() + '+' + entry.getValue().getValue();

                Node node = entry.getKey();
                if (curVal > 0 && node.getDijkstraDistance() > curVal) {
                    node.getDijkstraTexTokens().clear();
                    node.getDijkstraTexTokens().addAll(minNode.getDijkstraTexTokens());
                    node.getDijkstraTexTokens().add(entry.getValue().getValue());

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
    private static void DFS(@Nullable Consumer<Node> handler) {
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
