package ru.asmisloff;

import java.awt.*;

public final class Edge {

    private final Node src;
    private final Node tgt;
    private Color color = Color.BLACK;

    public Edge(Node src, Node tgt) {
        this.src = src;
        this.tgt = tgt;
    }

    public Node src() { return src; }

    public Node tgt() { return tgt; }

    public void setColor(Color color) {
        if (color != null) {
            this.color = color;
        }
    }
}
