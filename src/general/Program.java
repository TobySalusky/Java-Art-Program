package general;

import file_menu.FileMenu;
import misc.AsciiGenerator;
import util.Gizmo;
import util.Vector;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

@SuppressWarnings("serial")
public class Program extends JPanel {

    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    static final int WIDTH = screenSize.width;
    static final int HEIGHT = screenSize.height;
    //static final int WIDTH = 1920;
    //static final int HEIGHT = 1080; //-48 for decorated

    private Program thisProgram = this;

    private static JFrame frame;
    private BufferedImage image;
    private Timer timer;
    private Graphics g;

    static int canvasDivider = 10; // normally 10

    static final float initialCanvasX = (float) WIDTH / 2;
    static final float initialCanvasY = (float) HEIGHT / 2;
    static final float initialCanvasWidth = WIDTH - 200;
    static final float initialCanvasHeight = HEIGHT - 200;

    private final ColorWheel colorWheel = new ColorWheel(this, 60, 120, 100, 100);

    private static ArrayList<Slider> sliders = new ArrayList<>();

    private ArrayList<Project> projects = new ArrayList<>();
    private Project selectedProject;


    private FileGui fileGui;
    private NewFileGui newFileGui;

    private final Color overlayColor = new Color(30, 30, 30);

    static boolean initialClick = false;
    static boolean drawing = false, middleDrag = false, middleDragSize = false;
    private float mouseX = 0;
    private float mouseY = 0;

    private Vector lastClick;
    private Vector lastMouse;

    private static LayerButton draggedLayerButton;

    float stabilizer = 2;
    float brushSize = 1;

    public enum brushes {
        brush, eraser, fill, colorGrab, rectSelect;
    }

    static brushes selectedBrush = brushes.brush;

    public enum modes {
        program, fileMenu;
    }

    private modes mode = modes.program;

    static Color brushColor = Color.BLACK;
    int wheelRed = 0;
    int wheelGreen = 0;
    int wheelBlue = 0;

    private static String filePath;
    private static String originalFilePath;

    private FileMenu fileMenu;
    private Slider tabSizeSlider;

    private long lastAutoSave = 0;


    private Palette palette;
    private boolean drawPalette = false;


    // FPS counting
    // credit to Java 2D Pixel Game Tutorial - Episode 5 - The FPS Counter (by Sam Parker on Youtube)
    private static long lastFpsCheck = 0;
    private static int currentFps = 0;
    private static int totalFrames = 0;
    private static int nanosPerSec = 1000000000;

    //TODO: add associated filetype https://www.rgagnon.com/javadetails/java-0592.html
    // execute from program with https://alvinalexander.com/java/java-exec-processbuilder-process-2 and https://alvinalexander.com/java/java-exec-system-command-pipeline-pipe


    private static final Vector zoomPoint = new Vector(WIDTH / 2F, HEIGHT / 2F);

    private static final int screenNum = 2;


    public Program() {

        //buffered image
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = image.getGraphics();

        GraphicsUtilities.drawBackground(g, Color.WHITE, WIDTH, HEIGHT);

        setupFilePath();

        // instantiate your objects
        start();

        //timer
        timer = new Timer(0, new TimerListener());
        timer.start();
        addMouseListener(new Mouse());
        addMouseMotionListener(new MouseMotion());
        addMouseWheelListener(new MouseWheel());
        addKeyListener(new Keyboard());
        setFocusable(true);
    }

    public void setupFilePath() {
        try {
            checkForExportPath();
        } catch (Exception e) {
            System.out.println("exception getting file path!?");
            e.printStackTrace();
        }
        originalFilePath = filePath;

        fileMenu = new FileMenu(this, filePath, new Rectangle(100, 100, WIDTH - 200, HEIGHT - 200));
    }

    public static void setFilePath(String filePath) {
        Program.filePath = filePath;
    }

    public boolean isDrawPalette() {
        return drawPalette;
    }

    public Palette getPalette() {
        return palette;
    }

    public Color getOverlayColor() {
        return overlayColor;
    }

    public FileMenu getFileMenu() {
        return fileMenu;
    }

    public void setDraggedLayerButton(LayerButton button) {
        draggedLayerButton = button;
    }

    public static void setSliders() {
        for (Slider slider : sliders) {
            slider.startSlide();
        }
    }

    public void setToProgramMode() {

        this.mode = modes.program;

    }

    public void deleteUndos() {
        selectedProject.getMainCanvas().deleteUndos();
    }

