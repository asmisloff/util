package ru.asmisloff;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

final class PartitionViewMouseListener extends MouseAdapter {

    private final PartitionView view;

    public PartitionViewMouseListener(PartitionView view) {
        this.view = view;
    }

    private int x0;

    @Override
    public void mousePressed(MouseEvent e) {
        x0 = e.getX();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            view.fit();
            view.repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        view.origin += e.getX() - x0;
        x0 = e.getX();
        view.repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int r = e.getWheelRotation();
        if (r != 0) {
            float k = 1 + r * 0.1f;
            view.xScale *= k;
            view.origin = Math.round(e.getX() * (1 - k) + k * view.origin);
            view.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        view.vp.setScale(1f / view.xScale, 1);
        view.vp.setOrigin(-view.origin / view.xScale, 0);
        float x = view.vp.mx(e.getX());
        view.setTitle(String.format("%.3f км", x));
    }
}
