package main;

import entities.SimpleGraph;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

/**
 * Class which controls all file operations
 */
class FileManager {

    private static File curFile = null;
    private static FileChooser fileChooser = new FileChooser();
    private static FileChooser gifChooser = new FileChooser();
    private static Stage mainStage = null;

    private static BooleanProperty noSave = new SimpleBooleanProperty(false);

    static{
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("Graph object (*.graph)","*.graph"));
        gifChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("GIF (*.gif)","*.gif"));
    }


    public static File getGifFile(){
        return gifChooser.showSaveDialog(mainStage);
    }
    /**
     * Stage setter (is set only once)
     * @param stage main stage
     */
    static void setStage(Stage stage){
        if(mainStage == null)
            mainStage = stage;
    }

    /**
     * Getter
     * @return whether the save is needed, reverted
     */
    static BooleanProperty getDisable()
    {
        return noSave;
    }

    /**
     * Setter
     * @param val value to set (whether the save is needed, reverted)
     */
    static void setNoSave(boolean val){
        if(val != noSave.get())
            noSave.set(val);
    }


    /**
     * Defines whether the save is needed
     * @return save or not
     */
    static boolean isSaveNeeded(){
        return SimpleGraph.getInstance().getSize() != 0 &&
                !noSave.get();
    }


    /**
     * Saves the current graph in the current file (if no file,
     * calls saveAs)
     */
    static void save(){
        if(curFile == null) {
            saveAs();
            return;
        }

        if (SimpleGraph.getInstance().getSize() == 0) {
            PopupMessage.showMessage("Nothing to save");
            return;
        }

        convertGraph(curFile);
    }

    /**
     * Saves the graph in a concrete file defined by user
     */
    static  void saveAs(){

        if (SimpleGraph.getInstance().getSize() == 0) {
            PopupMessage.showMessage("Nothing to save");
            return;
        }

        File file = fileChooser.showSaveDialog(mainStage);

        if(file!=null)
            convertGraph(file);

    }

    /**
     * Opens the specified file
     */
    static void open(){

        File file = fileChooser.showOpenDialog(mainStage);

        if(file == null)
            return;

        try(ObjectInputStream inputStream = new ObjectInputStream(
                new FileInputStream(file)
        )) {
            SimpleGraph g = (SimpleGraph) inputStream.readObject();
            SimpleGraph.setNew(g);
        }catch (FileNotFoundException ex){
            PopupMessage.showMessage("File not found");
            return;
        }catch(IOException ex)
        {
            PopupMessage.showMessage("Unable to read the data");
            ex.printStackTrace();
            return;
        }catch(Exception ex){
            PopupMessage.showMessage("Failed to open the file");
            ex.printStackTrace();
            return;
        }
        Invoker.reset();
        noSave.set(false);

    }

    /**
     * Serializes the graph and saves it in the file
     * @param file place to save into
     */
    private static void convertGraph(File file){

        try(ObjectOutputStream outputStream = new ObjectOutputStream(
                new FileOutputStream(file))){
            outputStream.writeObject(SimpleGraph.getInstance());

        }catch(FileNotFoundException ex)
        {
            PopupMessage.showMessage("File not found");
            return;
        }catch(IOException ex)
        {
            PopupMessage.showMessage("Unable to write the data");
            ex.printStackTrace();
            return;
        }
        curFile = file;
        Invoker.renewLastCommand();
        PopupMessage.showMessage(String.format("Saved to %s",curFile.getPath()));
    }
}
