package ru.asmisloff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.util.List;

public final class PartitionView extends JFrame implements Runnable {

    private static final int NODE_SIZE = 4;
    private static final int LINE_SPACING = 100;
    private static final int PADDING = 50;
    private int xLeft;
    private int xRight;
    private int origin;
    private float xScale = 1f;
    private int x0;
    private final Node[] nodes;
    private final int lineQty;
    private boolean disposed;

    public PartitionView(String json) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ObjectMapper m = new ObjectMapper();
        try {
            List<Node> data = m.readValue(json, new TypeReference<>() { });
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
        addMouseListener(new MouseListener());
        addMouseMotionListener(new MouseMotionListener());
        addMouseWheelListener(new MouseWheelListener());
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

    private void fit() {
        origin = PADDING - (int) (xScale * xLeft);
        xScale = (getWidth() - PADDING * 2f) / (xRight - xLeft);
    }

    private void drawNode(Node n, Graphics g) {
        g.setColor(n.br() ? Color.red : Color.black);
        g.fillOval(x(n), y(n), NODE_SIZE, NODE_SIZE);
    }

    private int x(Node n) {
        int val = (int) (n.x() * xScale) - NODE_SIZE / 2 + origin;
        return n.br() ? val - NODE_SIZE : val;
    }

    private int y(Node n) {
        return n.trackNumber() * LINE_SPACING;
    }

    private class MouseMotionListener extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            origin += e.getX() - x0;
            x0 = e.getX();
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            float x = (e.getX() - origin) / (1e3f * xScale);
            setTitle(String.format("%.3f км", x));
        }
    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            x0 = e.getX();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                fit();
                repaint();
            }
        }
    }

    private class MouseWheelListener implements java.awt.event.MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int r = e.getWheelRotation();
            if (r != 0) {
                float k = 1 + r * 0.1f;
                xScale *= k;
                origin = Math.round(e.getX() * (1 - k) + k * origin);
                repaint();
            }
        }
    }
}