    public void readBufferedImage(BufferedImage image) {

        createProject();

        Canvas canvas = getCanvas();

        deleteUndos();

        Layer layer = canvas.getLayers().get(0);

        layer.setImage(image);
        canvas.setxPixels(layer.getImage().getWidth());
        canvas.setyPixels(layer.getImage().getHeight());
        canvas.findSize();
        canvas.setLayer(layer, 0);

        canvas.updateDisplay();
        canvas.resetPosition();
    }

    public void readPNG(File imageFile) {

        createProject();

        Canvas canvas = getCanvas();

        deleteUndos();

        Layer layer = canvas.getLayers().get(0);

        try {
            layer.setImage(ImageIO.read(imageFile));
            canvas.setxPixels(layer.getImage().getWidth());
            canvas.setyPixels(layer.getImage().getHeight());
            canvas.findSize();
            canvas.setLayer(layer, 0);
            System.out.println(imageFile.getName() + " read");
        } catch (IOException e) {
            e.printStackTrace();
        }

        canvas.updateDisplay();
        canvas.resetPosition();
    }

    public void readPNG(String imageName) {

        createProject();

        Canvas canvas = getCanvas();

        deleteUndos();

        File imageFile = new File(filePath + imageName + ".png");
        Layer layer = canvas.getLayers().get(0);

        //BufferedImage newImage = new BufferedImage(imageFile.getWidth(), imageFile.getHeight(), BufferedImage.TYPE_INT_ARGB);
        try {
            layer.setImage(ImageIO.read(imageFile));
            canvas.setxPixels(layer.getImage().getWidth());
            canvas.setyPixels(layer.getImage().getHeight());
            canvas.findSize();
            canvas.setLayer(layer, 0);
            System.out.println(imageName + ".png read");
        } catch (IOException e) {
            e.printStackTrace();
        }

        canvas.updateDisplay();
        canvas.resetPosition();
    }

    public void createPNG(String imageName) {
        createPNG(filePath, imageName);
    }

