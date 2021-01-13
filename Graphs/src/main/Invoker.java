package main;

import entities.*;

/**
 * Processes commands instances and stores them
 * in Cache stack
 */

public class Invoker {
    private Cache commands = new Cache();
    private static Invoker instance = new Invoker();
    private Command toUndo;
    private Command toRedo;
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
    static void renewLastCommand(){
        lastSaveCommand = instance.commands.getCurrent();
        FileManager.setNoSave(true);
    }

    /**
     * Checks whether the save is needed or not
     */
    static void checkLastCommand(){
        if(lastSaveCommand != null &&
                lastSaveCommand == instance.commands.getCurrent())
            FileManager.setNoSave(true);
        else
            FileManager.setNoSave(false);
    }


    /**
     * Calls create command
     * @param el element to create
     */
    public void createElement(Undoable el){
        Command c = new CreateCommand(el);
        if(c.execute())
            commands.push(c);
    }

    public void changeAllDistances(String input){
        Command c = new ChangeAllDistancesCommand(input);
        if(c.execute())
            commands.push(c);
    }

    /**
     * Calls delete command
     * @param el element to delete
     */
    public void deleteElement(Undoable el){
        Command c = new DeleteCommand(el);
        if(c.execute())
            commands.push(c);
    }

    public void changeDistance(Distance d, String text, double val){
        Command c = new ChangeDistCommand(d, text,val);
        if(c.execute())
            commands.push(c);
    }
    /**
     * Reverts last command in cache
     */
    void undoLast(){

        toUndo = commands.pop();

        if(toUndo !=null) {
            toUndo.undo();
            checkLastCommand();
        }
    }

    /**
     * Reverts next command in cache
     */
    void redoLast(){
        toRedo = commands.getNext();

        if (toRedo != null) {
            toRedo.execute();
            checkLastCommand();
        }
    }



    public void moveElement(Node el, double[] init, double[] newPos){
        Command c = new MoveCommand(el,init,newPos);
        commands.push(c);
    }
}
