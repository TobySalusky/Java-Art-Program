package general;

import java.awt.Color;
import java.awt.Graphics;

public class GraphicsUtilities {

    /**
     * Draws a rectangle of the given color from (0, 0) with the given width and height
     *
     * @param g      the Graphics object
     * @param color  the Color of the background
     * @param width  the width of the rectangle
     * @param height the height of the background
     */
    public static void drawBackground(Graphics g, Color color, int width, int height) {
        g.setColor(color);
        g.fillRect(0, 0, width, height);
    }


    /**
     * Creates a grid of vertical and horizontal lines starting at (0,0) to the given
     * width and height at the given interval.
     *
     * @param g         the Graphics object
     * @param color     the color of the lines
     * @param width     the width of the grid
     * @param height    the height of the grid
     * @param increment the distance between each line
     */
    public static void drawGrid(Graphics g, Color color, int width, int height, int increment) {

    }
}