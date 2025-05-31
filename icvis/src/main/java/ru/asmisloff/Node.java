package ru.asmisloff;

import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Узел схемы.
 */
public final class Node {

    public static final int R = 3;
    public static final int D = 2 * R;
    public static final int LINE_SPACING = 100;

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
        y = LINE_SPACING * trackNumber();
    }

    public int trackNumber() {
        return li % 10_000;
    }

    public int index() { return index; }

    public int x() { return x; }

    public int lineIndex() { return li; }

    public boolean breaking() { return br; }

    public float y() {
        return y;
    }

    public void setY(float y) { this.y = y; }

    public void paint(Graphics g, Viewport vp) {
        int xc = vp.vpx(x());
        if (breaking()) {
            xc -= D;
            g.setColor(Color.red);
        } else {
            g.setColor(Color.black);
        }
        int yc = vp.vpy(y());
        g.fillOval(xc - R, yc - R, D, D);
    }

    @Nullable
    public static Node findByIndex(Node[] arr, int index) {
        int begin = 0;
        int end = arr.length - 1;
        while (begin <= end) {
            int mid = (begin + end) >>> 1;
            Node midNode = arr[mid];
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
}
