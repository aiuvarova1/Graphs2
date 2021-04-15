package main;

import entities.*;

/**
 * Processes commands instances and stores them
 * in Cache stack
 */

public class Invoker {
    private final Cache commands = new Cache();
    private static Invoker instance = new Invoker();
    private static Command lastSaveCommand;

    /**
     * Singleton
     * @return an instance of Invoker
     */
    public static Invoker getInstance(){
        if(instance == null) instance = new Invoker();
        return instance;
    }

    /**
     * Resets data for the new graph
     */
    static void reset(){
        instance.commands.clear();
        lastSaveCommand = null;
    }

    /**
     * Renews last saved command
     */
    static void renewLastSaveCommand() {
        lastSaveCommand = instance.commands.getCurrent();
        FileManager.setDontNeedSave(true);
    }

    /**
     * Checks whether the save is needed or not
     */
    static void checkLastSaveCommand() {
        if (lastSaveCommand != null &&
            lastSaveCommand == instance.commands.getCurrent()) {
            FileManager.setDontNeedSave(true);
        } else {
            FileManager.setDontNeedSave(false);
        }
    }

    /**
     * Calls create command
     *
     * @param el element to create
     */
    public void create(Undoable el) {
        Command c = new CreateCommand(el);
        if (c.execute()) {
            commands.push(c);
        }
    }

    public void setAllLengths(String length) {
        Command c = new SetAllLengthsCommand(length);
        if (c.execute()) {
            commands.push(c);
        }
    }

    /**
     * Calls delete command
     *
     * @param toDelete element to delete
     */
    public void delete(Undoable toDelete) {
        Command c = new DeleteCommand(toDelete);
        if (c.execute()) {
            commands.push(c);
        }
    }

    public void setSingleLength(EdgeDistance length, String text, double val) {
        Command c = new SetSingleLengthCommand(length, text, val);
        if (c.execute()) {
            commands.push(c);
        }
    }

    /**
     * Reverts last command in cache
     */
    public void undoCommand() {

        Command toUndo = commands.pop();

        if (toUndo != null) {
            toUndo.undo();
            checkLastSaveCommand();
        }
    }

    /**
     * Reverts next command in cache
     */
    public void redoCommand() {
        Command toRedo = commands.getNext();

        if (toRedo != null) {
            toRedo.execute();
            checkLastSaveCommand();
        }
    }



    public void moveElement(Node el, double[] init, double[] newPos){
        Command c = new MoveCommand(el,init,newPos);
        commands.push(c);
    }
}
