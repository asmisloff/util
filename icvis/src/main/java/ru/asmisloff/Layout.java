package ru.asmisloff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Layout extends JPanel {

    private BranchPartitions pp;
    private final Viewport vp = new Viewport(getWidth(), getHeight());
    private final Logger logger = LoggerFactory.getLogger(Layout.class);

    public Layout() {
        MouseListener l = new MouseAdapter() {

            private int x0;

            @Override
            public void mouseDragged(MouseEvent e) {
                float mdx = vp.mx(e.getX() - x0);
                vp.setOrigin(vp.getOriginX() - mdx, vp.getOriginY());
                logger.trace("x0 = {}; ex = {}", x0, e.getX());
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                x0 = e.getX();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, String.valueOf(vp.mx(e.getX())));
            }
        };
        addMouseListener(l);
        addMouseMotionListener((MouseMotionListener) l);
    }

    public BranchPartitions getPartitions() { return pp; }

    public void setPartitions(BranchPartitions pp) {
        this.pp = pp;
    }

    public Viewport viewport() {
        return vp;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        pp.paint(g, vp);
    }
}
