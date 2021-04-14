package services;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import entities.Graph;
import entities.Node;
import exceptions.ValidationException;
import services.dto.CustomFunctionDto;

import static utils.Constants.EMPTY_GRAPH_MESSAGE;
import static utils.Constants.NO_CHOSEN_VERTEX;
import static utils.Constants.NO_DISTANCES_SHOWN_MESSAGE;
import static utils.Constants.PATH_TO_CUSTOM_DATA;
import static utils.Constants.PATH_TO_CUSTOM_SCRIPT;

@ParametersAreNonnullByDefault
public class CustomFunctionService {
    public static void calculate() {
        validate();

        Node vertex = Graph.getInstance().getSelectedNode();
        List<String> paths = AlgorithmService.findAllPaths(vertex);

        CustomFunctionDto dto =
            new CustomFunctionDto()
                .setPaths(paths)
                .setVertex(vertex.getNum());
        PythonService.constructResult(dto, PATH_TO_CUSTOM_SCRIPT, PATH_TO_CUSTOM_DATA);

        Graph.getInstance().deselectNode();
    }

    private static void validate() {
        if (Graph.getInstance().getSize() == 0) {
            throw new ValidationException(EMPTY_GRAPH_MESSAGE);
        }

        if (!Graph.areDistancesShown()) {
            throw new ValidationException(NO_DISTANCES_SHOWN_MESSAGE);
        }

        if (Graph.getInstance().getSelectedNode() == null) {
            throw new ValidationException(NO_CHOSEN_VERTEX);
        }
    }
}
