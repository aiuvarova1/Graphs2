package entities;

public interface Visitable {
    boolean isVisited();
    void visit();
    void unvisit();
}
