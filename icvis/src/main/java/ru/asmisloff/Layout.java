package ru.asmisloff;

import javax.swing.*;
import java.awt.*;

public class Layout extends JPanel {

    private BranchPartitions pp;
    private final Viewport vp = new Viewport(getWidth(), getHeight());

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
