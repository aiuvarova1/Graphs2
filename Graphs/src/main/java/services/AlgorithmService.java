package services;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import entities.Edge;
import entities.Graph;
import entities.Node;
import exceptions.IsAlreadyVisitedException;
import exceptions.ValidationException;
import javafx.util.Pair;
import main.Parser;
import services.data.EdgeData;
import services.dto.IharaEdgeDto;
import services.dto.IharaPathDto;

public class AlgorithmService {

    private static final Stack<Node> dfsStack = new Stack<>();

    public static List<String> findAllPaths(Node node) {
        List<String> paths = new ArrayList<>();
        recursiveDFS(node, new ArrayList<>(), paths, new HashSet<>());
        return paths;
    }

    public static String[][] findAllMinDistances() {
        int n = Graph.getInstance().getSize();

        String[][] matrix = new String[n][n];

        for (int i = 0; i < n; i++) {
            findMinDistance(Graph.getInstance().getNodes().get(i), matrix[i]);
        }

        return matrix;
    }

    public static IharaPathDto findPathMatrix() {
        int m = Graph.getInstance().getOrientedEdgesCount();

        Pair<EdgeData[], HashMap<EdgeData, Integer>> edgeData = markEdges(m);

        EdgeData[] edges = edgeData.getKey();
        HashMap<EdgeData, Integer> dict = edgeData.getValue();

        Set<Integer> edgesInTreeIndexes = findSpanningTree(Graph.getInstance().getNodes(), dict);

        if (edgesInTreeIndexes.size() == m / 2) {
            throw new RuntimeException("The whole graph is a spanning tree, path matrix has size 0");
        }

        String edgesDict = getEdgesDictString(edges);
        String spanningTreeDict =
            edgesInTreeIndexes.stream()
                .map(i -> String.format("$e_{%d}, e_{%d}$", i + 1, i + (m / 2) + 1))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");


        HashMap<Integer, List<EdgeData>> treeDict = new HashMap<>();

        for (Integer index : edgesInTreeIndexes) {
            if (!treeDict.containsKey(index)) {
                treeDict.put(edges[index].getFrom(), new ArrayList<>());
            }

            if (!treeDict.containsKey(index + m / 2)) {
                treeDict.put(edges[index + m / 2].getFrom(), new ArrayList<>());
            }

            treeDict.get(edges[index].getFrom()).add(edges[index]);
            treeDict.get(edges[index + m / 2].getFrom()).add(edges[index + m / 2]);

            edges[index].setIndex(index + 1);
            edges[index + m / 2].setIndex(index + (m / 2) + 1);
        }

        List<EdgeData> notInTree = IntStream.range(0, m)
            .filter(i -> !edgesInTreeIndexes.contains(i) && (i < m / 2 || !edgesInTreeIndexes.contains(i - m / 2)))
            .mapToObj(i -> edges[i])
            .collect(Collectors.toList());

        String notInTreeDict =
            notInTree.stream()
                .sorted(Comparator.comparing(EdgeData::getIndex))
                .map(i -> String.format("$e_{%d}$", i.getIndex()))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        String[][] matrix = new String[notInTree.size()][notInTree.size()];
        for (int i = 0; i < notInTree.size(); i++) {
            for (int j = 0; j < notInTree.size(); j++) {

                if (!(notInTree.get(i).getFrom() == notInTree.get(j).getTo() && notInTree.get(i).getTo() == notInTree.get(j).getFrom())
                    || (notInTree.get(i).equals(notInTree.get(j)))) {
                    String res = recursiveFindPathBySpanningTree(
                        notInTree.get(i),
                        notInTree.get(j),
                        treeDict,
                        "",
                        new HashSet<>()
                    );
                    matrix[i][j] = res.length() > 0 ? res.substring(0, res.length() - 1) : "";
                    if (i == j) {
                        matrix[i][j] += "-1";
                    }
                } else {
                    matrix[i][j] = i == j ? "-1" : "0";
                }
            }
        }

        return new IharaPathDto()
            .setSpanningTree(spanningTreeDict)
            .setEdgeMatrix(matrix)
            .setEdgeOrder(edgesDict)
            .setNotSpanningTree(notInTreeDict);
    }

