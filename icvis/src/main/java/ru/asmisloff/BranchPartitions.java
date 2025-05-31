package ru.asmisloff;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;

public class BranchPartitions {

    private final Node[] nodes;
    private final @Nullable Edge[] edges;
    private final List<Partition> pp = new ArrayList<>();

    private static final Comparator<NodeDto> NODE_DTO_CMP = (n1, n2) -> {
        int xCmp = Integer.compare(n1.x(), n2.x());
        if (xCmp == 0) return Integer.compare(n1.li(), n2.li());
        return xCmp;
    };

    public BranchPartitions(@NotNull PartitionsDto dto) {
        nodes = createNodes(dto);
        edges = createEdges(dto);
    }

    public int xLeft() { return nodes[0].x(); }

    public int xRight() { return nodes[nodes.length - 1].x(); }

    public int numLines() { return nodes[nodes.length - 1].lineIndex(); }

    public int numNodes() { return nodes.length; }

    public int numEdges() { return edges == null ? 0 : edges.length; }

    public Node node(int i) { return nodes[i]; }

    public Edge edge(int i) {
        if (edges != null) {
            return edges[i];
        } else {
            throw new NoSuchElementException("Нет ребер");
        }
    }

    public void paint(Graphics g, Viewport vp) {
        if (!pp.isEmpty()) {
            for (Partition p : pp) {
                for (Edge e : p.edges()) {
                    if (p.leftToRight(e)) {
                        e.setShape(Edge.Shape.LINE);
                        e.paint(g, vp);
                    } else if (p.leftToLeft(e)) {
                        e.setShape(Edge.Shape.ARC_RIGHT);
                        e.paint(g, vp);
                    } else { // rightToRight
                        e.setShape(Edge.Shape.ARC_LEFT);
                        e.paint(g, vp);
                    }
                }
                paintNodes(p.leftSection(), g, vp);
            }
            Partition last = pp.get(pp.size() - 1);
            paintNodes(last.rightSection(), g, vp);
        }
    }

    private void paintNodes(Iterable<Node> nodes, Graphics g, Viewport vp) {
        for (Node n : nodes) {
            n.paint(g, vp);
        }
    }

    private Node @NotNull [] createNodes(PartitionsDto dto) {
        List<NodeDto> dtoNodes = dto.nodes();
        if (dtoNodes == null || dtoNodes.isEmpty()) {
            throw new IllegalArgumentException(NO_NODES_MSG);
        }
        dtoNodes.sort(NODE_DTO_CMP);
        Node[] nodes = new Node[dtoNodes.size()];
        nodes[0] = new Node(dtoNodes.get(0));
        int prevIndex = nodes[0].index();
        for (int i = 1; i < nodes.length; i++) {
            Node n = new Node(dtoNodes.get(i));
            if (n.index() == prevIndex) {
                throw new IllegalArgumentException(repeatedNodeIndexMsg(prevIndex));
            }
            nodes[i] = n;
            prevIndex = n.index();
        }
        return nodes;
    }

    private @Nullable Edge[] createEdges(PartitionsDto dto) {
        List<EdgeDto> dtoEdges = dto.edges();
        if (dtoEdges == null || dtoEdges.isEmpty()) {
            return null;
        }
        Edge[] edges = new Edge[dtoEdges.size()];
        for (int i = 0; i < dtoEdges.size(); ++i) {
            EdgeDto dtoEdge = dtoEdges.get(i);
            Node src = findNodeByIndex(dtoEdge.src());
            Node tgt = findNodeByIndex(dtoEdge.tgt());
            edges[i] = new Edge(
                Objects.requireNonNull(src, danglingEdgeMsg(dtoEdge)),
                Objects.requireNonNull((tgt), danglingEdgeMsg(dtoEdge))
            );
        }
        return edges;
    }

    @Nullable
    public Node findNodeByIndex(int index) {
        int begin = 0;
        int end = nodes.length - 1;
        while (begin <= end) {
            int mid = (begin + end) >>> 1;
            Node midNode = nodes[mid];
            int midIndex = midNode.index();
            if (index < midIndex) {
                end = mid - 1;
            } else if (index > midIndex) {
                begin = mid + 1;
            } else {
                return midNode;
            }
        }
        return null;
    }

    private String danglingEdgeMsg(EdgeDto dtoEdge) {
        return REF_TO_ABSENT_NODE_MSG + dtoEdge.toString();
    }

    private static String repeatedNodeIndexMsg(int prevIndex) {
        return REPEATED_NODE_INDEX_MSG + prevIndex;
    }

    private static final String NO_NODES_MSG = "Отсутствуют узлы";
    private static final String REPEATED_NODE_INDEX_MSG = "Повторяющийся индекс узлов: ";
    private static final String REF_TO_ABSENT_NODE_MSG = "Ребро ссылается на несуществующий узел: ";
}
