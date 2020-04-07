package file_menu;

import general.Canvas;
import general.Layer;
import general.Program;
import general.Project;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

public abstract class FileTab {


    protected float x, y;
    private float toX, toY;

    protected float width, height;

    private float toWidth;
    private float toHeight;

    private boolean hovered = false;

    protected File file;

    protected Program program;

    private final static Color tabColor = new Color(45, 45, 45);

    private Font font;
    private String name;
    private String displayName;

    public FileTab(Program program, File file) {

        this.program = program;

        this.file = file;
        this.name = file.getName();

    }

    public static String getExtension(File file) {

        String name = file.getName();

        String extension = "";

        if (name.indexOf('.') != -1) {
            extension = name.substring(name.indexOf('.') + 1);
        }

        return extension;
    }

    public boolean hasExtension(String extension) {

        return (getExtension(file).equalsIgnoreCase(extension)); // perhaps add trim?

    }


    public void draw(Graphics g) {

        g.setColor(tabColor);

        g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);

        g.setColor(Color.LIGHT_GRAY);

        g.drawRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);

        g.setFont(font);

        if (displayName == null) { // find display name

            for (int i = 0; i < name.length(); i++) { // POLISH PLEASE
                if (g.getFontMetrics().stringWidth(name.substring(0, i + 1)) > (width * 0.8)) {
                    displayName = name.substring(0, i - 3) + "..."; //can crash if tooo short
                    break;
                }
            }

            if (displayName == null) {
                displayName = name;
            }
        }

        g.drawString(displayName, (int) (x - width / 2 * 0.8), (int) (y + height / 2 * 0.8));
    }

    public void tick(float scroll) {

        int divide = 10;

        if (hovered) {
            width /= 1.1;
            height /= 1.1;
        }

        x += (toX - x) / divide;
        y += ((toY - scroll) - y) / divide;
        width += (toWidth - width) / divide;
        font = findFont();

        height += (toHeight - height) / divide;

        if (hovered) {
            width *= 1.1;
            height *= 1.1;
        }
    }

    public Font findFont() {
        return new Font("Impact", Font.PLAIN, (int) (width / 10.5));
    }

    public void ifHover(float mouseX, float mouseY) {

        hovered = false;

        if (mouseX >= x - width / 2 && mouseX <= x + width / 2) {
            if (mouseY >= y - height / 2 && mouseY <= y + height / 2) {

                hovered = true;

            }
        }
    }

    public void checkClick(float mouseX, float mouseY) {

        if (mouseX >= x - width / 2 && mouseX <= x + width / 2) {
            if (mouseY >= y - height / 2 && mouseY <= y + height / 2) {

                clickEvent();

            }
        }
    }

    public abstract void clickEvent();

    public void setDimensions(float x, float y, float width, float height) {

        this.toX = x;
        this.toY = y;
        this.toWidth = width;
        this.toHeight = height;

        if (font == null) {
            this.x = x; // if first time, will set x and y
            this.y = y;
            this.width = width;
            this.height = height;
        }

        this.font = new Font("Impact", Font.PLAIN, (int) (width / 10.5));
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return this.y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getToX() {
        return toX;
    }

    public void setToX(float toX) {
        this.toX = toX;
    }

    public float getToY() {
        return toY;
    }

    public void setToY(float toY) {
        this.toY = toY;
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

    public float getToWidth() {
        return toWidth;
    }

    public void setToWidth(float toWidth) {
        this.toWidth = toWidth;
    }

    public float getToHeight() {
        return toHeight;
    }

    public void setToHeight(float toHeight) {
        this.toHeight = toHeight;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Color getTabColor() {
        return tabColor;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setY(float y) {
        this.y = y;
    }


}