    public static IharaEdgeDto findEdgeMatrix() {
        int m = Graph.getInstance().getOrientedEdgesCount();
        int[][] matrix = new int[m][m];

        EdgeData[] dict = markEdges(m).getKey();

        for (int i = 0; i < dict.length; i++) {
            for (int j = 0; j < dict.length; j++) {
                if (dict[i].getTo() == dict[j].getFrom() &&
                    (dict[i].getFrom() != dict[j].getTo() || dict[i].getTo() == dict[i].getFrom())
                ) {
                    matrix[i][j] = 1;
                } else {
                    matrix[i][j] = 0;
                }
            }
        }

        String dictString = getEdgesDictString(dict);

        return new IharaEdgeDto().setEdgeMatrix(matrix).setEdgeOrder(dictString);
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

    private static String recursiveFindPathBySpanningTree(
        EdgeData curEdge,
        EdgeData aimEdge,
        HashMap<Integer, List<EdgeData>> spanningTree,
        String res,
        Set<Integer> visited
    ) {

        if (curEdge.getTo() == aimEdge.getFrom()) {
            return res + String.format("w_{%d, %d}$", curEdge.getIndex(), aimEdge.getIndex());
        }

        if (curEdge.equals(aimEdge)) {
            return "";
        }

        visited.add(curEdge.getTo());

        for (EdgeData e : spanningTree.get(curEdge.getTo())) {

            if (visited.contains(e.getTo())) {
                continue;
            }

            String curRes = recursiveFindPathBySpanningTree(
                e,
                aimEdge,
                spanningTree,
                res + String.format("w_{%d, %d}", curEdge.getIndex(), e.getIndex()),
                visited
            );

            if (curRes.length() > 1 && curRes.endsWith("$")) {
                return curRes;
            }
        }

        return "";

    }

    private static String getEdgesDictString(EdgeData[] dict) {
        return IntStream.range(0, dict.length)
            .mapToObj(i -> String.format("$e_{%d}$ = %s\\\\\n", i + 1, dict[i].toString()))
            .reduce((a, b) -> a + b)
            .orElse("");
    }

    private static List<Node> getSorted() {
        return Graph.getInstance().getNodes().stream().sorted(
            Comparator.comparing(Node::getNum)
        ).collect(Collectors.toList());
    }

    private static Pair<EdgeData[], HashMap<EdgeData, Integer>> markEdges(int m) {
        EdgeData[] dict = new EdgeData[m];
        HashMap<EdgeData, Integer> pairsToIndexes = new HashMap<>();

        List<Node> sorted = getSorted();

        int index = 0;

        for (Node n : sorted) {
            List<Node> neighbours = n.getNeighboursSorted();
            for (Node neighbour : neighbours) {
                EdgeData pair = new EdgeData(n.getNum(), neighbour.getNum(), -1);

                if (n.getNum() < neighbour.getNum()) {
                    pairsToIndexes.put(pair, index);
                    dict[index] = pair;
                    pair.setIndex(index + 1);
                    index += 1;

                } else {
                    Integer revertedIndex = pairsToIndexes.get(new EdgeData(neighbour.getNum(), n.getNum(), -1));
                    Integer realIndex = revertedIndex + m / 2;

                    pair.setIndex(realIndex + 1);
                    pairsToIndexes.put(pair, realIndex);
                    dict[realIndex] = pair;

                }
            }
        }
        return new Pair(dict, pairsToIndexes);
    }

    private static Set<Integer> findSpanningTree(List<Node> nodes, HashMap<EdgeData, Integer> dict) {
        Deque<Node> queue = new ArrayDeque<>();

        Set<Node> addedNodes = new HashSet<>();

        Set<Integer> indexes = new HashSet<>();

        queue.push(nodes.get(0));
        addedNodes.add(nodes.get(0));

        while (!queue.isEmpty()) {
            Node node = queue.pop();
            Set<Node> neighbours = node.getNeighbours();

            for (Node neighbour : neighbours) {
                if (!addedNodes.contains(neighbour)) {
                    queue.push(neighbour);
                    addedNodes.add(neighbour);

                    indexes.add(dict.get(new EdgeData(node.getNum(), neighbour.getNum(), -1)));
                }
            }
        }

        if (addedNodes.size() != nodes.size()) {
            throw new RuntimeException("Graph not connected, unable to construct spanning tree");
        }

        return indexes;
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

    private static void recursiveDFS(
        Node curNode,
        List<String> tokens,
        List<String> paths,
        Set<Edge> visited
    ) {

        for (Edge e : curNode.getEdges()) {
            Set<Edge> newVisited = new HashSet<>(visited);

            if (e.getTextLength().contains("infty")) {
                throw new ValidationException("There must be no infinities in distances");
            }

            if (visited.contains(e)) {
                continue;
            }

            newVisited.add(e);

            ArrayList<String> t = new ArrayList<>(tokens);
            t.add(e.getTextLength());
            paths.add(Parser.parseTexToSympy(t));

            recursiveDFS(e.getNeighbour(curNode), t, paths, newVisited);

        }
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
