package main;

import java.util.Stack;

/**
 * Represents a cycled stack of commands
 */
public class Cache extends Stack<Command> {
    private static final int MAX_COMMANDS = 20;
    private final Command[] commandStack = new Command[MAX_COMMANDS];

    private int currentSize;

    private int undoCommandPointer = -1;
    private int redoCommandPointer = 0;

    @Override
    public Command push(Command command) {
        undoCommandPointer++;
        redoCommandPointer = 0;

        if (undoCommandPointer == MAX_COMMANDS) {
            undoCommandPointer = 0;
        }
        commandStack[undoCommandPointer] = command;
        if (currentSize < MAX_COMMANDS) {
            currentSize++;
        }
        Invoker.checkLastSaveCommand();
        return command;
    }

    @Override
    public Command pop() {

        if (commandStack.length > 0 && currentSize > 0) {
            Command toReturn = commandStack[undoCommandPointer];
            undoCommandPointer--;
            if (undoCommandPointer < 0) {
                undoCommandPointer = MAX_COMMANDS - 1;
            }
            currentSize--;
            redoCommandPointer++;
            return toReturn;
        }

        return null;

    }

    public Command getNext() {

        if (currentSize == MAX_COMMANDS || redoCommandPointer == 0) {
            return null;
        }

        undoCommandPointer++;
        redoCommandPointer--;

        if (undoCommandPointer == MAX_COMMANDS) {
            undoCommandPointer = 0;
        }
        currentSize++;

        return commandStack[undoCommandPointer];
    }

    public Command getCurrent() {
        return undoCommandPointer > -1 && undoCommandPointer < MAX_COMMANDS ? commandStack[undoCommandPointer] : null;
    }
}
