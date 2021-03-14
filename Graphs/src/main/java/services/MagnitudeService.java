package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import entities.Graph;
import exceptions.ValidationException;
import main.FileManager;
import main.PopupMessage;
import utils.Constants;

import static utils.Constants.EMPTY_GRAPH_MESSAGE;
import static utils.Constants.NO_DISTANCES_SHOWN_MESSAGE;
import static utils.Constants.PATH_TO_MAGNITUDE_SCRIPT;

public class MagnitudeService {

    public static void calculateMagnitude() {
        validate();
        String[][] allMinDistances = AlgorithmService.findAllMinDistances();

        List<String> args = new ArrayList<>();

        Arrays.stream(allMinDistances)
            .map(arr ->
                Arrays.stream(arr)
                    .reduce((a, b) -> a + " " + b)
                    .get())
            .forEach(args::add);

        try {
            PythonService.writeMatrix(args, Constants.PATH_TO_MAGNITUDE_DATA);
            String result = PythonService.runScript(PATH_TO_MAGNITUDE_SCRIPT);
            FileManager.saveFunctionOutput(result);
        } catch (Exception e) {
            PopupMessage.showMessage("Failed to run python script");
            System.out.println(e.getMessage());
        }
    }

    private static void validate() {
        if (Graph.getInstance().getSize() == 0) {
            throw new ValidationException(EMPTY_GRAPH_MESSAGE);
        }
        if (!Graph.areDistancesShown()) {
            throw new ValidationException(NO_DISTANCES_SHOWN_MESSAGE);
        }
//        if (AlgorithmService.hasCycles()) {
//            throw new ValidationException(GRAPH_HAS_CYCLES_MESSAGE);
//        }
    }
}
