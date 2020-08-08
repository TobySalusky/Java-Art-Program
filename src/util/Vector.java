package util;

public class Vector {

    public float x;
    public float y;

    public Vector() {
        setTo(0F, 0F);
    }

    public Vector(float x, float y) {
        setTo(x, y);
    }

    @Override
    public final String toString() {
        return "vec<" + x + ", " + y + ">";
    }

    public final void add(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public final void add(Vector vec) {
        x += vec.x;
        y += vec.y;
    }

    public final void sub(Vector vec) {
        x -= vec.x;
        y -= vec.y;
    }

    public final float normDot(Vector vec) {
        return normalized().dot(vec);
    }

    public final float dot(Vector vec) {
        return x * vec.x + y * vec.y;
    }

    public final void reset() {
        x = 0F;
        y = 0F;
    }

    public final void setTo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public final Vector added(Vector vec) {
        return new Vector(x + vec.x, y + vec.y);
    }

    public final Vector added(float x, float y) {
        return new Vector(this.x + x, this.y + y);
    }

    public final Vector subbed(Vector vec) {
        return new Vector(x - vec.x, y - vec.y);
    }

    public final Vector scaled(float scalar) {

        float angle = angle();
        float mag = mag() * scalar;

        return new Vector(mag * (float) Math.cos(angle), mag * (float) Math.sin(angle));
    }

    public final Vector normalized() {
        return scaled(1F / mag());
    }

    public final Vector copy() {
        return new Vector(x, y);
    }

    public final void clamp(float min, float max) {
        setMag(Math.min(Math.max(mag(), min), max));
    }

    public final void mult(float scalar) {
        setMag(mag() * scalar);
    }

    public final float mag() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public final void addMag(float mag) {
        setMag(mag() + mag);
    }

    public final void subMag(float mag) {
        addMag(-mag);
    }

    public final void setMag(float mag) {

        resize(mag, angle());

    }

    public final void resize(float mag, float angle) {
        x = mag * (float) Math.cos(angle);
        y = mag * (float) Math.sin(angle);
    }

    public final void setAngle(float angle) {
        resize(mag(), angle);
    }

    public final void rotate(float radians) {

        resize(mag(), angle() + radians);
    }

    public final float angle() {
        return (float) Math.atan2(y, x);
    }

    // TODO: dot and cross products
}