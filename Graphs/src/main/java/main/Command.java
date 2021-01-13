package main;

import entities.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface Command {
    void undo();

    //void redo();
    boolean execute();
}


class ChangeDistCommand implements Command {
    private Distance dist;
    private String oldDist;
    private double oldVal;
    private String newDist;
    private double newVal;

    public ChangeDistCommand(Distance dist, String text, double newVal) {
        this.dist = dist;
        oldDist = dist.getText();
        oldVal = dist.getValue();
        newDist = text;
        this.newVal = newVal;
    }

    public boolean execute() {
        dist.setDistance(newDist, newVal);
        return !oldDist.equals(dist.getText());
    }

    public void undo() {
        dist.setDistance(oldDist, oldVal);
    }
}

class CreateCommand implements Command {
    private Undoable elem;

    CreateCommand(Undoable elem) {
        this.elem = elem;
    }

    public boolean execute() {
        return (elem.create());
    }

    public void undo() {
        elem.remove();
    }

}

class ChangeAllDistancesCommand implements Command {
    private double commonValue;
    private boolean initialized = false;
    private String input;
    private HashMap<Edge, Pair<String, Double>> oldVals;

    ChangeAllDistancesCommand(String input) {
        System.out.println("input " + input);
        this.input = input;
    }

    public boolean execute() {

        if (!initialized) {
            try {
                commonValue = Parser.parseDistance(input);
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
                PopupMessage.showMessage(ex.getMessage());
                return false;
            }

            oldVals = SimpleGraph.getInstance().getEdgesAndDistances();
        }

        for (Edge e : oldVals.keySet())
            e.changeLength(input, commonValue);
        initialized = true;

        return true;
    }

    public void undo() {
        for (Map.Entry<Edge, Pair<String, Double>> edge : oldVals.entrySet())
            edge.getKey().changeLength(edge.getValue().getKey(), edge.getValue().getValue());
    }
}

class DeleteCommand implements Command {
    private Undoable elem;
    private ArrayList<Edge> cachedEdges;

    public DeleteCommand(Undoable elem) {
        this.elem = elem;

        cachedEdges = new ArrayList<>();
    }

    public boolean execute() {
        if (elem instanceof Node) {
            Node node = (Node) elem;
            cachedEdges = (ArrayList<Edge>) node.getEdges().clone();
        }
        elem.remove();
        return true;
    }

    public void undo() {
        elem.create();
        if (elem instanceof Node) {
            System.out.println(cachedEdges);
            for (Edge e : cachedEdges)
                e.create();
        }
    }
}

class MoveCommand implements Command {
    private Node node;
    private double[] backUp;
    private double[] newPos;

    public MoveCommand(Node elem, double[] init, double[] newPos) {
        node = elem;
        backUp = init;
        this.newPos = newPos;
    }

    public boolean execute() {
        node.fixPosition(newPos[0], newPos[1]);
        return true;
    }

    public void undo() {
        if (node.getLayoutX() == newPos[0] &&
                node.getLayoutY() == newPos[1])
            node.fixPosition(backUp[0], backUp[1]);
        else
            node.fixPosition(newPos[0], newPos[1]);
    }
}
