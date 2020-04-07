package general;

import java.awt.Color;
import java.awt.Graphics;

public class LayerButton {

    private float x;
    private float y;

    private float toY;
    private float keepFromY;

    private float width;
    private float height;
    private Layer layer;
    private String layerName = "unnamed layer";
    private boolean clicked = false;
    private float clickShrink = 4;

    private float initialVisibleWidth = 15;
    private float visibleWidth = initialVisibleWidth;
    private float visibleHeight = initialVisibleWidth;
    private boolean visible = true;
    private boolean visibleClicked = false;

    private Program program;

    public LayerButton(Layer layer) {
        this.program = layer.getProgram();

        setUp(layer);
        this.layerName = "layer " + (layer.getIndex() + 1);
    }

    public LayerButton(String name, Layer layer) {
        this.program = layer.getProgram();

        setUp(layer);
        this.layerName = name;
    }

    public void setUp(Layer layer) {
        this.layer = layer;
        this.x = Program.WIDTH - 60;
        findToY();
        this.y = toY;
        this.width = 100;
        this.height = 50;
    }

    public void findToY() {
        this.toY = Program.HEIGHT - 35 - 55 * layer.getIndex();
    }

    public void indexLayer(int index) {
        layer.setIndex(index);
        findToY();
    }

    public void tick() { // laggggg
        y += (toY - y) / 5;
    }

    public void draw(Graphics g) {

        if (clicked) {
            width -= clickShrink;
            height -= clickShrink;
        }

        if (program.getCanvas().getSelectedLayer() == layer) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
        }

        g.setColor(Color.LIGHT_GRAY);
        g.drawRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
        g.drawString(layerName, (int) (x - width / 2 + 5), (int) y);

        // visibility box //YUCKY GARBO PLLLEAASE OPTIMIZE!!! please fix
        visible = layer.isVisible();

        if (visibleClicked) {
            visibleWidth -= clickShrink;
            visibleHeight -= clickShrink;
        }

        if (visible) {
            g.fillRect((int) ((x + width / 2 - initialVisibleWidth * 1) - visibleWidth / 2), (int) ((y) - visibleHeight / 2), (int) visibleWidth, (int) visibleHeight);
        } else {
            g.drawRect((int) ((x + width / 2 - initialVisibleWidth * 1) - visibleWidth / 2), (int) ((y) - visibleHeight / 2), (int) visibleWidth, (int) visibleHeight);
        }

        if (visibleClicked) {
            visibleWidth += clickShrink;
            visibleHeight += clickShrink;
        }


        if (clicked) {
            width += clickShrink;
            height += clickShrink;
        }
    }

    public boolean onVisible(float mouseX, float mouseY) {

        if (mouseX >= (x + width / 2 - initialVisibleWidth * 1) - visibleWidth / 2 && mouseX <= (x + width / 2 - initialVisibleWidth * 1) + visibleWidth / 2) {
            if (mouseY >= y - visibleHeight / 2 && mouseY <= y + visibleHeight / 2) {
                return true;
            }
        }

        return false;
    }

    public void drag(float mouseY) {

        y = mouseY + keepFromY;

    }

    public void checkClick(float mouseX, float mouseY) {

        if (mouseX >= x - width / 2 && mouseX <= x + width / 2) {
            if (mouseY >= y - height / 2 && mouseY <= y + height / 2) {

                program.addUndo();

                if (onVisible(mouseX, mouseY)) {

                    if (visible) {
                        layer.setVisible(false);
                    } else {
                        layer.setVisible(true);
                    }
                    visibleClicked = true;

                    program.getCanvas().updateDisplay(); // TERRIBLE AAAAAAA please fix

                } else {
                    program.getCanvas().setSelectedLayer(layer.getIndex());
                    clicked = true;
                    program.setDraggedLayerButton(this);

                    keepFromY = y - mouseY;
                }
            }
        }
    }


    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public float getClickShrink() {
        return clickShrink;
    }

    public void setClickShrink(float clickShrink) {
        this.clickShrink = clickShrink;
    }

    public float getVisibleWidth() {
        return visibleWidth;
    }

    public void setVisibleWidth(float visibleWidth) {
        this.visibleWidth = visibleWidth;
    }

    public float getVisibleHeight() {
        return visibleHeight;
    }

    public void setVisibleHeight(float visibleHeight) {
        this.visibleHeight = visibleHeight;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisibleClicked() {
        return visibleClicked;
    }

    public void setVisibleClicked(boolean visibleClicked) {
        this.visibleClicked = visibleClicked;
    }


}