    public void createPNG(String path, String imageName) {
        BufferedImage layer = getCanvas().singleLayer();

        try {
            Formatter f = new Formatter(path + imageName + ".png");
            //f.format("%s", layer.getWidth()+" ");
            f.close();
            System.out.println("\n" + imageName + ".png created");
        } catch (Exception e) {
            System.out.println("\nError in 'createPNG'");
        }

        File imageFile = new File(path + imageName + ".png");
        try {
            ImageIO.write(layer, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Project> getProjects() {
        return projects;
    }

    public void setProjects(ArrayList<Project> projects) {
        this.projects = projects;
    }


    public void setSelectedProject(Project selectedProject) {
        this.selectedProject = selectedProject;
    }

    public void changeBrushColor(Color newColor) {

        brushColor = newColor;


        if (drawPalette) {
            if (palette.getTabClicked() != null) {
                palette.getTabClicked().updateColor(newColor.getRGB()); // sus
                palette.setReselect(newColor.getRGB());
            }
        }
    }

    public void readFile(String imageName) {

        createProject();

        Canvas canvas = getCanvas();

        deleteUndos();

        List<Layer> layers = canvas.getLayers();

        // remove all layers
        int removeCount = layers.size();
        for (int i = 0; i < removeCount; i++) {
            layers.remove(0);
        }

        try {
            File savedImage = new File(filePath + imageName + ".art");
            Scanner sc = new Scanner(savedImage);

            //int layerIndex = 0;
            //int iteration = 1;

            int layerCount = Integer.parseInt(sc.next());
            int width = Integer.parseInt(sc.next());
            int height = Integer.parseInt(sc.next());

            int pixelColor = 0;
            int pixelCount = 0;

            canvas.setxPixels(width);
            canvas.setyPixels(height);
            canvas.findSize();

            //while(sc.hasNext()) {
            for (int i = 0; i < layerCount; i++) {

                BufferedImage layerImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                int layerVisible = Integer.parseInt(sc.next().substring(1));

                for (int row = 0; row < width; row++) {
                    for (int col = 0; col < height; col++) {
                        if (pixelCount <= 0) {
                            pixelColor = Integer.parseInt(sc.next());
                            pixelCount = Integer.parseInt(sc.next().substring(1));
                        }
                        layerImage.setRGB(row, col, pixelColor);
                        pixelCount--;
                    }
                }

                Layer layer = new Layer(layerImage, i, this); //LAYCHECK

                if (layerVisible == 1) {
                    layer.setVisible(true);
                } else {
                    layer.setVisible(false);
                }

                layers.add(layer);
                //addLayerButton();
            }


            sc.close();
            canvas.updateDisplay();
            canvas.resetPosition();

        } catch (FileNotFoundException e) {
            System.out.println("no " + imageName + " found...");
        }
    }

    public void readFile(File file) {

        createProject();

        Canvas canvas = getCanvas();

        deleteUndos();

        List<Layer> layers = canvas.getLayers();

        // remove all layers
        int removeCount = layers.size();
        for (int i = 0; i < removeCount; i++) {
            layers.remove(0);
        }

        try {
            Scanner sc = new Scanner(file);

            //int layerIndex = 0;
            //int iteration = 1;

            int layerCount = Integer.parseInt(sc.next());
            int width = Integer.parseInt(sc.next());
            int height = Integer.parseInt(sc.next());

            int pixelColor = 0;
            int pixelCount = 0;

            canvas.setxPixels(width);
            canvas.setyPixels(height);
            canvas.findSize();

            //while(sc.hasNext()) {
            for (int i = 0; i < layerCount; i++) {

                BufferedImage layerImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                int layerVisible = Integer.parseInt(sc.next().substring(1));

                for (int row = 0; row < width; row++) {
                    for (int col = 0; col < height; col++) {
                        if (pixelCount <= 0) {
                            pixelColor = Integer.parseInt(sc.next());
                            pixelCount = Integer.parseInt(sc.next().substring(1));
                        }
                        layerImage.setRGB(row, col, pixelColor);
                        pixelCount--;
                    }
                }

                Layer layer = new Layer(layerImage, i, this); //LAYCHECK

                if (layerVisible == 1) {
                    layer.setVisible(true);
                } else {
                    layer.setVisible(false);
                }

                layers.add(layer);
                //addLayerButton();
            }


            sc.close();
            canvas.updateDisplay();
            canvas.resetPosition();

        } catch (FileNotFoundException e) {
            System.out.println("no " + file.getName() + " found...");
        }
    }

    public void writeFile(String imageName) {
        writeFile(filePath, imageName);
    }

    public void writeFile(String savePath, String imageName) {

        Canvas canvas = getCanvas();

        BufferedImage layer = canvas.singleLayer();

        int lastColor = -1; // please fix, idk what you're doing... why -1, my man?
        int samePixels = 1;
        int iteration = 0;

        try {
            List<Layer> layers = canvas.getLayers();
            int layerCount = layers.size();

            Formatter f = new Formatter(savePath + imageName + ".art");
            f.format("%s", layerCount + " ");
            f.format("%s", layer.getWidth() + " ");
            f.format("%s", layer.getHeight() + " ");

            for (int i = 0; i < layerCount; i++) {

                layer = layers.get(i).getImage();

                if (canvas.getLayers().get(i).isVisible()) {
                    f.format("%s", "v1" + " ");
                } else {
                    f.format("%s", "v0" + " ");
                }

                lastColor = -1;
                samePixels = 1;
                iteration = 0;

                for (int row = 0; row < layer.getWidth(); row++) {
                    for (int col = 0; col < layer.getHeight(); col++) {
                        iteration++;
                        int pixelColor = layer.getRGB(row, col);

                        if (lastColor != pixelColor) {
                            if (iteration != 1) {
                                f.format("%s", "e" + samePixels + " ");
                                samePixels = 1;
                            }
                            f.format("%s", pixelColor + " ");
                            lastColor = pixelColor;
                        } else {
                            samePixels++;
                        }

                    }
                }
                f.format("%s", "e" + samePixels + " ");
            }

            f.close();
            System.out.println("\n" + imageName + " written");
        } catch (Exception e) {
            System.out.println("\nError in writing file " + imageName);
        }
    }

    public void mixColor() {
        colorWheel.setColor(new Color(wheelRed, wheelGreen, wheelBlue));
        colorWheel.createWheel();
    }

    public int getInk(int[][] canvasUse) { // remove, please fix
        int inkCount = 0;
        for (int row = 0; row < canvasUse.length; row++) {

            for (int col = 0; col < canvasUse[row].length; col++) {

                if (canvasUse[row][col] == 1) {
                    inkCount++;
                }
            }
        }
        return inkCount;
    }

    public float getDistance(float fromX, float fromY, float toX, float toY) {

        return (float) Math.sqrt((Math.pow((toX - fromX), 2) + Math.pow((toY - fromY), 2)));

    }

    public void programTick() {

        Canvas selectedCanvas = selectedProject.getLastSelectedCanvas();

        if (drawing) {
            for (Canvas canvas : selectedProject.getCanvasList()) {
                canvas.brush(mouseX, mouseY, brushSize, stabilizer, brushColor);
            }
        }

        // draw canvas
        // currently no canvas tick
        for (Canvas canvas : selectedProject.getCanvasList()) {
            canvas.draw(g);
        }

        if (middleDragSize) {
            g.setColor(Gizmo.darkGrey);
            Vector mouse = new Vector(mouseX, mouseY);
            Gizmo.dottedLine(g, zoomPoint, mouse);
            Gizmo.dot(g, zoomPoint, Gizmo.midOrange);
            Gizmo.dot(g, mouse, Gizmo.midOrange);
        }

        // draw overlay
        g.setColor(overlayColor);
        g.fillRect(WIDTH - 120, 0, 120, HEIGHT);
        g.fillRect(0, 0, WIDTH, 60);
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(0, 60, WIDTH - 120, 60);
        g.drawLine(WIDTH - 120, 60, WIDTH - 120, HEIGHT);

        // draw current brushColor
        g.setColor(brushColor);
        g.fillRect(750, 20, 20, 20);

        // draw sliders
        for (int i = 0; i < sliders.size(); i++) {
            sliders.get(i).draw(g);
            sliders.get(i).tick(mouseX);
        }

        // draw layers
        for (Layer layer : selectedCanvas.getLayers()) { // LAYCHECK

            layer.getButton().draw(g);
            if (layer.getButton() != draggedLayerButton) {
                layer.getButton().tick(); // REMOVE THIS LAY CHECK PLSS ************** only do when needed LAG
            }
        }

        selectedCanvas.getSelectedLayer().getButton().draw(g); // LAG, this is duping up, make not draw first time please

        colorWheel.tick(mouseX, mouseY);
        colorWheel.draw(g);

        if (drawPalette) {
            palette.draw(g);
        }

		/* draw pointer //removed
        g.setColor(Color.BLACK); // remove magic number please
        g.fillOval((int)(mouseX-20/2),(int)(mouseY-20/2),(int)20,(int)20);*/

        // draw fps counter
        g.setColor(Color.WHITE);
        g.setFont(new Font("Impact", Font.BOLD, 18));
        g.drawString("FPS: " + currentFps, 10, 30);

        // draw selected layer readout
        g.drawString("layer: " + (selectedCanvas.getSelectedLayerIndex() + 1) + "/" + selectedCanvas.getLayerCount(), 800, 30);

        g.drawString("undo count: " + selectedProject.getLastSelectedCanvas().getUndoCount(), 1300, 30);
    }

    public void tick() {

        switch (mode) {

            case program:

                programTick();

                break;

            case fileMenu:

                fileMenuTick();

                break;

        }

    }

    public void fileMenuTick() {

        tabSizeSlider.draw(g); // OH LORD THIS SLIDER STILL EXISTS IN NORMAL MODE, PLEASE SEPERATE THEM SIR
        tabSizeSlider.tick(mouseX);

        //fileMenu.check();
        fileMenu.checkHovers(mouseX, mouseY);
        fileMenu.draw(g);

    }

    public void createProject() {
        selectedProject = new Project(thisProgram, "Untitled " + (int) (Math.random() * 10000), initialCanvasX, initialCanvasY, initialCanvasWidth, initialCanvasHeight);
        projects.add(selectedProject);

    }

    public void createProject(int xPixel, int yPixel) {
        selectedProject = new Project(thisProgram, "Untitled " + (int) (Math.random() * 10000), xPixel, yPixel, initialCanvasX, initialCanvasY, initialCanvasWidth, initialCanvasHeight);
        projects.add(selectedProject);

//        Canvas canvas = getCanvas();
//        // initially fill 2D array
//        canvas.resetCanvas();
//        canvas.findSize();
//        canvas.resetPosition();
    }

    public void updateEvents() {

        palette.setUpdate(true);

    }

    public void start() {

        createProject();

        palette = new Palette(this);

        // fill sliders
        Slider brushSlider = new Slider(200, 30, 100, 20, Slider.Type.brushSize, this);
        Slider redSlider = new Slider(350, 30, 100, 20, Slider.Type.brushRed, this);
        Slider greenSlider = new Slider(500, 30, 100, 20, Slider.Type.brushGreen, this);
        Slider blueSlider = new Slider(650, 30, 100, 20, Slider.Type.brushBlue, this);
        Slider stabilizerSlider = new Slider(950, 30, 100, 20, Slider.Type.stabilizer, this);
        Slider opacitySlider = new Slider(1100, 30, 100, 20, Slider.Type.opacity, this);
        tabSizeSlider = new Slider(50, 50, 100, 20, Slider.Type.fileTabSize, this);

        sliders.add(brushSlider);
        sliders.add(redSlider);
        sliders.add(greenSlider);
        sliders.add(blueSlider);
        sliders.add(stabilizerSlider);
        sliders.add(opacitySlider);

        sliders.add(tabSizeSlider);

        fullscreen();

        //change ui look to that of the platform (for JFileChooser)
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("EXCEPTION IN START() when changing UI look and feels");
        }
    }

    public Project getSelectedProject() {
        return selectedProject;
    }

    public void copyFromClipboard(BufferedImage image) {

        if (image != null) {
            readBufferedImage(image);
        }

    }

    private class Keyboard implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            // key controls

            Canvas mainCanvas = getCanvas();
            Canvas selectedCanvas = selectedProject.getLastSelectedCanvas();

            boolean control = e.isControlDown();

            switch (e.getKeyCode()) {
                case (KeyEvent.VK_Z):

                    if (control) {
                        System.out.println("control-z pressed");

                        if (!drawing) {
                            selectedCanvas.undo();
                            selectedCanvas.updateDisplay();
                        }
                    }

                    break;
                case (KeyEvent.VK_X):

                    System.out.println("x pressed");
                    selectedCanvas.resetCanvas();
                    selectedCanvas.updateDisplay();

                    break;

                case (KeyEvent.VK_C):

                    if (control && selectedProject.hasSelected()) {
                        ClipBoardUser clipBoard = new ClipBoardUser();

                        clipBoard.copyToClipboard(selectedProject.getSelected());
                    }
                    break;

                case (KeyEvent.VK_DELETE):

                    System.out.println("delete pressed");

                    List<Layer> layers = mainCanvas.getLayers();

                    if (layers.size() > 1) {
                        int deleteIndex = mainCanvas.getSelectedLayerIndex();

                        layers.remove(mainCanvas.getSelectedLayerIndex());

                        for (int i = deleteIndex; i < layers.size(); i++) { // starts at deleteIndex because down-shift
                            Layer layer = layers.get(i);
                            layer.setIndex(layer.getIndex() - 1);
                            layer.getButton().findToY();
                        }

                        mainCanvas.updateDisplay();
                    }

                    break;

                case (KeyEvent.VK_O):

                    System.out.println("o pressed");

                    fileGui = new FileGui(thisProgram, "read");
                    fileGui.display();

                    break;

                case (KeyEvent.VK_M):

                    System.out.println("m pressed");

                    if (mode == modes.program) {
                        mode = modes.fileMenu;

                        if (!fileMenu.isFolderRead()) {
                            fileMenu.setUp();
                        }

                    } else {
                        mode = modes.program;
                    }

                    break;

                case (KeyEvent.VK_E):
                    System.out.println("e pressed");

                    if (control) {
                        if (getSelectedProject().hasSelected()) {
                            mainCanvas.fitToSelection();
                            getSelectedProject().setSelected(null);
                        } else {
                            mainCanvas.expandCanvas(-1, -1, -1, -1);
                        }
                    } else {
                        mainCanvas.expandCanvas(1, 1, 1, 1);
                    }
                    break;

                case (KeyEvent.VK_ESCAPE):
                    System.out.println("escape pressed");
                    System.exit(0);
                    break;
                case (KeyEvent.VK_R):
                    System.out.println("r pressed");

                    if (control) {
                        for (Canvas canvas : selectedProject.getCanvasList()) {
                            canvas.setInitPosDimen();
                        }
                    } else {
                        for (Canvas canvas : selectedProject.getCanvasList()) {
                            canvas.resetPosition();
                        }
                    }
                    break;
                case (KeyEvent.VK_W):
                    System.out.println("w pressed");
                    selectedBrush = brushes.colorGrab;
                    break;
                case (KeyEvent.VK_A):
                    if (e.isShiftDown()) {
                        selectedProject.addBasicCanvas(32, 32);
                    } else {
                        System.out.println("a pressed");
                        selectedBrush = brushes.brush;
                    }
                    break;
                case (KeyEvent.VK_Q):

                    System.out.println("q pressed");

                    if (control) {
                        mainCanvas.selectionToImage();
                    } else {
                        selectedBrush = brushes.rectSelect;
                    }
                    break;
                case (KeyEvent.VK_S):

                    if (control) {
                        System.out.println("control-s pressed");

                        if (selectedProject.getLastSavedByName() == null) {
                            fileGui = new FileGui(thisProgram, "write");
                            fileGui.display();
                        } else {
                            writeFile(selectedProject.getLastSavedByName());
                        }

                    } else {
                        System.out.println("s pressed");
                        selectedBrush = brushes.eraser;
                    }
                    break;
                case (KeyEvent.VK_D):
                    System.out.println("d pressed");
                    selectedBrush = brushes.fill;
                    break;
                case (KeyEvent.VK_N):

                    if (control) {

                        System.out.println("control-n pressed");

                        newFileGui = new NewFileGui(thisProgram);
                        newFileGui.display();

                    } else {

                        System.out.println("n pressed");
                        addUndo();
                        mainCanvas.addLayer(mainCanvas.getSelectedLayerIndex() + 1);
                        mainCanvas.setSelectedLayerIndex(mainCanvas.getSelectedLayerIndex() + 1);

                    }
                    break;
                case (KeyEvent.VK_V):

                    if (control) {
                        System.out.println("control v pressed");

                        ClipBoardUser clipBoard = new ClipBoardUser();

                        copyFromClipboard(clipBoard.getCopied());
                    } else {

                        System.out.println("v pressed");
                        addUndo();
                        selectedProject.copySelected(mainCanvas.getSelectedLayerIndex() + 1);
                        mainCanvas.setSelectedLayerIndex(mainCanvas.getSelectedLayerIndex() + 1);
                    }
                    break;

                case (KeyEvent.VK_P):
                    System.out.println("p pressed");
                    drawPalette = !drawPalette;

                    if (!drawPalette) {
                        palette.close();
                    }

                    break;

                case (KeyEvent.VK_DOWN):
                    System.out.println("down key pressed");
                    mainCanvas.selectedLayer(mainCanvas.getSelectedLayerIndex() - 1);
                    break;
                case (KeyEvent.VK_UP):
                    System.out.println("up key pressed");
                    mainCanvas.selectedLayer(mainCanvas.getSelectedLayerIndex() + 1);
                    break;

                case (KeyEvent.VK_LEFT):
                    System.out.println("right key pressed");
                    int oldProjectIndex = projects.indexOf(selectedProject);
                    if (oldProjectIndex > 0) {
                        selectedProject = projects.get(oldProjectIndex - 1);
                    } else {
                        selectedProject = projects.get(projects.size() - 1);
                    }
                    break;

                case (KeyEvent.VK_RIGHT):
                    System.out.println("right key pressed");
                    int projectIndex = projects.indexOf(selectedProject);
                    if (projectIndex < projects.size() - 1) {
                        selectedProject = projects.get(projectIndex + 1);
                    } else {
                        selectedProject = projects.get(0);
                    }
                    break;

                case (KeyEvent.VK_U):

                    String ascii = AsciiGenerator.toAscii(getCanvas().singleLayer());
                    ClipBoardUser.copyToClipboard(ascii);
                    System.out.println(ascii);

                    break;
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void keyTyped(KeyEvent e) {
            // TODO Auto-generated method stub

        }


    }

    private void zoomOn(Vector point, float amount) {
        float wheelMult = 0.1F;
        float moveDivide = 1 / wheelMult;

        for (Canvas canvas : selectedProject.getCanvasList()) {

            canvas.setWidth(canvas.getWidth() * (1 + amount * wheelMult));
            canvas.setHeight(canvas.getHeight() * (1 + amount * wheelMult));

            canvas.setX(canvas.getX() - ((point.x - canvas.getX()) / moveDivide) * amount);
            canvas.setY(canvas.getY() - ((point.y - canvas.getY()) / moveDivide) * amount);
        }
    }

    private class MouseWheel implements MouseWheelListener {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

            switch (mode) {

                case program:

                    zoomOn(new Vector(mouseX, mouseY), e.getWheelRotation());
                    break;

                case fileMenu:

                    float scrollMult = 150;

                    fileMenu.addScroll(e.getWheelRotation() * scrollMult);

                    break;
            }
        }

    }

    public Canvas getCanvas() {
        return selectedProject.getMainCanvas();
    }

    private class MouseMotion implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();

            Canvas canvas = getCanvas();

            selectedProject.selectDrag();

            if (draggedLayerButton != null) {
                draggedLayerButton.drag(mouseY);

                float check = Program.HEIGHT - 35 + 55 / 2;

                int lastIndex = draggedLayerButton.getLayer().getIndex();

                int index = (int) ((check - mouseY) / 55);
                index = Math.max(index, 0);
                index = Math.min(index, canvas.getLayers().size() - 1);

                if (index != lastIndex) {
                    draggedLayerButton.indexLayer(index);
                    canvas.setSelectedLayerIndex(index);

                    if (index > lastIndex) { // shift others down
                        for (int i = lastIndex + 1; i <= index; i++) {
                            Layer layer = canvas.getLayers().get(i);
                            layer.getButton().indexLayer(layer.getIndex() - 1);
                            ;
                        }
                    }

                    if (index < lastIndex) { // shift others up
                        for (int i = lastIndex - 1; i >= index; i--) {
                            Layer layer = canvas.getLayers().get(i);
                            layer.getButton().indexLayer(layer.getIndex() + 1);
                        }
                    }

                    // OH LORD LAGGG, please optimise pls
                    canvas.resortLayers();
                    canvas.updateDisplay();
                }
            }

            Vector thisMouse = new Vector(mouseX, mouseY);
            Vector delta = thisMouse.subbed(lastMouse);
            lastMouse = thisMouse;

            if (middleDrag) {
                middleDrag(delta);
            }

            if (middleDragSize) {
                middleDragSize(delta);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();

            for (Canvas canvas : selectedProject.getCanvasList()) {
                canvas.setBrushX(canvas.toBrushCanvasX(mouseX));
                canvas.setBrushY(canvas.toBrushCanvasY(mouseY));
            }
        }

    }

