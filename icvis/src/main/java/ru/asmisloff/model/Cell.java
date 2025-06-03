package ru.asmisloff.model;

import java.util.Comparator;
import java.util.List;

public record Cell(List<Node> leftSection, List<Node> rightSection, List<Edge> edges) {

    public Cell(List<Node> leftSection, List<Node> rightSection, List<Edge> edges) {
        this.leftSection = leftSection;
        this.rightSection = rightSection;
        this.edges = edges;
        this.leftSection.sort(Comparator.comparingInt(Node::index));
        this.rightSection.sort(Comparator.comparingInt(Node::index));
        setShapes();
    }

    public void setShapes() {
        for (Edge e : edges) {
            List<Node> srcSection = findSection(e.src());
            List<Node> tgtSection = findSection(e.tgt());
            if (srcSection != tgtSection) {
                e.setShape(Edge.Shape.LINE);
            } else if (srcSection == leftSection) {
                e.setShape(Edge.Shape.ARC_RIGHT);
            } else { // srcSection == rightSection
                e.setShape(Edge.Shape.ARC_LEFT);
            }
        }
    }

    private List<Node> findSection(Node src) {
        if (leftSection.contains(src)) {
            return leftSection;
        }
        if (rightSection.contains(src)) {
            return rightSection;
        }
        return null;
    }
}
