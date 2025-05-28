package ru.asmisloff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

public final class PartitionView extends JFrame implements Runnable {

    private static final int NODE_SIZE = 4;
    private static final int LINE_SPACING = 100;
    private static final int PADDING = 50;
    private int xLeft;
    private int xRight;
    int origin;
    float xScale = 1f;
    private final Node[] nodes;
    private final int lineQty;
    private boolean disposed;
    final Viewport vp = new Viewport(getWidth(), getHeight());

    public PartitionView(String json) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ObjectMapper m = new ObjectMapper();
        try {
            List<Node> data = m.readValue(json, new TypeReference<>() {});
            nodes = new Node[data.size()];
            data.toArray(nodes);
            int maxTrackNumber = 0;
            if (nodes.length > 0) {
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
            }
            this.lineQty = maxTrackNumber;
            disposed = false;
        } catch (JsonProcessingException e) {
            JOptionPane.showMessageDialog(this, "Не удалось разобрать JSON");
            dispose();
            throw new RuntimeException(e);
        }
        registerMouseListeners();
    }

    public static PartitionView fromClipboard() {
        try {
            String json = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            return new PartitionView(json);
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
        for (Node n : nodes) {
            drawNode(n, g);
        }
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

    private void drawNode(Node n, Graphics g) {
        g.setColor(n.br() ? Color.red : Color.black);
        int xc = vp.vpx(n.x());
        if (n.br()) {
            xc -= NODE_SIZE;
        }
        int yc = n.trackNumber() * LINE_SPACING;
        g.fillOval(xc, yc, NODE_SIZE, NODE_SIZE);
    }

    private void registerMouseListeners() {
        PartitionViewMouseListener mouseListener = new PartitionViewMouseListener(this);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);
    }
}