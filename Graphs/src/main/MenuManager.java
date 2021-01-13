package main;

public class MenuManager {

    private static EdgeContextMenu edgeMenu = new EdgeContextMenu();
    private static NodeContextMenu nodeMenu = new NodeContextMenu();

    public static EdgeContextMenu getEdgeMenu(){
        return edgeMenu;
    }
    public static NodeContextMenu getNodeMenu(){return nodeMenu;}
}
