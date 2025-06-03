package ru.asmisloff.model;

import org.jetbrains.annotations.NotNull;
import ru.asmisloff.Viewport;
import ru.asmisloff.dto.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class BranchPartitions {

    private final List<Partition> pp = new ArrayList<>();
    private final int numLines;

    public BranchPartitions(@NotNull BranchPartitionsDto dto) {
        if (dto.nodes().isEmpty()) throw new IllegalArgumentException(NO_NODES_MSG);
        SortedMap<Integer, Node> indexToNode = new TreeMap<>();
        int cnt = 0;
        for (NodeDto nodeDto : dto.nodes()) {
            Node prev = indexToNode.put(nodeDto.i(), new Node(nodeDto));
            if (prev != null) throw new IllegalArgumentException(repeatedNodeIndexMsg(nodeDto.i()));
            if (nodeDto.li() > cnt) cnt = nodeDto.li();
        }
        numLines = cnt;
        for (PartitionDto pDto : dto.partitions()) {
            var cells = new ArrayList<Cell>();
            for (CellDto cellDto : pDto.cells()) {
                List<Node> leftSection = makeSection(cellDto.leftSection(), indexToNode);
                List<Node> rightSection = makeSection(cellDto.rightSection(), indexToNode);
                List<Edge> edges = new ArrayList<>();
                for (var edgeDto : cellDto.edges()) {
                    var src = indexToNode.get(edgeDto.src());
                    if (src == null) throw new IllegalArgumentException(danglingEdgeMsg(edgeDto));
                    var tgt = indexToNode.get(edgeDto.tgt());
                    if (tgt == null) throw new IllegalArgumentException(danglingEdgeMsg(edgeDto));
                    edges.add(new Edge(src, tgt));
                }
                cells.add(new Cell(leftSection, rightSection, edges));
            }
            pp.add(new Partition(cells));
        }
    }

    @NotNull
    private static List<Node> makeSection(int[] nodeIndices, SortedMap<Integer, Node> indexToNode) {
        var section = new ArrayList<Node>();
        for (int index : nodeIndices) {
            var node = indexToNode.get(index);
            if (node == null) throw new IllegalArgumentException(unspecifiedNodeIndexMsg(index));
            section.add(node);
        }
        return section;
    }

    private static String unspecifiedNodeIndexMsg(int index) {
        return "Неизвестный индекс узла: %d".formatted(index);
    }

    public int xLeft() { return pp.get(0).xLeft(); }

    public int xRight() { return pp.get(pp.size() - 1).xRight(); }

    public int numLines() { return numLines; }

    public void paint(Graphics g, Viewport vp) {
        if (!pp.isEmpty()) {
            for (Partition p : pp) {
                for (Cell c : p.cells()) {
                    paintNodes(c.leftSection(), g, vp);
                }
            }
            Partition lastPartition = pp.get(pp.size() - 1);
            Cell lastCell = lastPartition.cells().get(lastPartition.cells().size() - 1);
            paintNodes(lastCell.rightSection(), g, vp);
            for (Partition p : pp) {
                for (Cell c : p.cells()) {
                    paintEdges(c.edges(), g, vp);
                }
            }
        }
    }

    private static void paintEdges(Iterable<Edge> edges, Graphics g, Viewport vp) {
        for (Edge e : edges) {
            if (!e.isGndShunt()) {
                e.paint(g, vp);
            }
        }
    }

    private void paintNodes(Iterable<Node> nodes, Graphics g, Viewport vp) {
        for (Node n : nodes) {
            n.paint(g, vp);
        }
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
