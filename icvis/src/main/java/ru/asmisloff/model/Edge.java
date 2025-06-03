package ru.asmisloff.model;

import ru.asmisloff.Viewport;

import java.awt.*;

import static java.util.Objects.requireNonNull;

public final class Edge {

    private final Node src;
    private final Node tgt;
    private Shape shape = Shape.LINE;

    public enum Shape {LINE, ARC_LEFT, ARC_RIGHT}

    public Edge(Node src, Node tgt) {
        this.src = requireNonNull(src, "Для ребра не задан начальный узел");
        this.tgt = requireNonNull(tgt, "Для ребра не задан конечный узел");
    }

    public Node src() { return src; }

    public Node tgt() { return tgt; }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public void paint(Graphics g, Viewport vp) {
        int xSrc = vp.vpx(src.x());
        int xTgt = vp.vpx(tgt.x());
        int ySrc = vp.vpy(src.y());
        int yTgt = vp.vpy(tgt.y());

        g.setColor(Color.BLACK);
        if (shape == Shape.LINE) {
            g.drawLine(xSrc, ySrc, xTgt, yTgt);
        } else {
            int height = Math.abs(ySrc - yTgt);
            int width = height / 8;
            if (shape == Shape.ARC_LEFT) {
                g.drawArc(xSrc - width / 2, Math.min(ySrc, yTgt), width, height, 90, 180);
            } else if (shape == Shape.ARC_RIGHT) {
                g.drawArc(xSrc - width / 2, Math.min(ySrc, yTgt), width, height, -90, 180);
            } else {
                throw new IllegalStateException("Неизвестная форма ребра");
            }
        }
    }

    public boolean isGndShunt() {
        return tgt.index() == 0;
    }
}
