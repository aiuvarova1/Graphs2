package services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import entities.Graph;
import exceptions.ValidationException;
import main.FileManager;

import static utils.Constants.EMPTY_GRAPH_MESSAGE;
import static utils.Constants.GRAPH_HAS_CYCLES_MESSAGE;
import static utils.Constants.NO_DISTANCES_SHOWN_MESSAGE;
import static utils.Constants.PATH_TO_MAGNITUDE_SCRIPT;

public class MagnitudeService {

    private static final Graph graph = Graph.getInstance();

    public static void calculateMagnitude() {
        validate();
        double[][] allMinDistances = AlgorithmService.findAllMinDistances();

        List<String> args = Arrays.stream(allMinDistances)
            .map(arr ->
                Arrays.stream(arr)
                    .mapToObj(String::valueOf)
                    .reduce((a, b) -> a + ", " + b)
                    .get())
            .collect(Collectors.toList());

        String result = JythonService.runScript(args, PATH_TO_MAGNITUDE_SCRIPT);
        FileManager.saveFunctionOutput(result);
    }

    private static void validate() {
        if (graph.getSize() == 0) {
            throw new ValidationException(EMPTY_GRAPH_MESSAGE);
        }
        if (!Graph.areDistancesShown()) {
            throw new ValidationException(NO_DISTANCES_SHOWN_MESSAGE);
        }
        if (AlgorithmService.hasCycles()) {
            throw new ValidationException(GRAPH_HAS_CYCLES_MESSAGE);
        }
    }
}
