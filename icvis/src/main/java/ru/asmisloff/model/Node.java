package ru.asmisloff.model;

import ru.asmisloff.Viewport;
import ru.asmisloff.dto.NodeDto;

import java.awt.*;

/**
 * Узел схемы.
 */
public final class Node {

    public static final int R = 4;
    public static final int D = 2 * R;
    public static final int LINE_SPACING = 50;

    private final int index;
    private final int x;
    private final int li;
    private final boolean br;
    private float y;
    private Color color;

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
        color = br ? Color.red : Color.blue;
    }

    public Node(NodeDto dto) {
        this(dto.i(), dto.x(), dto.li(), dto.br());
        y = defaultY();
    }

    public int branchIndex() { return li / 10_000; }

    public int trackNumber() { return li % 10_000; }

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
        }
        g.setColor(color);
        y = vp.my(yc);
        g.fillOval(xc - R, yc - R, D, D);
    }

    private int defaultY() {
        int relLineIndex = trackNumber();
        if (relLineIndex < 1000) {
            return LINE_SPACING * relLineIndex;
        }
        return LINE_SPACING * (relLineIndex / 1000) + 6 * LINE_SPACING;
    }

    public void markAsMotionless() {
        if (!br) color = Color.BLACK;
    }
}
