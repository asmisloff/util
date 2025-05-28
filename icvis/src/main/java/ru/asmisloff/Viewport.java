package ru.asmisloff;

public class Viewport {

    private int width;
    private int height;
    private float mx0 = 0f;
    private float my0 = 0f;
    private float scaleX = 1f;
    private float scaleY = 1f;

    public Viewport(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int vpx(float mx) {
        return Math.round((mx - mx0) / scaleX);
    }

    public int vpy(float my) {
        return Math.round((my - my0) / scaleY);
    }

    public float mx(int vpx) {
        return vpx * scaleX + mx0;
    }

    public float my(int vpy) {
        return vpy * scaleY + my0;
    }

    public float getOriginX() {
        return mx0;
    }

    public float getOriginY() {
        return my0;
    }

    public void setOrigin(float mx, float my) {
        mx0 = mx;
        my0 = my;
    }

    public void setCenter(float mx, float my) {
        mx0 = mx - scaleX * width / 2f;
        my0 = my - scaleY * height / 2f;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setScale(float x, float y) {
        scaleX = x;
        scaleY = y;
    }
}
