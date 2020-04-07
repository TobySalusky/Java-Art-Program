package general;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Project {

    private Canvas canvas;
    private ArrayList<ArrayList<Layer>> undos = new ArrayList<ArrayList<Layer>>();

    protected String autoSaveName;
    private String lastSavedByName;

    private float initialCanvasX;
    private float initialCanvasY;
    private float initialCanvasWidth;
    private float initialCanvasHeight;

    private float defaultCanvasWidth;
    private float defaultCanvasHeight;

    private boolean selecting = false;
    private BufferedImage selected;
    private Point rectSelectStart;
    private Point rectSelectEnd;

    private Program program;

    protected boolean changesSinceAutoSave = false;

    public Project(Program program, String autoSaveName, int xPixel, int yPixel, float x, float y, float width, float height) {
        this.program = program;

        this.autoSaveName = autoSaveName;

        canvas = new Canvas(this, xPixel, yPixel, x, y, width, height);
        initialCanvasX = x;
        initialCanvasY = y;
        defaultCanvasWidth = width;
        defaultCanvasHeight = height;

        initialCanvasWidth = defaultCanvasWidth;
        initialCanvasHeight = defaultCanvasHeight;
    }

    public Project(Program program, String autoSaveName, float x, float y, float width, float height) {
        this.program = program;

        this.autoSaveName = autoSaveName;

        canvas = new Canvas(this, x, y, width, height);
        initialCanvasX = x;
        initialCanvasY = y;
        defaultCanvasWidth = width;
        defaultCanvasHeight = height;

        initialCanvasWidth = defaultCanvasWidth;
        initialCanvasHeight = defaultCanvasHeight;
    }

    public void copySelected(int index) {

        if (hasSelected()) {
            canvas.addLayer(index);

            BufferedImage image = canvas.getLayers().get(index).getImage();

            for (int xPixel = rectSelectStart.x; xPixel < rectSelectEnd.x; xPixel++) {
                for (int yPixel = rectSelectStart.y; yPixel < rectSelectEnd.y; yPixel++) {
                    image.setRGB(xPixel, yPixel, selected.getRGB(xPixel - rectSelectStart.x, yPixel - rectSelectStart.y));
                    System.out.println(xPixel + yPixel);
                }
            }
        }
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    public boolean isSelecting() {
        return selecting;
    }

    public boolean hasSelected() {
        if (selected != null) {
            return true;
        }
        return false;
    }

    public void selectDrag() {
        if (selecting) {
            rectSelectStart = getCanvas().getRectSelectStart();
            rectSelectEnd = getCanvas().getRectSelectEnd();
        }
    }

    public void selectRelease() {
        if (selecting) {

            rectSelectStart = getCanvas().getRectSelectStart();
            rectSelectEnd = getCanvas().getRectSelectEnd();

            if (rectSelectStart.x == rectSelectEnd.x || rectSelectStart.y == rectSelectEnd.y) {
                selected = null;
            } else {
                selected = getCanvas().getDisplayLayer().getSubimage(rectSelectStart.x, rectSelectStart.y, rectSelectEnd.x - rectSelectStart.x, rectSelectEnd.y - rectSelectStart.y);
            }

            selecting = false;
        }
    }


    public BufferedImage getSelected() {
        return selected;
    }

    public void setSelected(BufferedImage selected) {
        this.selected = selected;
    }

    public Point getRectSelectStart() {
        return rectSelectStart;
    }

    public void setRectSelectStart(Point rectSelectStart) {
        this.rectSelectStart = rectSelectStart;
    }

    public Point getRectSelectEnd() {
        return rectSelectEnd;
    }

    public void setRectSelectEnd(Point rectSelectEnd) {
        this.rectSelectEnd = rectSelectEnd;
    }

    public Program getProgram() {
        return program;
    }


    public void setProgram(Program program) {
        this.program = program;
    }


    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public ArrayList<ArrayList<Layer>> getUndos() {
        return undos;
    }

    public void setUndos(ArrayList<ArrayList<Layer>> undos) {
        this.undos = undos;
    }

    public float getInitialCanvasX() {
        return initialCanvasX;
    }

    public void setInitialCanvasX(float initialCanvasX) {
        this.initialCanvasX = initialCanvasX;
    }

    public float getInitialCanvasY() {
        return initialCanvasY;
    }

    public void setInitialCanvasY(float initialCanvasY) {
        this.initialCanvasY = initialCanvasY;
    }

    public float getInitialCanvasWidth() {
        return initialCanvasWidth;
    }

    public void setInitialCanvasWidth(float initialCanvasWidth) {
        this.initialCanvasWidth = initialCanvasWidth;
    }

    public float getInitialCanvasHeight() {
        return initialCanvasHeight;
    }

    public void setInitialCanvasHeight(float initialCanvasHeight) {
        this.initialCanvasHeight = initialCanvasHeight;
    }

    public float getDefaultCanvasWidth() {
        return defaultCanvasWidth;
    }

    public void setDefaultCanvasWidth(float defaultCanvasWidth) {
        this.defaultCanvasWidth = defaultCanvasWidth;
    }

    public float getDefaultCanvasHeight() {
        return defaultCanvasHeight;
    }

    public void setDefaultCanvasHeight(float defaultCanvasHeight) {
        this.defaultCanvasHeight = defaultCanvasHeight;
    }

    public String getLastSavedByName() {
        return lastSavedByName;
    }

    public void setLastSavedByName(String lastSavedByName) {
        this.lastSavedByName = lastSavedByName;
    }


}