    private void middleDragSize(Vector delta) {

        Vector diff = new Vector(mouseX, mouseY).subbed(zoomPoint);
        Vector lastDiff = diff.subbed(delta);

        float sensitivity = 0.05F;

        zoomOn(zoomPoint, sensitivity * (diff.mag() - lastDiff.mag()));
    }

    private void middleDrag(Vector delta) {

        float sensitivity = 1F;

        for (Canvas canvas : selectedProject.getCanvasList()) {
            canvas.setX(canvas.getX() + delta.x * sensitivity);
            canvas.setY(canvas.getY() + delta.y * sensitivity);
        }
    }

    private class Mouse implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

            mouseX = e.getX();
            mouseY = e.getY();

            boolean leftClick = e.getButton() == MouseEvent.BUTTON1;
            boolean middleClick = e.getButton() == MouseEvent.BUTTON2;
            boolean rightClick = e.getButton() == MouseEvent.BUTTON3;

            switch (mode) {

                case program:

                    for (Canvas canvas : selectedProject.getCanvasList()) {

                        canvas.setBrushX(canvas.toBrushCanvasX(mouseX));
                        canvas.setBrushY(canvas.toBrushCanvasY(mouseY));

                        canvas.setLastX(canvas.toBrushCanvasX(mouseX));
                        canvas.setLastY(canvas.toBrushCanvasY(mouseY));

                    }

                    //int[][] canvasCopy = new int[canvas.getCanvas().length][canvas.getCanvas()[0].length];

                    //ArrayList<BufferedImage> layersCopy = new ArrayList<BufferedImage>();
                    //layersCopy = canvas.getLayers();

                    if (middleClick) {
                        if (e.isShiftDown()) {
                            middleDragSize = true;
                        } else {
                            middleDrag = true;
                        }
                    }

                    // magic nums are from overlay size
                    if (mouseX <= WIDTH - 120 && mouseY >= 60) {

                        if (leftClick) {
                            addUndo();

                            drawing = true;
                            initialClick = true;

                            colorWheel.checkClick(mouseX, mouseY); // ADDS UNDO please pls fix
                        }

                    } else {

                        for (Slider slider : sliders) {
                            slider.checkClick(mouseX, mouseY);
                        }

                        for (Layer layer : selectedProject.getLastSelectedCanvas().getLayers()) {
                            layer.getButton().checkClick(mouseX, mouseY);
                        }

                    }

                    if (drawPalette) {
                        palette.checkClicks(mouseX, mouseY);
                    }
                    break;

                case fileMenu:

                    tabSizeSlider.checkClick(mouseX, mouseY);

                    fileMenu.checkClicks(mouseX, mouseY, rightClick);

                    break;

            }

