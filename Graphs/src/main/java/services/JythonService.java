package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import main.PopupMessage;
import org.python.util.PythonInterpreter;

@ParametersAreNonnullByDefault
public class JythonService {

    @Nonnull
    public static String runScript(List<String> args, String pathToScript) {
        try (PythonInterpreter interpreter = new PythonInterpreter();
             InputStream inputStream = MagnitudeService.class.getResourceAsStream(pathToScript);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            StringWriter stringWriter = new StringWriter();
            interpreter.setOut(stringWriter);

            String[] arguments = new String[args.size() + 1];
            arguments[0] = pathToScript;

            for (int i = 0; i < args.size(); i++) {
                arguments[i + 1] = args.get(i);
            }

            setArgs(arguments, interpreter);
            String script = reader.lines().reduce((a, b) -> a + "\n" + b).get();
            interpreter.exec(script);

            System.out.println(stringWriter.toString());
            return stringWriter.toString();
        } catch (IOException e) {
            PopupMessage.showMessage("Failed to run python script");
            return "";
        }
    }

    private static void setArgs(String[] arguments, PythonInterpreter interpreter) {
        String args = Arrays.stream(arguments).map(s -> '\'' + s + '\'').reduce((a, b) -> a + ", " + b).get();
        interpreter.exec(String.format("import sys\nsys.argv=[%s]", args));
    }
}
