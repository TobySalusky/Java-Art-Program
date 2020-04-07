package general;

import java.awt.*;

public class PaletteTab {

    private int rgb;
    private float x, y;
    private final static float size = 20;

    private boolean clicked;
    public static Palette palette;

    public PaletteTab(int rgb, float x, float y) {

        this.rgb = rgb;
        this.x = x;
        this.y = y;

    }

    public boolean checkClick(float mouseX, float mouseY) {

        if (mouseX >= x - size / 2 && mouseX <= x + size / 2) {
            if (mouseY >= y - size / 2 && mouseY <= y + size / 2) {

                clicked = !clicked;
                return true;

            }
        }
        return false;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void updateColor(int newRGB) {

        Canvas canvas = palette.getProgram().getCanvas();

        canvas.changeColor(rgb, newRGB);
        canvas.updateDisplay();

    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void draw(Graphics g) {

        float size = PaletteTab.size;

        if (clicked) {
            size *= 1.25F;
        }

        g.setColor(new Color(rgb));
        g.fillRect((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);

        g.setColor(Color.WHITE);
        g.drawRect((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);

    }

    public int getRGB() {
        return rgb;
    }
}
