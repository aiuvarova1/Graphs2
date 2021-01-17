package main;

import entities.Edge;
import entities.Graph;
import javafx.scene.control.MenuItem;

public class EdgeContextMenu extends MyContextMenu {
    public EdgeContextMenu() {
        MenuItem selection = new MenuItem("Select as a beginning edge");
        selection.setOnAction(event -> {

            if (Graph.getInstance().getStartNode() != null &&
                Graph.getInstance().getStartNode() != ((Edge) elem).getNodes()[0] &&
                Graph.getInstance().getStartNode() != ((Edge) elem).getNodes()[1]) {
                Graph.getInstance().getStartNode().deselect();
                Graph.getInstance().setStartNode(null);

            }
            if (Graph.getInstance().getStartEdge() != null) {
                Graph.getInstance().getStartEdge().deselect();
            }

            Graph.getInstance().setStartEdge((Edge) elem);
            ((Edge) elem).select();

        });
        this.getItems().add(selection);
    }
}
