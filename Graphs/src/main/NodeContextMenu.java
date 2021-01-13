package main;

import entities.SimpleGraph;
import entities.Node;
import javafx.scene.control.MenuItem;

public class NodeContextMenu extends MyContextMenu{
    public NodeContextMenu(){
        MenuItem selection = new MenuItem("Select as a beginning node");
        selection.setOnAction(event -> {

            if (SimpleGraph.getInstance().getStartEdge() != null &&
                    ((Node) elem) != SimpleGraph.getInstance().getStartEdge().getNodes()[0] &&
                    ((Node) elem) != SimpleGraph.getInstance().getStartEdge().getNodes()[1]) {
                SimpleGraph.getInstance().getStartEdge().deselect();
                SimpleGraph.getInstance().setStartEdge(null);
            }
            if (SimpleGraph.getInstance().getStartNode() != null) {
                SimpleGraph.getInstance().getStartNode().deselect();
            }
            SimpleGraph.getInstance().setStartNode((Node) elem);
            ((Node) elem).select();
        });
        this.getItems().add(selection);
    }


}
