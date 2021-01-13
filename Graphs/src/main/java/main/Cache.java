package main;

import java.util.ArrayList;
import java.util.Stack;


/**
 * Represents a cycled stack of commands
 */
public class Cache extends Stack<Command> {
    private static final int CAPACITY = 20;
    private Command[] stack = new Command[CAPACITY];

    private int curCapacity;

    private int pointer = -1;
    private int redoPointer = 0;


    @Override
    public Command push(Command elem){
        pointer++;
        redoPointer = 0;

        if(pointer == CAPACITY)
            pointer = 0;
        stack[pointer] = elem;
        if(curCapacity < CAPACITY) curCapacity ++;
        Invoker.checkLastCommand();
        return elem;
    }

    @Override
    public Command pop(){

        if (stack.length > 0 && curCapacity > 0){
            Command toReturn = stack[pointer];
            pointer--;
            if(pointer < 0)
                pointer = CAPACITY - 1;
            curCapacity--;
            redoPointer++;
            return toReturn;
        }

        return null;

    }

    public Command getNext(){

        if(curCapacity == CAPACITY || redoPointer == 0) return null;

        pointer++;
        redoPointer--;

        if(pointer == CAPACITY)
            pointer = 0;
        curCapacity++;

        return stack[pointer];
    }

    public Command getCurrent(){
        return pointer > - 1 && pointer < CAPACITY ? stack[pointer] : null;
    }
}
