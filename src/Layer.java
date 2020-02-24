import java.awt.image.BufferedImage;

public class Layer {

    private BufferedImage image;
    private boolean visible = true;
    private LayerButton button;
    private int index;
    private float opacity = 100;

    private Program program;

    public Layer(BufferedImage image, int index, Program program) {
        this.program = program;

        this.image = image;
        this.index = index;

        this.button = new LayerButton(this);
    }

    public Layer(String name, BufferedImage image, int index, Program program) {
        this.program = program;

        this.image = image;
        this.index = index;

        this.button = new LayerButton(name, this);
    }


    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public LayerButton getButton() {
        return button;
    }


    public void setButton(LayerButton button) {
        this.button = button;
    }


    public int getIndex() {
        return index;
    }


    public void setIndex(int index) {
        this.index = index;
    }


    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }


}
