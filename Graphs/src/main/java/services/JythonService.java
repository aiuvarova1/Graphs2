package services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import main.PopupMessage;
import utils.Constants;

@ParametersAreNonnullByDefault
public class JythonService {

    @Nonnull
    public static String runScript(List<String> args, String pathToScript) {
        try {
            writeMatrix(args);

            URL resource = JythonService.class.getResource(pathToScript);

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

    private static void writeMatrix(List<String> args) {
        System.out.println(JythonService.class.getResource(Constants.PATH_TO_DATA).getPath());
        try (PrintWriter writer = new PrintWriter(JythonService.class.getResource(Constants.PATH_TO_DATA).getPath())) {
            args.forEach(writer::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
