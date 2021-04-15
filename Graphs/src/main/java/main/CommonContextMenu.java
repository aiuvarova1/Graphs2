package main;

import entities.Undoable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class CommonContextMenu extends ContextMenu {
    protected Undoable undoable;

    public CommonContextMenu() {

        MenuItem deletion = new MenuItem("Delete");

        deletion.setOnAction(actionEvent -> Invoker.getInstance().delete(undoable));

        this.getItems().addAll(deletion);

    }

    @Override
    public void show(javafx.scene.Node node, double x, double y) {
        super.show(node, x, y);
    }

    public void bindElem(javafx.scene.Node el) {
        undoable = (Undoable) el;
    }
}
