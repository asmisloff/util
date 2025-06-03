package ru.asmisloff.model;

import ru.asmisloff.Viewport;
import ru.asmisloff.dto.NodeDto;

import java.awt.*;

/**
 * Узел схемы.
 */
public final class Node {

    public static final int R = 3;
    public static final int D = 2 * R;
    public static final int LINE_SPACING = 50;

    private final int index;
    private final int x;
    private final int li;
    private final boolean br;
    private float y;

    /**
     * @param index Индекс узла.
     * @param x     Логическая координата узла, м.
     * @param li    Индекс линии.
     * @param br    true, если узел разрывной.
     */
    public Node(int index, int x, int li, boolean br) {
        this.index = index;
        this.x = x;
        this.li = li;
        this.br = br;
    }

    public Node(NodeDto dto) {
        this(dto.i(), dto.x(), dto.li(), dto.br());
        y = defaultY();
    }

    public int trackNumber() {
        return li % 10_000;
    }

    public int index() { return index; }

    public int x() { return x; }

    public boolean breaking() { return br; }

    public float y() {
        return y;
    }

    public void paint(Graphics g, Viewport vp) {
        int xc = vp.vpx(x());
        int yc = vp.vpy(defaultY());
        if (breaking()) {
            yc += D;
            g.setColor(Color.red);
        } else {
            g.setColor(Color.black);
        }
        y = vp.my(yc);
        g.fillOval(xc - R, yc - R, D, D);
    }

    private int defaultY() {
        return LINE_SPACING * trackNumber();
    }
}
