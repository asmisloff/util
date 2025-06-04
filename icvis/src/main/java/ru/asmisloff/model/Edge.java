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
            int startAngle = (shape == Shape.ARC_LEFT) ? 90 : -90;
            g.drawArc(xSrc - width / 2, Math.min(ySrc, yTgt), width, height, startAngle, 180);
        }
    }

    public boolean isGndShunt() {
        return tgt.index() == 0;
    }
}
