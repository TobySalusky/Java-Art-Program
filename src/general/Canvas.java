package general;

import util.Vector;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Canvas {

    private float x, y;
    private float width, height;

    private Vector initialPos;
    private Vector initialDimen;

    private float displayX, displayY;
    private float displayWidth, displayHeight;
    private static final float displayDivider = 5;

    private int programWidth = Program.WIDTH;
    private int programHeight = Program.HEIGHT;
    private int canvasDivider = Program.canvasDivider;

    //private int xPixel = programWidth / canvasDivider;
    //private int yPixel = programHeight / canvasDivider;

    public static final int defaultPixelX = 16, defaultPixelY = defaultPixelX;

    private int xPixels;
    private int yPixels;

    private final Project project;

    private int selectedLayer = 0;
    private List<Layer> layers = new ArrayList<>();

    private float lastX = 0;
    private float lastY = 0;

    private float brushX = 0;
    private float brushY = 0;
    private float brushCanvasX = 0;
    private float brushCanvasY = 0;

    private Point rectSelect1;
    private Point rectSelect2;
    private Point rectSelectStart;
    private Point rectSelectEnd;

    private int pixelsFilled = 0;

    public static final Color erasedColor = new Color(255, 255, 255, 0);
    public static final int erasedColorRGB = erasedColor.getRGB();
    private static final Color backgroundColor = Color.LIGHT_GRAY, selectedOut = Color.ORANGE;

    private static final int fillColor = new Color(13, 138, 17, 29).getRGB();

    private BufferedImage displayLayer;

    // THIS NEEDS TO BE UPDATED!!! please fix
    private BufferedImage nullLayer;


    private List<List<Layer>> undos = new ArrayList<>();

    private boolean referenceCanvas = false;

    public Canvas(Project project, int xPixel, int yPixel, float x, float y, float width, float height) {
        this.project = project;

        this.xPixels = xPixel;
        this.yPixels = yPixel;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.displayX = x;
        this.displayY = y;
        this.displayWidth = width;
        this.displayHeight = height;

        initialPos = new Vector(x, y);
        initialDimen = new Vector(width, height);

        // setup
        addLayer();
        displayLayer = singleLayer();

        nullLayer = new BufferedImage(xPixel, yPixel, BufferedImage.TYPE_INT_ARGB);
        fillNullLayer();

        resetCanvas();
        findSize();
        resetPosition();
    }

    public boolean isReferenceCanvas() {
        return referenceCanvas;
    }

    public void setReferenceCanvas(boolean referenceCanvas) {
        this.referenceCanvas = referenceCanvas;
    }

    public void addUndo(List<Layer> layerList) {
        undos.add(0, layerList);
    }

    public void deleteUndos() {

        while (undos.size() > 0) {
            undos.remove(0);
        }
    }

    public void undo() {

        if (undos.size() >= 1) {
            undoTo(undos.get(0));
            undos.remove(0);
            System.out.println("undo successful");
        }

    }

    public void expandCanvas(int top, int bottom, int left, int right) {

        project.getProgram().addUndo();

        int oldX = xPixels;
        int oldY = yPixels;

        xPixels += left + right;
        yPixels += top + bottom;

        for (Layer layer : layers) {

            BufferedImage image = layer.getImage();
            BufferedImage newImage = new BufferedImage(xPixels, yPixels, BufferedImage.TYPE_INT_ARGB);

            for (int i = 0; i < xPixels; i++) { // WHAT THE FRICK BRO, OPTIMISE THIS PLEASE
                for (int j = 0; j < yPixels; j++) {
                    newImage.setRGB(i, j, erasedColorRGB);
                }
            }

            for (int x = 0; x < oldX; x++) {
                for (int y = 0; y < oldY; y++) {
                    int thisX = x + left, thisY = y + top;
                    if (thisX >= 0 && thisX < xPixels && thisY >= 0 && thisY < yPixels) {
                        newImage.setRGB(thisX, thisY, image.getRGB(x, y));
                    }
                }
            }

            layer.setImage(newImage);
        }

        findSize();
        updateDisplay();
        resetPosition();
    }

    public void undoTo(List<Layer> undo) {

        setLayers(undo);

        BufferedImage image = layers.get(0).getImage();
        int undoX = image.getWidth(), undoY = image.getHeight();
        if (undoX != xPixels || undoY != yPixels) {

            xPixels = undoX;
            yPixels = undoY;

            findSize();
            updateDisplay();
            resetPosition();
        }

    }

    public int getUndoCount() {
        return undos.size();
    }

    public void changeColor(int oldRGB, int newRGB) {

        for (Layer layer : layers) {
            for (int xPixel = 0; xPixel < xPixels; xPixel++) {
                for (int yPixel = 0; yPixel < yPixels; yPixel++) {

                    if (layer.getImage().getRGB(xPixel, yPixel) == oldRGB) {
                        layer.getImage().setRGB(xPixel, yPixel, newRGB);
                    }

                }
            }
        }
    }

    public List<Integer> getUniqueColors() {

        List<Integer> colors = new ArrayList<>();

        for (Layer layer : layers) {
            for (int xPixel = 0; xPixel < xPixels; xPixel++) {

                for (int yPixel = 0; yPixel < yPixels; yPixel++) {

                    int color = layer.getImage().getRGB(xPixel, yPixel);

                    if (!colors.contains(color)) { // OPTIMIZE, please, hashmap perhaps?
                        colors.add(color);
                    }

                }

            }
        }
        return colors;
    }

    public BufferedImage getDisplayLayer() {
        return displayLayer;
    }


    public void setDisplayLayer(BufferedImage displayLayer) {
        this.displayLayer = displayLayer;
    }


    public void resortLayers() {

        for (int i = 0; i < layers.size(); i++) {

            Layer layer = layers.get(i);

            if (layer.getIndex() != i) {
                moveLayerTo(layer, layer.getIndex());
                i = 0;
            }

        }

    }

    public void moveLayerTo(Layer layer, int index) {
        layers.remove(layer);
        layers.add(index, layer);
    }

    public void fillNullLayer() {
        for (int i = 0; i < xPixels; i++) {
            for (int j = 0; j < yPixels; j++) {
                nullLayer.setRGB(i, j, erasedColorRGB);
            }
        }
    }

    public void findSize() {
        int xPixel = getxPixels();
        int yPixel = getyPixels();

        float sizeMult = project.getDefaultCanvasWidth() / xPixel;
        sizeMult = Math.min(sizeMult, project.getDefaultCanvasHeight() / yPixel);

        initialDimen = new Vector(sizeMult * xPixel, sizeMult * yPixel);

        nullLayer = new BufferedImage(xPixel, yPixel, BufferedImage.TYPE_INT_ARGB);
        fillNullLayer();
    }

    public void setSelectedLayerIndex(int index) {
        selectedLayer = index;
    }

    public void updateDisplay() {
        displayLayer = singleLayer();

        project.getProgram().updateEvents();
    }

    public BufferedImage singleLayer() {

        BufferedImage singleLayer;

        Layer firstLayer = layers.get(0);

        if (firstLayer.isVisible()) {
            if (firstLayer.getOpacity() == 100) {
                singleLayer = firstLayer.getImage();
            } else {
                singleLayer = combineOpacity(firstLayer.getImage(), nullLayer, firstLayer.getOpacity());
            }
        } else {
            singleLayer = nullLayer; // please fix will cause issues with changing size
        }

        for (int i = 1; i < layers.size(); i++) {
            Layer topLayer = layers.get(i);

            if (topLayer.isVisible()) {
                if (firstLayer.getOpacity() == 100) {
                    singleLayer = combine(topLayer.getImage(), singleLayer);
                } else {
                    singleLayer = combineOpacity(topLayer.getImage(), singleLayer, topLayer.getOpacity());
                }
            }
        }

        return singleLayer;
    }

    public void updateFrom(int x1, int y1, int x2, int y2) {

        x1 = bindX(x1);
        y1 = bindY(y1);
        x2 = bindX(x2);
        y2 = bindY(y2);

        for (int i = 0; i < layers.size(); i++) {
            for (int row = x1; row <= x2; row++) {
                for (int col = y1; col <= y2; col++) {
                    // OPTIMIZE PLEASE FIX
                    int pixelColor = layers.get(i).getImage().getRGB(row, col);

                    if (pixelColor != erasedColorRGB && layers.get(i).isVisible()) {
                        displayLayer.setRGB(row, col, pixelColor);
                    } else {
                        if (i == 0) {
                            if (layers.get(i).isVisible()) {
                                displayLayer.setRGB(row, col, pixelColor);
                            } else {
                                displayLayer.setRGB(row, col, erasedColorRGB);
                            }
                        }
                    }
                }
            }
        }

        project.getProgram().updateEvents();
    }

    public BufferedImage combine(BufferedImage topLayer, BufferedImage bottomLayer) {

        BufferedImage newLayer = new BufferedImage(xPixels, yPixels, BufferedImage.TYPE_INT_ARGB);

        for (int row = 0; row < xPixels; row++) {
            for (int col = 0; col < yPixels; col++) {
                newLayer.setRGB(row, col, bottomLayer.getRGB(row, col));
            }
        }

        for (int row = 0; row < xPixels; row++) {
            for (int col = 0; col < yPixels; col++) {
                int pixelColor = topLayer.getRGB(row, col);

                if (pixelColor != erasedColorRGB) {
                    newLayer.setRGB(row, col, pixelColor);
                }
            }
        }

        return newLayer;
    }

    public BufferedImage combineOpacity(BufferedImage topLayer, BufferedImage bottomLayer, float topOpacity) {

        topOpacity /= 100;

        BufferedImage newLayer = new BufferedImage(xPixels, yPixels, BufferedImage.TYPE_INT_ARGB);

        for (int row = 0; row < xPixels; row++) {
            for (int col = 0; col < yPixels; col++) {
                newLayer.setRGB(row, col, bottomLayer.getRGB(row, col));
            }
        }

        for (int row = 0; row < xPixels; row++) {
            for (int col = 0; col < yPixels; col++) {
                int pixelColor = topLayer.getRGB(row, col);

                if (pixelColor != erasedColorRGB) {

                    Color bottomColor = new Color(bottomLayer.getRGB(row, col), true);

                    int bottomOpacity = bottomColor.getAlpha();

                    Color topColor = new Color(pixelColor);

                    int red = (int) (topColor.getRed() * topOpacity + bottomColor.getRed() * (1 - topOpacity));
                    int green = (int) (topColor.getGreen() * topOpacity + bottomColor.getGreen() * (1 - topOpacity));
                    int blue = (int) (topColor.getBlue() * topOpacity + bottomColor.getBlue() * (1 - topOpacity));
                    // 255 = max
                    int opacity = (int) (Math.min((bottomOpacity + topOpacity * 255), 255));

                    Color newColor = new Color(red, green, blue, opacity);

                    newLayer.setRGB(row, col, newColor.getRGB());
                }
            }
        }

        return newLayer;
    }

    public void addLayer() {
        layers.add(new Layer(new BufferedImage(xPixels, yPixels, BufferedImage.TYPE_INT_ARGB), layers.size(), project.getProgram()));
        resetLayer(layers.size() - 1);
    }

    public void addLayer(int index) {
        layers.add(index, new Layer("general.Layer " + (layers.size() + 1), new BufferedImage(xPixels, yPixels, BufferedImage.TYPE_INT_ARGB), index, project.getProgram()));
        resetLayer(index);

        for (int i = index + 1; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            layer.setIndex(layer.getIndex() + 1);
            layer.getButton().findToY();
        }
    }

    public void moveLayer(Layer layer, int newPosition) {
        layers.remove(layer);
        layers.add(newPosition, layer); //please fix?
    }

    public Layer getSelectedLayer() {
        return layers.get(getSelectedLayerIndex());
    }

    public int getSelectedLayerIndex() {
        selectedLayer(selectedLayer);
        return selectedLayer;
    }

    public int getLayerCount() {
        return layers.size();
    }

    public void selectedLayer(int index) {
        selectedLayer = Math.min(Math.max(index, 0), layers.size() - 1);
    }

    public void resetPosition() {

        setX(initialPos.x);
        setY(initialPos.y);
        setWidth(initialDimen.x);
        setHeight(initialDimen.y);

    }

    public void resetCanvas() {

        // reset layers
        for (int i = 0; i < xPixels; i++) {
            for (int j = 0; j < yPixels; j++) {

                for (int layer = 0; layer < layers.size(); layer++) {
                    layers.get(layer).getImage().setRGB(i, j, erasedColorRGB);
                }
            }
        }
    }

    public void resetLayer(int layerIndex) {
        for (int i = 0; i < xPixels; i++) {
            for (int j = 0; j < yPixels; j++) {
                layers.get(layerIndex).getImage().setRGB(i, j, erasedColorRGB);
            }
        }
    }

    public float toBrushCanvasX(float mouseX) {
        return xPixels * ((mouseX - (displayX - displayWidth / 2)) / displayWidth);
    }

    public float toBrushCanvasY(float mouseY) {
        return yPixels * ((mouseY - (displayY - displayHeight / 2)) / displayHeight);
    }

    public void fill(int x, int y, int oldColor, int newColor) {

        Layer onLayer = getSelectedLayer();

        ArrayList<Point> points = new ArrayList<Point>();

        onLayer.getImage().setRGB(x, y, newColor);
        points.add(new Point(x, y));

        while (points.size() > 0) {
            Point point = points.get(0);

            onLayer.getImage().setRGB(point.x, point.y, newColor);

            tryFill(onLayer, points, point.x - 1, point.y, oldColor, newColor);
            tryFill(onLayer, points, point.x + 1, point.y, oldColor, newColor);
            tryFill(onLayer, points, point.x, point.y - 1, oldColor, newColor);
            tryFill(onLayer, points, point.x, point.y + 1, oldColor, newColor);
            points.remove(0);
        }

    }

    public void tryFill(Layer onLayer, ArrayList<Point> points, int x, int y, int oldColor, int newColor) {
        if (mayFill(x, y, oldColor, newColor)) {
            onLayer.getImage().setRGB(x, y, newColor);
            updateFrom(x, y, x, y);
            points.add(new Point(x, y));
        }
    }

    public boolean mayFill(int x, int y, int oldColor, int newColor) {

        if (oldColor == newColor) {
            return false;
        }

        if (x >= 0 && x < xPixels && y >= 0 && y < yPixels) {

            if (displayLayer.getRGB(x, y) == oldColor) {
                return true;
            }

        }

        return false;
    }

    // these methods are for the outdated filling
    public void fill(int brushColor, int onColor) {

        BufferedImage layer = layers.get(selectedLayer).getImage();

        int fillIterations = 0;

        pixelsFilled = 0;
        do {
            fillIterations++;
            pixelsFilled = 0;

            if (fillIterations % 2 == 0) {
                for (int row = layer.getWidth() - 1; row >= 0; row--) {
                    for (int col = layer.getHeight() - 1; col >= 0; col--) {
                        fillAround(row, col, onColor);
                    }
                }
            } else {
                for (int row = 0; row < layer.getWidth(); row++) {
                    for (int col = 0; col < layer.getHeight(); col++) {
                        fillAround(row, col, onColor);
                    }
                }
            }

        } while (pixelsFilled > 0);

        // change 2's to 1's
        for (int row = 0; row < layer.getWidth(); row++) {

            for (int col = 0; col < layer.getHeight(); col++) {

                if (layer.getRGB(row, col) == fillColor) { //make fill color rgb, please fix
                    layer.setRGB(row, col, brushColor); //please fix
                }

            }
        }

        System.out.println("DEBUG: fill iterations: " + fillIterations);

    }

    public void fillAround(int row, int col, int onColor) {

        BufferedImage layer = layers.get(selectedLayer).getImage();

        if (layer.getRGB(row, col) == fillColor) { // sub-par code, please fix

            int pixelIntValue;

            for (int i = 0; i < 2; i++) {
                pixelIntValue = (Math.min(Math.max(row - (i * 2 - 1), 0), layer.getWidth() - 1));

                canFill(pixelIntValue, col, onColor);
            }

            for (int i = 0; i < 2; i++) {
                pixelIntValue = (Math.min(Math.max(col - (i * 2 - 1), 0), layer.getHeight() - 1));

                canFill(row, pixelIntValue, onColor);
            }
        }
    }

    public void fillPixel(int xPixel, int yPixel, BufferedImage layer) {
        layer.setRGB(xPixel, yPixel, fillColor);
        pixelsFilled++;
    }

    public void canFill(int xPixel, int yPixel, int onColor) {

        int pixelColor = displayLayer.getRGB(xPixel, yPixel);

        BufferedImage image = layers.get(selectedLayer).getImage();

        if (pixelColor == onColor && image.getRGB(xPixel, yPixel) != fillColor) {
            fillPixel(xPixel, yPixel, image);
        }
    }
    // this concludes the methods are for the outdated filling


    public boolean onCanvas(float brushX, float brushY) {

        if (brushX >= 0 && brushX <= layers.get(selectedLayer).getImage().getWidth() - 1) {
            if (brushY >= 0 && brushY <= layers.get(selectedLayer).getImage().getHeight() - 1) {
                return true;
            }
        }

        return false;
    }

    public int bindX(int brushX) { //please fix, use this for fill, please bud
        return (Math.min(Math.max(brushX, 0), layers.get(selectedLayer).getImage().getWidth() - 1));
    }

    public int bindY(int brushY) { // also mostly unnecessary
        return (Math.min(Math.max(brushY, 0), layers.get(selectedLayer).getImage().getHeight() - 1));
    }

    protected boolean canEdit() {
        return !referenceCanvas;
    }

    protected boolean canSelect() {
        return true;
    }

    public void initPos(Vector initialPos) {
        this.initialPos = initialPos;
    }

    public void setInitPosDimen() {
        initialPos = new Vector(x, y);
        initialDimen = new Vector(width, height);
    }

    public void brush(float mouseX, float mouseY, float brushSize, float stabilizer, Color brushColor) {

        if (canSelect() && onCanvas(brushX, brushY)) {
            project.setLastSelectedCanvas(this);
        }

        if (layers.get(selectedLayer).isVisible()) { // TODO: add warning if layer isn't visible... annoying.

            BufferedImage layer = layers.get(selectedLayer).getImage();

            brushCanvasX = toBrushCanvasX(mouseX);
            brushCanvasY = toBrushCanvasY(mouseY);

            brushX += (brushCanvasX - brushX) / (Math.max(stabilizer, 1));
            brushY += (brushCanvasY - brushY) / (Math.max(stabilizer, 1));

            if (canEdit()) { // MUST BE EDITABLE
                switch (Program.selectedBrush) {

                    case fill:
                        if (onCanvas(brushX, brushY) && Program.initialClick) {

                            long startTime = System.nanoTime();

                            Program.initialClick = false;

                            int colorOn = displayLayer.getRGB((int) brushX, (int) brushY);

                            fill((int) brushX, (int) brushY, colorOn, brushColor.getRGB());

                            long endTime = System.nanoTime();
                            long duration = (endTime - startTime) / 1000000;
                            System.out.println("fill took " + duration + " miliseconds");

                            project.changesSinceAutoSave = true;
                        }
                        break;

                    case brush:

                        if (Program.initialClick) {
                            Program.initialClick = false;

                            lastX = brushX;
                            lastY = brushY;
                        }

                        drawAt(brushX, brushY, layer, brushSize / 2, brushColor);

                        drawLineFrom(lastX, lastY, brushX, brushY, layer, brushSize / 2, brushColor);

                        lastX = brushX;
                        lastY = brushY;

                        project.changesSinceAutoSave = true;
                        break;

                    case eraser:

                        if (Program.initialClick) {
                            Program.initialClick = false;

                            lastX = brushX;
                            lastY = brushY;
                        }

                        eraseAt(brushX, brushY, layer, brushSize / 2);

                        eraseLineFrom(lastX, lastY, brushX, brushY, layer, brushSize / 2);

                        lastX = brushX;
                        lastY = brushY;

                        project.changesSinceAutoSave = true;
                        break;

                    default:
                        break;
                }
            }
            switch (Program.selectedBrush) { // NON-EDITING
                case colorGrab:
                    if (onCanvas(brushX, brushY)) {
                        int colorGrabbed = displayLayer.getRGB((int) brushX, (int) brushY);

                        project.getProgram().changeBrushColor(new Color(colorGrabbed));

                        Program.setSliders();
                    }
                    break;


                case rectSelect:

                    if (Program.initialClick || rectSelect1 == null) {
                        Program.initialClick = false;

                        rectSelect1 = new Point(bindX((int) brushX), bindY((int) brushY));

                        project.setSelecting(true);
                    }
                    int rectSelect2X = bindX((int) brushX);
                    int rectSelect2Y = bindY((int) brushY);

                    int xAdd = 0;
                    int yAdd = 0;

                    if (rectSelect2X > rectSelect1.x) {
                        xAdd = 1;
                    }
                    if (rectSelect2Y > rectSelect1.y) {
                        yAdd = 1;
                    }

                    rectSelect2 = new Point(rectSelect2X + xAdd, rectSelect2Y + yAdd);

                    rectSelectStart = new Point(Math.min(rectSelect1.x, rectSelect2.x), Math.min(rectSelect1.y, rectSelect2.y));
                    rectSelectEnd = new Point(Math.max(rectSelect1.x, rectSelect2.x), Math.max(rectSelect1.y, rectSelect2.y));
                    break;

                default:
                    break;
            }
        }

    }

    public void selectionToImage() {

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        BufferedImage display = getDisplayLayer();

        for (int xPixel = 0; xPixel < xPixels; xPixel++) {
            for (int yPixel = 0; yPixel < yPixels; yPixel++) {
                int rgb = display.getRGB(xPixel, yPixel);

                if (rgb != erasedColorRGB) {
                    minX = Math.min(minX, xPixel);
                    minY = Math.min(minY, yPixel);
                    maxX = Math.max(maxX, xPixel);
                    maxY = Math.max(maxY, yPixel);
                }
            }
        }

        rectSelectStart = new Point(minX, minY);
        rectSelectEnd = new Point(maxX + 1, maxY + 1);

        project.setSelected(display.getSubimage(rectSelectStart.x, rectSelectStart.y, rectSelectEnd.x - rectSelectStart.x, rectSelectEnd.y - rectSelectStart.y));
    }

    public void fitToSelection() {
        expandCanvas(-rectSelectStart.y, rectSelectEnd.y - (yPixels), -rectSelectStart.x, rectSelectEnd.x - (xPixels));
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


    public Point getRectSelect1() {
        return rectSelect1;
    }

    public void setRectSelect1(Point rectSelect1) {
        this.rectSelect1 = rectSelect1;
    }

    public Point getRectSelect2() {
        return rectSelect2;
    }

    public void setRectSelect2(Point rectSelect2) {
        this.rectSelect2 = rectSelect2;
    }

    public void eraseAt(float atX, float atY, BufferedImage layer, float brushSize) {
        drawAt(atX, atY, layer, brushSize, erasedColor);
    }

    public void eraseLineFrom(float x1, float y1, float x2, float y2, BufferedImage layer, float brushSize) {
        drawLineFrom(x1, y1, x2, y2, layer, brushSize, erasedColor);
    }

    public void drawLineFrom(float x1, float y1, float x2, float y2, BufferedImage layer, float brushSize, Color brushColor) {

        float distanceBetween = getDistance(x1, y1, x2, y2) - brushSize;
        float dotDivider = 1; // line bellow fixes single line breaks...
        if (brushSize <= 3) {
            dotDivider = 0.1F; // PLEASE FIX pls optimise - super janky
        }

        int dotCount = (int) (distanceBetween / dotDivider);
        float angle = (float) (-Math.atan2(x1 - x2, y1 - y2) + Math.PI / 2 * 3);

        for (int i = 1; i < dotCount + 1; i++) {
            float lineX = x1 + (float) Math.cos(angle) * (dotDivider * i);
            float lineY = y1 + (float) Math.sin(angle) * (dotDivider * i);
            drawAt(lineX, lineY, layer, brushSize, brushColor);
        }


    }

    public float findAngle(float x1, float y1, float x2, float y2) {
        float angle = (float) -Math.toDegrees(Math.atan2(x2 - x1, y2 - y1)) + 90F;
        return angle;
    }

    public void drawAt(float atX, float atY, BufferedImage layer, float brushSize, Color brushColor) {
        // POORLY OPTIMISED!!! laggy, my man, please fix this garbo
        if (brushSize < 2) { // OH LORD REFACTOR PLEEAAASE PLEASE pls
            atX = (int) atX;
            atY = (int) atY;
        }

        int minX = Math.max(0, (int) (atX - brushSize) - 1);
        int maxX = Math.min(layer.getWidth(), (int) (atX + brushSize) + 1);

        int minY = Math.max(0, (int) (atY - brushSize) - 1);
        int maxY = Math.min(layer.getHeight(), (int) (atY + brushSize) + 1);

        for (int row = minX; row < maxX; row++) {
            for (int col = minY; col < maxY; col++) {
                if (getDistance(atX, atY, row, col) <= brushSize) {
                    layers.get(selectedLayer).getImage().setRGB(row, col, brushColor.getRGB());
                }
            }
        }
        updateFrom((int) (atX - brushSize - 1), (int) (atY - brushSize - 1), (int) (atX + brushSize + 1), (int) (atY + brushSize + 1));
    }

    public float getDistance(float fromX, float fromY, float toX, float toY) {

        return (float) Math.sqrt((Math.pow((toX - fromX), 2) + Math.pow((toY - fromY), 2)));

    }

    public void tick() {
        // useless so far
    }


    public void draw(Graphics g) {

        displayX += (x - displayX) / displayDivider;
        displayY += (y - displayY) / displayDivider;
        displayWidth += (width - displayWidth) / displayDivider;
        displayHeight += (height - displayHeight) / displayDivider;

        // draw image //idk what null is for

        int cornerX = (int) (displayX - displayWidth / 2);
        int cornerY = (int) (displayY - displayHeight / 2);

        g.setColor(backgroundColor);
        g.fillRect(cornerX, cornerY, (int) displayWidth, (int) displayHeight);


		/*for (int i = 0; i < layers.size(); i++) {
            g.drawImage(layers.get(i), cornerX,cornerY,(int)displayWidth,(int)displayHeight,null);
        }*/
        g.drawImage(displayLayer, cornerX, cornerY, (int) displayWidth, (int) displayHeight, null);

        if (project.isSelecting() || project.hasSelected()) {
            drawSelectDarkness(g);
        }

        if (this == project.getLastSelectedCanvas()) {
            g.setColor(selectedOut);
            g.drawRect(cornerX, cornerY, (int) displayWidth, (int) displayHeight);
        }

    }

    public void drawSelectDarkness(Graphics g) {
        g.setColor(new Color(125, 125, 255, 100));

        int cornerX = (int) (displayX - displayWidth / 2);
        int cornerY = (int) (displayY - displayHeight / 2);

        int startX = (int) (displayWidth * ((float) rectSelectStart.x / xPixels));
        int endX = (int) (displayWidth * ((float) rectSelectEnd.x / xPixels));
        int startY = (int) (displayHeight * ((float) rectSelectStart.y / yPixels));
        int endY = (int) (displayHeight * ((float) rectSelectEnd.y / yPixels));

        g.fillRect(cornerX, cornerY, (int) displayWidth, startY);
        g.fillRect(cornerX, cornerY + startY, startX, (int) displayHeight - (startY + (int) displayHeight - endY));
        g.fillRect(cornerX + endX, cornerY + startY, (int) displayWidth - endX, (int) displayHeight - (startY + (int) displayHeight - endY));
        g.fillRect(cornerX, cornerY + endY, (int) displayWidth, (int) displayHeight - endY);
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

    public int getProgramWidth() {
        return programWidth;
    }

    public void setProgramWidth(int programWidth) {
        this.programWidth = programWidth;
    }

    public int getProgramHeight() {
        return programHeight;
    }

    public void setProgramHeight(int programHeight) {
        this.programHeight = programHeight;
    }

    public int getCanvasDivider() {
        return canvasDivider;
    }

    public void setCanvasDivider(int canvasDivider) {
        this.canvasDivider = canvasDivider;
    }

    public int getxPixels() {
        return xPixels;
    }

    public void setxPixels(int xPixels) {
        this.xPixels = xPixels;
    }

    public int getyPixels() {
        return yPixels;
    }

    public void setyPixels(int yPixels) {
        this.yPixels = yPixels;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    public void setLayer(Layer layer, int index) {
        this.layers.add(index, layer);
        this.layers.remove(index + 1);
    }

    public float getBrushX() {
        return brushX;
    }

    public void setBrushX(float brushX) {
        this.brushX = brushX;
    }

    public float getBrushY() {
        return brushY;
    }

    public void setBrushY(float brushY) {
        this.brushY = brushY;
    }

    public float getBrushCanvasX() {
        return brushCanvasX;
    }

    public void setBrushCanvasX(float brushCanvasX) {
        this.brushCanvasX = brushCanvasX;
    }

    public float getBrushCanvasY() {
        return brushCanvasY;
    }

    public void setBrushCanvasY(float brushCanvasY) {
        this.brushCanvasY = brushCanvasY;
    }

    public void setSelectedLayer(int selectedLayer) {
        this.selectedLayer = selectedLayer;
    }

    public float getLastX() {
        return lastX;
    }

    public void setLastX(float lastX) {
        this.lastX = lastX;
    }

    public float getLastY() {
        return lastY;
    }

    public void setLastY(float lastY) {
        this.lastY = lastY;
    }

    public Vector getInitialPos() {
        return initialPos;
    }

    public Vector getInitialDimen() {
        return initialDimen;
    }
}