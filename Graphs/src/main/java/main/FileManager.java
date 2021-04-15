package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import entities.Graph;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Class which controls all file operations
 */
public class FileManager {

    private static File chosenFile = null;
    private static final FileChooser fileChooser = new FileChooser();
    private static final FileChooser functionFileChooser = new FileChooser();
    private static Stage mainStage = null;

    private static final BooleanProperty dontNeedSave = new SimpleBooleanProperty(false);

    static {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
            ("Graph object (*.graph)", "*.graph"));
        functionFileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Tex file (*.tex)", "*.tex"),
            new FileChooser.ExtensionFilter("Txt file (*.txt)", "*.txt")
        );
    }

    /**
     * Stage setter (is set only once)
     *
     * @param stage main stage
     */
    static void setStage(Stage stage) {
        if (mainStage == null) {
            mainStage = stage;
        }
    }

    /**
     * Getter
     *
     * @return whether the save is needed, reverted
     */
    static BooleanProperty getDisable() {
        return dontNeedSave;
    }

    /**
     * Setter
     *
     * @param val value to set (whether the save is needed, reverted)
     */
    static void setDontNeedSave(boolean val) {
        if (val != dontNeedSave.get()) {
            dontNeedSave.set(val);
        }
    }

    /**
     * Defines whether the save is needed
     *
     * @return save or not
     */
    static boolean isSaveNeeded() {
        return Graph.getInstance().getSize() != 0 &&
            !dontNeedSave.get();
    }

    /**
     * Saves the current graph in the current file (if no file,
     * calls saveAs)
     */
    public static void save() {
        if (chosenFile == null) {
            saveAs();
            return;
        }

        if (Graph.getInstance().getSize() == 0) {
            PopupMessage.showPopup("Nothing to save");
            return;
        }

        serializeCurrentGraph(chosenFile);
    }

    /**
     * Saves the graph in a concrete file defined by user
     */
    public static void saveAs() {

        if (Graph.getInstance().getSize() == 0) {
            PopupMessage.showPopup("Nothing to save");
            return;
        }

        File file = fileChooser.showSaveDialog(mainStage);

        if (file != null) {
            serializeCurrentGraph(file);
        }
    }

    public static void saveFunctionOutput(String functionResult) {
        File file = functionFileChooser.showSaveDialog(mainStage);
        if (file != null) {
            try (FileOutputStream outputStream = new FileOutputStream(file);
                 Writer writer = new OutputStreamWriter(outputStream)
            ) {
                writer.write(functionResult);
            } catch (IOException e) {
                PopupMessage.showPopup("Failed to write data to file");
                e.printStackTrace();
            }
        }
    }

    public static void openGraphFile() {

        File file = fileChooser.showOpenDialog(mainStage);

        if (file == null) {
            return;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(
            new FileInputStream(file)
        )) {
            Graph g = (Graph) inputStream.readObject();
            Graph.setNew(g);
        } catch (FileNotFoundException ex) {
            PopupMessage.showPopup("File not found");
            return;
        } catch (IOException ex) {
            PopupMessage.showPopup("Unable to read the data");
            ex.printStackTrace();
            return;
        } catch (Exception ex) {
            PopupMessage.showPopup("Failed to open the file");
            ex.printStackTrace();
            return;
        }
        Invoker.reset();
        dontNeedSave.set(false);
    }

    /**
     * Serializes the graph and saves it in the file
     *
     * @param file place to save into
     */
    private static void serializeCurrentGraph(File file) {

        try (ObjectOutputStream outputStream = new ObjectOutputStream(
            new FileOutputStream(file))) {
            outputStream.writeObject(Graph.getInstance());

        } catch (FileNotFoundException ex) {
            PopupMessage.showPopup("File not found");
            return;
        } catch (IOException ex) {
            PopupMessage.showPopup("Unable to write the data");
            ex.printStackTrace();
            return;
        }
        chosenFile = file;
        Invoker.renewLastSaveCommand();
        PopupMessage.showPopup(String.format("Saved to %s", chosenFile.getPath()));
    }
}
