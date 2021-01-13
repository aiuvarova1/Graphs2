package entities;

import java.util.Collection;

public abstract class InfiniteGraph implements Graph{

    protected Collection<Node> nodes;

    public static int EDGE_LENGTH = 100;

    protected Collection<Node> getNodes(){
        return nodes;
    }

    /**
     * Resets nodes' visualization info
     */
    public void resetNodes(){
        for(Node n: nodes){
            n.resetNode();
        }
    }

    public abstract void visualize();
    public abstract void stop();
    public abstract void erase();
    public abstract void redraw();
}
