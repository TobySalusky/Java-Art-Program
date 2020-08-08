package general;

import util.Vector;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Project {

    private Canvas mainCanvas;
    private Canvas lastSelectedCanvas;
    private List<Canvas> canvasList = new ArrayList<>();

    protected String autoSaveName;
    private String lastSavedByName;

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

        defaultCanvasWidth = width;
        defaultCanvasHeight = height;

        mainCanvas = new Canvas(this, xPixel, yPixel, x, y, width, height);
        canvasList.add(mainCanvas);
    }

    public Project(Program program, String autoSaveName, float x, float y, float width, float height) {
        this(program, autoSaveName, Canvas.defaultPixelX, Canvas.defaultPixelY, x, y, width, height);
    }

    public void addBasicCanvas(int xPixels, int yPixels) {

        for (Canvas canvas : canvasList) {
            canvas.resetPosition();
        }

        Canvas last = canvasList.get(canvasList.size() - 1);
        Vector lastPos = last.getInitialPos();

        Canvas newCanvas = new Canvas(this, xPixels, yPixels, lastPos.x, lastPos.y, 0, 0);
        newCanvas.initPos(lastPos.added(last.getInitialDimen().x / 2 + newCanvas.getInitialDimen().x / 2 + 50, 0));
        newCanvas.resetPosition();

        canvasList.add(newCanvas);
    }

    public void setLastSelectedCanvas(Canvas lastSelectedCanvas) {
        this.lastSelectedCanvas = lastSelectedCanvas;
    }

    public void copySelected(int index) {

        if (hasSelected()) {
            mainCanvas.addLayer(index);

            BufferedImage image = mainCanvas.getLayers().get(index).getImage();

            for (int xPixel = rectSelectStart.x; xPixel < rectSelectEnd.x; xPixel++) {
                for (int yPixel = rectSelectStart.y; yPixel < rectSelectEnd.y; yPixel++) {
                    image.setRGB(xPixel, yPixel, selected.getRGB(xPixel - rectSelectStart.x, yPixel - rectSelectStart.y));
                    System.out.println(xPixel + yPixel);
                }
            }
        }
    }

    public List<Canvas> getCanvasList() {
        return canvasList;
    }

    public Canvas getLastSelectedCanvas() {

        if (lastSelectedCanvas != null) {
            return lastSelectedCanvas;
        }

        return mainCanvas;
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
            rectSelectStart = getMainCanvas().getRectSelectStart();
            rectSelectEnd = getMainCanvas().getRectSelectEnd();
        }
    }

    public void selectRelease() {
        if (selecting) {

            rectSelectStart = getMainCanvas().getRectSelectStart();
            rectSelectEnd = getMainCanvas().getRectSelectEnd();

            if (rectSelectStart.x == rectSelectEnd.x || rectSelectStart.y == rectSelectEnd.y) {
                selected = null;
            } else {
                selected = getMainCanvas().getDisplayLayer().getSubimage(rectSelectStart.x, rectSelectStart.y, rectSelectEnd.x - rectSelectStart.x, rectSelectEnd.y - rectSelectStart.y);
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


    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
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
