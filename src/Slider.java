import java.awt.Color;
import java.awt.Graphics;

public class Slider {

    private float x;
    private float y;
    private float width;
    private float height;
    private float slidePercent = 0.5F;
    private float sliderWidth = 10;
    private Type type;
    private boolean clicked = false;
    private float min = 1;
    private float max = 10;

    private Program program;

    public enum Type {
        brushSize, brushRed, brushGreen, brushBlue, stabilizer, opacity, fileTabSize;
    }

    public Slider(float x, float y, float width, float height, Program program) {

        this.program = program;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        minMax();
        startSlide();
    }

    public Slider(float x, float y, float width, float height, Type type, Program program) {
        this.program = program;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;

        minMax();
        startSlide();
    }

    public void minMax() {

        switch (type) {

            case brushSize:
                min = 1.01F;
                max = 20;
                break;
            case brushRed:
                min = 0;
                max = 255;
                break;
            case brushGreen:
                min = 0;
                max = 255;
                break;
            case brushBlue:
                min = 0;
                max = 255;
                break;
            case stabilizer:
                min = 0;
                max = 40;
                break;
            case opacity:
                min = 0;
                max = 100;
                break;
            case fileTabSize:
                min = 10;
                max = 500;
                break;
        }

    }

    public void tick(float mouseX) {

        if (clicked) {

            sliderToMouse(mouseX);
            makeChange();

        }

    }

    public float getAmount() {

        return min + (max - min) * slidePercent;

    }

    public void startSlide() {

        switch (type) {

            case brushSize:
                slidePercent = (program.brushSize - min) / (max - min);
                break;
            case brushRed:
                slidePercent = (program.wheelRed - min) / (max - min);
                break;
            case brushGreen:
                slidePercent = (program.wheelGreen - min) / (max - min);
                break;
            case brushBlue:
                slidePercent = (program.wheelBlue - min) / (max - min);
                break;
            case stabilizer:
                slidePercent = (program.stabilizer - min) / (max - min);
                break;
            case opacity:
                slidePercent = (program.getCanvas().getSelectedLayer().getOpacity() - min) / (max - min);
                break;
            case fileTabSize:
                slidePercent = (program.getFileMenu().getTabSize() - min) / (max - min);
                break;
        }

    }

    public void makeChange() {

        switch (type) {

            case brushSize:
                program.brushSize = getAmount();
                break;
            case brushRed:
                program.wheelRed = (int) getAmount();
                program.mixColor();
                program.colorWheelChange();
                break;
            case brushGreen:
                program.wheelGreen = (int) getAmount();
                program.mixColor();
                program.colorWheelChange();
                break;
            case brushBlue:
                program.wheelBlue = (int) getAmount();
                program.mixColor();
                program.colorWheelChange();
                break;
            case stabilizer:
                program.stabilizer = (int) getAmount();
                break;
            case opacity:
                program.getCanvas().getSelectedLayer().setOpacity(getAmount());
                program.getCanvas().updateDisplay();
                break;
            case fileTabSize:
                program.getFileMenu().setTabSize((int) getAmount());
                program.getFileMenu().setTabPositions();
                break;
        }

    }

    public void sliderToMouse(float mouseX) {

        float sliderX = Math.min(Math.max(mouseX, (x - width / 2)), (x + width / 2));

        slidePercent = (sliderX - (x - width / 2)) / width;

    }

    public void checkClick(float mouseX, float mouseY) {

        if (mouseX >= x - width / 2 && mouseX <= x + width / 2) {
            if (mouseY >= y - height / 2 && mouseY <= y + height / 2) {
                clicked = true;
            }
        }
    }

    public void removeClick() {
        clicked = false;
    }

    public void draw(Graphics g) {

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
        g.setColor(Color.BLACK);
        if (clicked) g.setColor(Color.RED);

        float sliderX = Math.min(Math.max(((x - width / 2) + (slidePercent * width) - (sliderWidth / 2)), (x - width / 2)), (x + width / 2 - sliderWidth));

        g.fillRect((int) sliderX, (int) (y - height / 2), (int) sliderWidth, (int) height);

    }

}