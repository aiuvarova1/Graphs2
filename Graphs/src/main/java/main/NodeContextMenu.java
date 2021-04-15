package main;

import entities.Graph;
import entities.Node;
import javafx.scene.control.MenuItem;

public class NodeContextMenu extends CommonContextMenu {
    private final MenuItem selection;

    public NodeContextMenu() {
        super();
        selection = new MenuItem("Select");

        selection.setOnAction(actionEvent -> Graph.getInstance().selectNode((Node) undoable));

        this.getItems().addAll(selection);
    }
}
