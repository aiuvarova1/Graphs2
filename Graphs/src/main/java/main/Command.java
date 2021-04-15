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


class SetSingleLengthCommand implements Command {
    private EdgeDistance length;
    private String oldLengthText;
    private double oldRealValue;
    private String newLengthText;
    private double newRealValue;

    public SetSingleLengthCommand(EdgeDistance length, String text, double newRealValue) {
        this.length = length;
        oldLengthText = length.getText();
        oldRealValue = length.getValue();
        newLengthText = text;
        this.newRealValue = newRealValue;
    }

    public boolean execute() {
        length.setDistance(newLengthText, newRealValue);
        return !oldLengthText.equals(length.getText());
    }

    public void undo() {
        length.setDistance(oldLengthText, oldRealValue);
    }
}

class CreateCommand implements Command {
    private Undoable created;

    CreateCommand(Undoable created) {
        this.created = created;
    }

    public boolean execute() {
        return (created.create());
    }

    public void undo() {
        created.remove();
    }

}

class SetAllLengthsCommand implements Command {
    private double newLength;
    private boolean initialized = false;
    private String newTextLength;
    private HashMap<Edge, Pair<String, Double>> oldLengthValues;

    SetAllLengthsCommand(String newTextLength) {
        System.out.println("input " + newTextLength);
        this.newTextLength = newTextLength;
    }

    public boolean execute() {

        if (!initialized) {
            try {
                newLength = Parser.parseDistance(newTextLength);
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
                PopupMessage.showPopup(ex.getMessage());
                return false;
            }

            oldLengthValues = Graph.getInstance().getEdgesAndDistances();
        }

        for (Edge e : oldLengthValues.keySet()) {
            e.changeLengthValue(newTextLength, newLength);
        }
        initialized = true;

        return true;
    }

    public void undo() {
        for (Map.Entry<Edge, Pair<String, Double>> edge : oldLengthValues.entrySet()) {
            edge.getKey().changeLengthValue(edge.getValue().getKey(), edge.getValue().getValue());
        }
    }
}

class DeleteCommand implements Command {
    private Undoable deleted;
    private ArrayList<Edge> connectedEdges;

    public DeleteCommand(Undoable deleted) {
        this.deleted = deleted;

        connectedEdges = new ArrayList<>();
    }

    public boolean execute() {
        if (deleted instanceof Node) {
            Node node = (Node) deleted;
            connectedEdges = (ArrayList<Edge>) node.getEdges().clone();
        }
        deleted.remove();
        return true;
    }

    public void undo() {
        deleted.create();
        if (deleted instanceof Node) {
            System.out.println(connectedEdges);
            for (Edge e : connectedEdges) {
                e.create();
            }
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
