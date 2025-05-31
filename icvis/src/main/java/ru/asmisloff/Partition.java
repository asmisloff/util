package ru.asmisloff;

import java.awt.*;
import java.util.*;
import java.util.List;

public final class Partition {

    private final List<Node> leftSection;
    private final List<Node> rightSection;
    private final SortedMap<Edge, Edge> edges;

    private static final Comparator<Edge> EDGE_CMP = (e1, e2) -> {
        int cmpSrc = compareNodes(e1.src(), e2.src());
        if (cmpSrc == 0) return compareNodes(e1.tgt(), e2.tgt());
        return cmpSrc;
    };

    public Partition(List<Node> leftSection, List<Node> rightSection) {
        this.leftSection = leftSection;
        this.rightSection = rightSection;
        this.edges = new TreeMap<>(EDGE_CMP);
    }

    public List<Node> leftSection() { return Collections.unmodifiableList(leftSection); }

    public List<Node> rightSection() { return Collections.unmodifiableList(rightSection); }

    public Collection<Edge> edges() {
        return edges.values();
    }

    public void addEdge(Edge e) {
        boolean ll = leftToLeft(e);
        boolean rrFirstTime = rightToRight(e) && !edges.containsKey(e);
        boolean lr = leftToRight(e);
        if (ll || rrFirstTime || lr) {
            edges.put(e, e);
            if (lr && edges.containsKey(e)) {
                e.setColor(Color.RED);
            }
        }
    }

    boolean leftToLeft(Edge e) {
        return leftSection.contains(e.src()) && leftSection.contains(e.tgt());
    }

    boolean rightToRight(Edge e) {
        return rightSection.contains(e.src()) && rightSection.contains(e.tgt());
    }

    boolean leftToRight(Edge e) {
        return leftSection.contains(e.src()) && rightSection.contains(e.tgt()) ||
               leftSection.contains(e.tgt()) && rightSection.contains(e.src());
    }

    private static int compareNodes(Node n1, Node n2) {
        int xCmp = Integer.compare(n1.x(), n2.x());
        if (xCmp == 0) return Integer.compare(n1.lineIndex(), n2.lineIndex());
        return xCmp;
    }
}
