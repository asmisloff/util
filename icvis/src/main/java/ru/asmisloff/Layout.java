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
            private final Rectangle iRect1 = new Rectangle();
            private final Rectangle iRect2 = new Rectangle();

            @Override
            public void mouseMoved(MouseEvent e) {
                if (onMouseMoved != null) onMouseMoved.accept(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                computeBoundingRect(iRect1);
                float mdx = vp.mx(e.getX()) - vp.mx(x0);
                x0 = e.getX();
                float mdy = e.isControlDown() ? 0 : vp.my(e.getY()) - vp.my(y0);
                y0 = e.getY();
                vp.setOrigin(vp.getOriginX() - mdx, vp.getOriginY() - mdy);
                computeBoundingRect(iRect2);
                computeUnion(iRect1, iRect2, iRect1);
                repaint(iRect1);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                x0 = e.getX();
                y0 = e.getY();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    vp.setScale((getWidth() - 10f * Node.D) / (pp.xRight() - pp.xLeft()), 1f);
                    vp.setOrigin(vp.mx(vp.vpx(pp.xLeft()) - 5 * Node.D), 0);
                    repaint();
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int clicks = e.getWheelRotation();
                if (clicks != 0) {
                    computeBoundingRect(iRect1);
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
                    computeBoundingRect(iRect2);
                    computeUnion(iRect1, iRect2, iRect1);
                    repaint(iRect1);
                }
            }

            private void computeBoundingRect(Rectangle dest) {
                pp.getBoundingRect(fRect);
                int x0 = vp.vpx((float) fRect.getX());
                int y0 = vp.vpy((float) (fRect.getY()));
                int w = (int) round(vp.getScaleX() * fRect.getWidth());
                int h = (int) round(vp.getScaleY() * fRect.getHeight());
                int margin = 4 * Node.R;
                dest.setBounds(x0 - margin, y0 - margin, w + 2 * margin, h + 2 * margin);
            }

            private void computeUnion(Rectangle r1, Rectangle r2, Rectangle dest) {
                int x1 = Math.min(r1.x, r2.x);
                if (x1 < 0) x1 = 0;
                int y1 = Math.min(r1.y, r2.y);
                if (y1 < 0) y1 = 0;
                int x2 = Math.max(r1.x + r1.width, r2.x + r2.width);
                if (x2 > getWidth()) x2 = getWidth();
                int y2 = Math.max(r1.y + r1.height, r2.y + r2.height);
                if (y2 > getHeight()) y2 = getHeight();
                dest.setBounds(x1, y1, x2 - x1, y2 - y1);
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
