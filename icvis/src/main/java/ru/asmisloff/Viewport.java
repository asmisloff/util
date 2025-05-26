package ru.asmisloff;

public class Viewport {

    private float x0;
    private float y0;
    private float scaleX;
    private float scaleY;

    public Viewport(int x0, int y0, float scaleX, float scaleY) {
        this.x0 = x0;
        this.y0 = y0;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public void setOrigin(float mx, float my) {
        x0 = mx;
        y0 = my;
    }

    public void setScale(float x, float y) {
        scaleX = x;
        scaleY = y;
    }

    public int vpx(float mx) {
        return Math.round((mx - x0) / scaleX);
    }

    public int vpy(float my) {
        return Math.round((my - y0) / scaleY);
    }

    public float mx(int vpx) {
        return vpx * scaleX + x0;
    }

    public float my(int vpy) {
        return vpy * scaleY + y0;
    }

    private float round3(float val) {
        return (float) Math.round(val * 1000.0) / 1000;
    }
}
