package services;

import javax.annotation.ParametersAreNonnullByDefault;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Graph;
import main.FileManager;
import services.dto.IharaDto;
import utils.Constants;

@ParametersAreNonnullByDefault
public class IharaZetaFunctionService {

    public static void calculateIharaFunction() {
        validate();

        IharaDto dto = constructDto();

        try {
            PythonService.writeJsonObject(dto, Constants.PATH_TO_IHARA_DATA);
            String result = PythonService.runScript(Constants.PATH_TO_IHARA_SCRIPT);
            FileManager.saveFunctionOutput(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to calculate zeta function");
        }

    }

    private static IharaDto constructDto() {
        int n = Graph.getInstance().getSize();
        int rm1 = Graph.getInstance().getEdgesAndDistances().keySet().size() - n;

        int[][] A = AlgorithmService.findAdjacencyMatrix();
        int[][] Q = AlgorithmService.findDiagonalMatrix();

        return new IharaDto()
            .setRm1(rm1)
            .setA(A)
            .setQ(Q);
    }

    private static void validate() {

    }
}
