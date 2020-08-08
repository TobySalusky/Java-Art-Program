package util;

import java.awt.*;

public class Gizmo {

    public static final Color green = Color.GREEN, midOrange = new Color(240, 120, 0), darkGrey = new Color(15, 15, 15);

    public static void dottedLine(Graphics g, Vector from, Vector to) {
        dottedLine(g, from, to, 10, 4);
    }

    public static void dot(Graphics g, Vector pos, Color color) {
        g.setColor(color);
        dot(g, pos, 4);
    }

    public static void dot(Graphics g, Vector pos, float rad) {
        g.fillOval((int) (pos.x - rad), (int) (pos.y - rad), (int) (rad * 2), (int) (rad * 2));
    }

    public static void dottedLine(Graphics g, Vector from, Vector to, float dotLength, float midLength) {

        Vector diff = to.subbed(from);
        float angle = diff.angle(), mag = diff.mag();

        float progress = 0;

        do {
            drawLine(g, from.added(new Polar(progress, angle)), from.added(new Polar(Math.min(mag, progress + dotLength), angle)));

            progress += dotLength + midLength;
        } while (progress < mag);
    }

    public static void drawLine(Graphics g, Vector from, Vector to) {
        g.drawLine((int) from.x, (int) from.y, (int) to.x, (int) to.y);
    }
}
