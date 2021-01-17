package main;

public class MenuManager {

    private static final EdgeContextMenu edgeMenu = new EdgeContextMenu();
    private static final NodeContextMenu nodeMenu = new NodeContextMenu();

    public static EdgeContextMenu getEdgeMenu() {
        return edgeMenu;
    }

    public static NodeContextMenu getNodeMenu() {
        return nodeMenu;
    }
}
