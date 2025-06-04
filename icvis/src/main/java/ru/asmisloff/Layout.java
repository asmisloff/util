package ru.asmisloff;

import org.jetbrains.annotations.NotNull;
import ru.asmisloff.model.BranchPartitions;
import ru.asmisloff.model.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.util.function.Consumer;

import static java.lang.Math.round;

public class Layout extends JPanel {

    private final BranchPartitions pp;
    private final Viewport vp = new Viewport(getWidth(), getHeight());

    private Consumer<MouseEvent> onMouseMoved = null;

    public Layout(@NotNull BranchPartitions pp) {
        this.pp = pp;
        vp.setScale(1000f / (pp.xRight() - pp.xLeft()), 1f);
        MouseAdapter l = new MouseAdapter() {

            private int x0;
            private int y0;
            private final Rectangle2D fRect = new Rectangle2D.Float();
            private final Rectangle iRect = new Rectangle();

            @Override
            public void mouseMoved(MouseEvent e) {
                if (onMouseMoved != null) onMouseMoved.accept(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                pp.getBoundingRect(fRect); // todo: сделать лучше
                toViewSpace(fRect, iRect);
                int vdx = e.getX() - x0;
                if (vdx < 0) {
                    iRect.x += vdx;
                    iRect.width -= vdx;
                } else if (vdx > 0) {
                    iRect.width += vdx;
                }
                int vdy = e.getY() - y0;
                if (vdy < 0) {
                    iRect.y += vdy;
                    iRect.height -= vdy;
                } else if (vdy > 0) {
                    iRect.height += vdy;
                }
                float mdx = vp.mx(e.getX()) - vp.mx(x0);
                x0 = e.getX();
                float mdy = e.isControlDown() ? 0 : vp.my(e.getY()) - vp.my(y0);
                y0 = e.getY();
                vp.setOrigin(vp.getOriginX() - mdx, vp.getOriginY() - mdy);
                repaint(iRect);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                x0 = e.getX();
                y0 = e.getY();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int clicks = e.getWheelRotation();
                if (clicks != 0) {
                    int vx = e.getX();
                    int vy = e.getY();
                    float mx0 = vp.mx(vx);
                    float my0 = vp.my(vy);
                    float kx = 1f + clicks / 10f;
                    float ky = e.isControlDown() ? 1f : kx;
                    vp.setScale(vp.getScaleX() * kx, vp.getScaleY() * ky);
                    float dx = vp.mx(vx) - mx0;
                    float dy = vp.my(vy) - my0;
                    vp.setOrigin(vp.getOriginX() - dx, vp.getOriginY() - dy);
                    repaint();
                }
            }

            private void toViewSpace(Rectangle2D rect, Rectangle dest) {
                int x0 = vp.vpx((float) rect.getX());
                int y0 = vp.vpy((float) (rect.getY()));
                int w = (int) round(vp.getScaleX() * rect.getWidth());
                int h = (int) round(vp.getScaleY() * rect.getHeight());
                int margin = 4 * Node.R;
                dest.setBounds(x0 - margin, y0 - margin, w + 2 * margin, h + 2 * margin);
            }
        };
        addMouseListener(l);
        addMouseMotionListener(l);
        addMouseWheelListener(l);
    }

    public BranchPartitions getPartitions() { return pp; }

    public Viewport viewport() {
        return vp;
    }

    public void setOnMouseMoved(Consumer<MouseEvent> onMouseMoved) {
        this.onMouseMoved = onMouseMoved;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        pp.paint(g, vp);
    }
}
