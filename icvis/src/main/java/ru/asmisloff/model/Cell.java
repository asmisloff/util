package ru.asmisloff.model;

import java.util.Comparator;
import java.util.List;

public record Cell(List<Node> leftSection, List<Node> rightSection, List<Edge> edges) {

    public Cell(List<Node> leftSection, List<Node> rightSection, List<Edge> edges) {
        this.leftSection = leftSection;
        this.rightSection = rightSection;
        this.edges = edges;
        this.leftSection.sort(cmp);
        this.rightSection.sort(cmp);
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

    private static final Comparator<Node> cmp = (a, b) -> {
        int brCmp = Integer.compare(a.branchIndex(), b.branchIndex());
        if (brCmp != 0) return brCmp;
        int xCmp = Integer.compare(a.x(), b.x());
        if (xCmp != 0) return xCmp;
        int tCmp = Integer.compare(a.trackNumber(), b.trackNumber());
        if (tCmp != 0) return tCmp;
        if (a.breaking() && !b.breaking()) return -1;
        if (b.breaking() && !a.breaking()) return 1;
        return 0;
    };
}
