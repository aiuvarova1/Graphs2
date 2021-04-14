package services;

import javax.annotation.ParametersAreNonnullByDefault;

import entities.Graph;
import exceptions.ValidationException;
import services.dto.IharaDto;
import services.dto.IharaEdgeDto;
import services.dto.IharaPathDto;
import utils.Constants;

import static utils.Constants.EMPTY_GRAPH_MESSAGE;
import static utils.Constants.GRAPH_NOT_CONNECTED_MESSAGE;

@ParametersAreNonnullByDefault
public class IharaZetaFunctionService {

    public static void calculateIharaFunction() {
        validate();

        IharaDto dto = constructDto();

        PythonService.constructResult(dto, Constants.PATH_TO_IHARA_SCRIPT, Constants.PATH_TO_IHARA_DATA);

    }

    public static void calculateIharaEdgeFunction() {
        validate();
        IharaEdgeDto dto = AlgorithmService.findEdgeMatrix();

        PythonService.constructResult(dto, Constants.PATH_TO_IHARA_EDGE_SCRIPT, Constants.PATH_TO_IHARA_EDGE_DATA);
    }

    public static void calculateIharaPathFunction() {
        validate();
        IharaPathDto dto = AlgorithmService.findPathMatrix();

        PythonService.constructResult(dto, Constants.PATH_TO_IHARA_PATH_SCRIPT, Constants.PATH_TO_IHARA_PATH_DATA);
    }

    private static IharaDto constructDto() {
        int n = Graph.getInstance().getSize();
        int rm1 = Graph.getInstance().getEdgesAndDistances().keySet().size() - n;

        int[][] Q = AlgorithmService.findDiagonalMatrix();

        IharaDto dto = new IharaDto()
            .setRm1(rm1)
            .setWeighted(Graph.areDistancesShown())
            .setQ(Q);

        if (Graph.areDistancesShown()) {
            String[][] W = AlgorithmService.findWeightedMatrix();
            dto.setW(W);
        } else {
            int[][] A = AlgorithmService.findAdjacencyMatrix();
            dto.setA(A);
        }

        return dto;
    }

    private static void validate() {
        if (Graph.getInstance().getSize() == 0) {
            throw new ValidationException(EMPTY_GRAPH_MESSAGE);
        }

        if (AlgorithmService.runDFS(null) > 1) {
            throw new ValidationException(GRAPH_NOT_CONNECTED_MESSAGE);
        }
    }
}
