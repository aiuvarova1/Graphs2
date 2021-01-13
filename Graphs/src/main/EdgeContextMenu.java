package main;

import entities.Edge;
import entities.SimpleGraph;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

public class EdgeContextMenu extends MyContextMenu{
    public EdgeContextMenu(){
        MenuItem selection = new MenuItem("Select as a beginning edge");
        selection.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (SimpleGraph.getInstance().getStartNode() != null &&
                        SimpleGraph.getInstance().getStartNode() != ((Edge) elem).getNodes()[0] &&
                        SimpleGraph.getInstance().getStartNode() != ((Edge) elem).getNodes()[1]) {
                    SimpleGraph.getInstance().getStartNode().deselect();
                    SimpleGraph.getInstance().setStartNode(null);

                }
                if (SimpleGraph.getInstance().getStartEdge() != null) {
                    SimpleGraph.getInstance().getStartEdge().deselect();
                }

                SimpleGraph.getInstance().setStartEdge((Edge) elem);
                ((Edge) elem).select();

            }
        });
        this.getItems().add(selection);
    }
}