            lastMouse = new Vector(mouseX, mouseY);
            lastClick = lastMouse.copy();
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            selectedProject.selectRelease();

            middleDrag = false;
            middleDragSize = false;
            drawing = false;
            initialClick = false;

            colorWheel.removeClick();

            for (Slider slider : sliders) {
                slider.removeClick();
            }

            for (Layer layer : getCanvas().getLayers()) {
                layer.getButton().setClicked(false);
                layer.getButton().setVisibleClicked(false);
            }
            draggedLayerButton = null;
        }
    }

    public void colorWheelChange() {
        colorWheel.changeColor();
    }

    public void addUndo() {
        selectedProject.getLastSelectedCanvas().addUndo(copyLayers(selectedProject.getLastSelectedCanvas()));
    }

    public List<Layer> copyLayers(Canvas canvas) { // hey this will probably be really laggy sooooooo...
        // bro why are you shallow copying, you're an actual nincompoop, please fix
        List<Layer> layers = canvas.getLayers();
        List<Layer> layersCopy = new ArrayList<>();

        for (int i = 0; i < layers.size(); i++) {

            Layer layer = layers.get(i);
            BufferedImage image = layer.getImage();
            BufferedImage layerCopy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

            for (int row = 0; row < image.getWidth(); row++) {
                for (int col = 0; col < image.getHeight(); col++) {// prolly someting wong here,
                    layerCopy.setRGB(row, col, image.getRGB(row, col));  // so keep your wits about you
                }
            }
            layersCopy.add(new Layer(layer.getButton().getLayerName(), layerCopy, layer.getIndex(), this)); // LAYCHECK
            layersCopy.get(layersCopy.size() - 1).setVisible(layer.isVisible());
            layersCopy.get(layersCopy.size() - 1).setOpacity(layer.getOpacity());
        }


        return layersCopy;
    }

    public Color getColorAt(BufferedImage image, int x, int y) {

        int clr = image.getRGB(x, y);
        int red = (clr & 0x00ff0000) >> 16;
        int green = (clr & 0x0000ff00) >> 8;
        int blue = clr & 0x000000ff;

        Color pixelColor = new Color(red, green, blue);

        return pixelColor;
    }

    public void autosave() {

        if (!directoryExists(originalFilePath, "autosaves")) {
            makeDirectory(originalFilePath, "autosaves");
        }

        String name = "autosave " + getSelectedProject().autoSaveName;

        writeFile(originalFilePath + "autosaves\\", name);

    }

    public boolean directoryExists(String pathInside, String folderName) {

        File file = new File(pathInside + folderName);

        return (file != null && file.isDirectory());

    }

    public boolean fileExists(String pathInside, String fileName) {

        File file = new File(pathInside + fileName);

        return (file != null);

    }

    public void makeDirectory(String pathInside, String folderName) {

        File file = new File(pathInside + folderName);

        boolean dir = file.mkdir();

        if (dir) {
            System.out.println("created directory: " + folderName);
        } else {
            System.out.println("failed to create directory: " + folderName + " in " + pathInside);
        }

    }

    public void checkForExportPath() throws FileNotFoundException {

        String docsPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\";
        System.out.println("docs path: " + docsPath);

        if (directoryExists(docsPath, "Art general.Program")) {

            if (fileExists(docsPath + "Art general.Program\\", "path.artsetting")) {
                readPathFile(docsPath + "Art general.Program\\");
            } else {
                createPathFile(docsPath + "Art general.Program\\");
            }

        } else {

            makeDirectory(docsPath, "Art general.Program");
            createPathFile(docsPath + "Art general.Program\\");
        }

        System.out.println("path: " + filePath);
    }

    public void readPathFile(String location) throws FileNotFoundException {

        File pathFile = new File(location + "path.artsettings");
        Scanner sc = new Scanner(pathFile);

        filePath = sc.nextLine();
        sc.close();

    }

    public void createPathFile(String location) throws FileNotFoundException {

        filePath = JOptionPane.showInputDialog("Enter Path For Exports: ");
        if (!filePath.substring(filePath.length() - 1).equals("\\")) {
            filePath += "\\";
        }

        Formatter f = new Formatter(location + "path.artsettings");
        f.format("%s", filePath);
        f.close();
    }

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            // draws a background color to cover up all the previous graphics
            GraphicsUtilities.drawBackground(g, Color.DARK_GRAY, WIDTH, HEIGHT);

            // frames
            totalFrames++;
            if (System.nanoTime() > lastFpsCheck + nanosPerSec) {
                lastFpsCheck = System.nanoTime();
                currentFps = totalFrames;
                totalFrames = 0;
            }

            if (selectedProject.changesSinceAutoSave && System.nanoTime() > lastAutoSave + nanosPerSec * 10) { // should save once every 10 seconds
                autosave();
                lastAutoSave = System.nanoTime();
                selectedProject.changesSinceAutoSave = false;
            }

            // tick
            tick();

            // This is the last line of actionPerformed
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }

    public static void fullscreen() {
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
    }

    public static void main(String[] args) {

        frame = new JFrame("Art Time.");
        frame.setSize(WIDTH, HEIGHT); //+17 +48
        frame.setLocation(200, 200 + 1080 * (screenNum - 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new Program());
        frame.setVisible(true);

    }

}