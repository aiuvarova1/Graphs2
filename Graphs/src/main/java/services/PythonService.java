package services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ParametersAreNonnullByDefault
public class PythonService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Nonnull
    public static String runScript(String pathToScript) {
        try {
            URL resource = PythonService.class.getResource(pathToScript);

            String runString = String.format("python3 %s", resource.getPath());
            Process process = Runtime.getRuntime().exec(runString);
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorMessage = err.lines().reduce((a, b) -> a + '\n' + b).orElse("");
                throw new Exception(errorMessage);
            }
            System.out.println("Process exited with : " + exitCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String res = in.lines().reduce((a, b) -> a + '\n' + b).orElseThrow();

            System.out.println(res);
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeMatrix(List<String> args, String pathToData) {
        System.out.println(PythonService.class.getResource(pathToData).getPath());
        try (PrintWriter writer = new PrintWriter(PythonService.class.getResource(pathToData).getPath())) {
            args.forEach(writer::println);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void writeJsonObject(Object value, String pathToData) throws JsonProcessingException {
        String jsonData = OBJECT_MAPPER.writeValueAsString(value);
        System.out.println(jsonData);

        try (PrintWriter writer = new PrintWriter(PythonService.class.getResource(pathToData).getPath())) {
            OBJECT_MAPPER.writeValue(writer, value);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
