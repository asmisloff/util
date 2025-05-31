package ru.asmisloff;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public final class Edge {

    private final Node src;
    private final Node tgt;
    private Shape shape = Shape.LINE;
    private Color color = Color.BLACK;

    public enum Shape {LINE, ARC_LEFT, ARC_RIGHT}

    public Edge(@NotNull Node src, @NotNull Node tgt) {
        this.src = src;
        this.tgt = tgt;
    }

    public Node src() { return src; }

    public Node tgt() { return tgt; }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public void setColor(Color color) {
        if (color != null) {
            this.color = color;
        }
    }

    public void paint(Graphics g, Viewport vp) {
        int xSrc = vp.vpx(src.x());
        int xTgt = vp.vpx(tgt.x());
        if (src.breaking()) xSrc -= Node.D;
        if (tgt.breaking()) xTgt -= Node.D;
        int ySrc = src.trackNumber() * Node.LINE_SPACING;
        int yTgt = tgt.trackNumber() * Node.LINE_SPACING;

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
}
