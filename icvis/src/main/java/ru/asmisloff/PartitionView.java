package ru.asmisloff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import java.util.*;

public final class PartitionView extends JFrame implements Runnable {

    private static final int NODE_SIZE = 6;
    private static final int LINE_SPACING = 100;
    private static final int PADDING = 50;
    private int xLeft;
    private int xRight;
    int origin;
    float xScale = 1f;
    private final Node[] nodes;
    private final Edge[] edges;
    private final List<Partition> partitions = new ArrayList<>();
    private final int lineQty;
    private boolean disposed;
    final Viewport vp = new Viewport(getWidth(), getHeight());

    public PartitionView(PartitionsDto dto) {
        nodes = new Node[dto.nodes().size()];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(dto.nodes().get(i));
        }
        edges = new Edge[dto.edges().size()];
        Arrays.sort(nodes, Comparator.comparingInt(Node::index));
        for (int i = 0; i < dto.edges().size(); i++) {
            EdgeDto eDto = dto.edges().get(i);
            edges[i] = new Edge(findNode(eDto.src()), findNode(eDto.tgt()));
        }
        int maxTrackNumber = 0;
        if (nodes.length > 0) {
            // Рассчитать xLeft, xRight и lineQty.
            Node n0 = nodes[0];
            xLeft = n0.x();
            xRight = n0.x();
            maxTrackNumber = n0.trackNumber();
            for (int i = 1; i < nodes.length; i++) {
                Node ni = nodes[i];
                if (ni.x() < xLeft) xLeft = ni.x();
                if (ni.x() > xRight) xRight = ni.x();
                if (ni.trackNumber() > maxTrackNumber) maxTrackNumber = ni.trackNumber();
            }
            createPartitions();
        }
        this.lineQty = maxTrackNumber;
        disposed = false;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        registerMouseListeners();
    }

    public static PartitionView fromJson(String json) {
        ObjectMapper m = new ObjectMapper();
        try {
            PartitionsDto dto = m.readValue(json, PartitionsDto.class);
            return new PartitionView(dto);
        } catch (JsonProcessingException e) {
            String msg = "Не удалось разобрать JSON";
            JOptionPane.showMessageDialog(null, msg);
            throw new IllegalArgumentException(msg, e);
        }
    }

    private void createPartitions() {
        SortedMap<Integer, List<Node>> sections = new TreeMap<>();
        for (Node n : nodes) {
            List<Node> section = sections.computeIfAbsent(n.x(), ArrayList::new);
            section.add(n);
        }
        if (sections.size() > 1) {
            Iterator<List<Node>> iter = sections.values().iterator();
            List<Node> leftSection = iter.next();
            while (iter.hasNext()) {
                List<Node> rightSection = iter.next();
                Partition p = new Partition(leftSection, rightSection);
                partitions.add(p);
                for (Edge e : edges) {
                    p.addEdge(e);
                }
                leftSection = rightSection;
            }
        }
    }

    public static PartitionView fromClipboard() {
        try {
            String json = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            return PartitionView.fromJson(json);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Буфер обмена недоступен");
        } catch (UnsupportedFlavorException e) {
            throw new IllegalStateException("Неверный формат данных в буфере обмена");
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось получить данные из буфера обмена");
        }
    }

    @Override
    public void run() {
        var dim = getToolkit().getScreenSize();
        dim.height = LINE_SPACING * (lineQty + 1);
        setSize(dim);
        setVisible(true);
        fit();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        vp.setScale(1f / xScale, 1f);
        vp.setOrigin(-origin / xScale, 0);
        drawPartitions(g);
    }

    @Override
    public void dispose() {
        super.dispose();
        disposed = true;
    }

    public boolean isDisposed() {
        return disposed;
    }

    void fit() {
        origin = PADDING - (int) (xScale * xLeft);
        xScale = (getWidth() - PADDING * 2f) / (xRight - xLeft);
    }

    private void drawPartitions(Graphics g) {
        if (!partitions.isEmpty()) {
            for (Partition p : partitions) {
                for (Edge e : p.edges()) {
                    if (p.leftToRight(e)) {
                        drawEdge(e, EdgeShape.LINE, g);
                    } else if (p.leftToLeft(e)) {
                        drawEdge(e, EdgeShape.ARC_RIGHT, g);
                    } else { // rightToRight
                        drawEdge(e, EdgeShape.ARC_LEFT, g);
                    }
                }
                drawNodes(p.leftSection(), g);
            }
            Partition last = partitions.get(partitions.size() - 1);
            drawNodes(last.rightSection(), g);
        }
    }

    private void drawNodes(Iterable<Node> nodes, Graphics g) {
        for (Node n : nodes) {
            drawNode(n, g);
        }
    }

    private void drawNode(Node n, Graphics g) {
        g.setColor(n.br() ? Color.red : Color.black);
        int xc = vp.vpx(n.x());
        if (n.br()) {
            xc -= NODE_SIZE;
        }
        int yc = n.trackNumber() * LINE_SPACING;
        g.fillOval(xc - NODE_SIZE / 2, yc - NODE_SIZE / 2, NODE_SIZE, NODE_SIZE);
    }

    private void drawEdge(Edge e, EdgeShape shape, Graphics g) {
        int xSrc = vp.vpx(e.src().x());
        int xTgt = vp.vpx(e.tgt().x());
        if (e.src().br()) xSrc -= NODE_SIZE;
        if (e.tgt().br()) xTgt -= NODE_SIZE;
        int ySrc = e.src().trackNumber() * LINE_SPACING;
        int yTgt = e.tgt().trackNumber() * LINE_SPACING;

        if (shape == EdgeShape.LINE) {
            g.drawLine(xSrc, ySrc, xTgt, yTgt);
        } else {
            int height = Math.abs(ySrc - yTgt);
            int width = height / 8;
            if (shape == EdgeShape.ARC_LEFT) {
                g.drawArc(xSrc - width / 2, Math.min(ySrc, yTgt), width, height, 90, 180);
            } else if (shape == EdgeShape.ARC_RIGHT) {
                g.drawArc(xSrc - width / 2, Math.min(ySrc, yTgt), width, height, -90, 180);
            } else {
                throw new IllegalStateException("Неизвестная форма ребра");
            }
        }
    }

    private void registerMouseListeners() {
        PartitionViewMouseListener mouseListener = new PartitionViewMouseListener(this);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);
    }

    private Node findNode(int index) {
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

    private enum EdgeShape {LINE, ARC_LEFT, ARC_RIGHT}
}