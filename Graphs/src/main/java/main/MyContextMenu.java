package main;


import entities.Undoable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class MyContextMenu extends ContextMenu {
    private MenuItem deletion;
    protected Undoable elem;

    public MyContextMenu(){

        deletion = new MenuItem("Delete");

        deletion.setOnAction(actionEvent -> Invoker.getInstance().deleteElement(elem));

        this.getItems().addAll(deletion);

        //setStyle("MyStyle");
    }


    @Override
    public void show(javafx.scene.Node node, double x, double y){

        super.show(node, x, y);
       // elem = (Undoable) node;
    }

    public void bindElem(javafx.scene.Node el){
        elem =(Undoable) el;
    }
}
