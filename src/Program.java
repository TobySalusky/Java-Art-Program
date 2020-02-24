import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
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
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

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

    private JFileChooser fileExplorer;
    @SuppressWarnings("unused")
    private UIManager UIManager; // not unused

    static int canvasDivider = 25; // normally 10

    static final float initialCanvasX = WIDTH / 2;
    static final float initialCanvasY = HEIGHT / 2;
    static final float initialCanvasWidth = WIDTH - 200;
    static final float initialCanvasHeight = HEIGHT - 200;

    //static Canvas canvas = new Canvas(initialCanvasX, initialCanvasY, initialCanvasWidth, initialCanvasHeight);

    private static ColorWheel colorWheel = new ColorWheel(60, 120, 100, 100);

    private Slider brushSlider;
    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;
    private Slider stabilizerSlider;
    private Slider opacitySlider;
    private static ArrayList<Slider> sliders = new ArrayList<Slider>();

    //private static ArrayList<ArrayList<Layer>> undo = new ArrayList<ArrayList<Layer>>();


    private ArrayList<Project> projects = new ArrayList<Project>();
    private Project selectedProject;


    private FileGui fileGui;

    private Color overlayColor = new Color(30, 30, 30);

    static boolean initialClick = false;
    static boolean drawing = false;
    private float mouseX = 0;
    private float mouseY = 0;

    private static LayerButton draggedLayerButton;

    float stabilizer = 2;
    float brushSize = 6;

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

    //private static String filePath = "C:\\Users\\Tobafett\\Desktop\\";
    private static String filePath = "C:\\Users\\Tobafett\\Dropbox\\file testing\\";
    //private static String filePath = "C:\\Users\\815328\\Documents\\file testing\\";

    private FileMenu fileMenu = new FileMenu(this, filePath, new Rectangle(100, 100, WIDTH - 200, HEIGHT - 200));
    private Slider tabSizeSlider;

    // FPS counting
    // credit to Java 2D Pixel Game Tutorial - Episode 5 - The FPS Counter (by Sam Parker on Youtube)
    private static long lastFpsCheck = 0;
    private static int currentFps = 0;
    private static int totalFrames = 0;

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
        ArrayList<ArrayList<Layer>> undos = getUndos();

        for (int i = undos.size() - 1; i >= 0; i--) {
            undos.remove(i);
        }
    }

    public void readBufferedImage(BufferedImage image) {

        createProject();

        Canvas canvas = getCanvas();

        deleteUndos();

        Layer layer = canvas.getLayers().get(0);

        layer.setImage(image);
        canvas.setxPixel(layer.getImage().getWidth());
        canvas.setyPixel(layer.getImage().getHeight());
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
            canvas.setxPixel(layer.getImage().getWidth());
            canvas.setyPixel(layer.getImage().getHeight());
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
            canvas.setxPixel(layer.getImage().getWidth());
            canvas.setyPixel(layer.getImage().getHeight());
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
        BufferedImage layer = getCanvas().singleLayer();

        try {
            Formatter f = new Formatter(filePath + imageName + ".png");
            //f.format("%s", layer.getWidth()+" ");
            f.close();
            System.out.println("\n" + imageName + ".png created");
        } catch (Exception e) {
            System.out.println("\nError in 'createPNG'");
        }

        File imageFile = new File(filePath + imageName + ".png");
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

    public void readFile(String imageName) {

        createProject();

        Canvas canvas = getCanvas();

        deleteUndos();

        ArrayList<Layer> layers = canvas.getLayers();

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

            canvas.setxPixel(width);
            canvas.setyPixel(height);
            canvas.findSize();

            //while(sc.hasNext()) {
            for (int i = 0; i < layerCount; i++) {
                System.out.println("hi: " + i); // test

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

        ArrayList<Layer> layers = canvas.getLayers();

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

            canvas.setxPixel(width);
            canvas.setyPixel(height);
            canvas.findSize();

            //while(sc.hasNext()) {
            for (int i = 0; i < layerCount; i++) {
                System.out.println("hi: " + i); // test

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

        Canvas canvas = getCanvas();

        BufferedImage layer = canvas.singleLayer();

        int lastColor = -1; // please fix, idk what you're doing... why -1, my man?
        int samePixels = 1;
        int iteration = 0;

        try {
            ArrayList<Layer> layers = canvas.getLayers();
            int layerCount = layers.size();

            Formatter f = new Formatter(filePath + imageName + ".art");
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
            System.out.println("\nError");
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

        Canvas canvas = getCanvas();

        if (drawing == true) {
            canvas.brush(mouseX, mouseY, brushSize, stabilizer, brushColor);
        }

        // draw canvas
        // currently no canvas tick
        canvas.draw(g);

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
        for (Layer layer : canvas.getLayers()) { // LAYCHECK

            layer.getButton().draw(g);
            if (layer.getButton() != draggedLayerButton) {
                layer.getButton().tick(); // REMOVE THIS LAY CHECK PLSS ************** only do when needed LAG
            }
        }

        canvas.getSelectedLayer().getButton().draw(g); // LAG, this is duping up, make not draw first time please

        colorWheel.tick(mouseX, mouseY);
        colorWheel.draw(g);


		/* draw pointer //removed
        g.setColor(Color.BLACK); // remove magic number please
        g.fillOval((int)(mouseX-20/2),(int)(mouseY-20/2),(int)20,(int)20);*/

        // draw fps counter
        g.setColor(Color.WHITE);
        g.setFont(new Font("Impact", Font.BOLD, 18));
        g.drawString("FPS: " + currentFps, 10, 30);

        // draw selected layer readout
        g.drawString("layer: " + (canvas.getSelectedLayerIndex() + 1) + "/" + canvas.getLayerCount(), 800, 30);
    }

    public void tick() {

        switch (mode) {

            case program:

                programTick();

                break;

            case fileMenu:

                // NO DONT USE THIS PLEASE
                mouseX = MouseInfo.getPointerInfo().getLocation().x;
                mouseY = MouseInfo.getPointerInfo().getLocation().y;

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
        selectedProject = new Project(thisProgram, initialCanvasX, initialCanvasY, initialCanvasWidth, initialCanvasHeight);
        projects.add(selectedProject);

        Canvas canvas = getCanvas();
        // initially fill 2D array
        canvas.resetCanvas();
        canvas.findSize();
        canvas.resetPosition();
    }

    public void start() {

        createProject();

        //addLayerButton();

        // fill sliders
        brushSlider = new Slider(200, 30, 100, 20, Slider.Type.brushSize, this);
        redSlider = new Slider(350, 30, 100, 20, Slider.Type.brushRed, this);
        greenSlider = new Slider(500, 30, 100, 20, Slider.Type.brushGreen, this);
        blueSlider = new Slider(650, 30, 100, 20, Slider.Type.brushBlue, this);
        stabilizerSlider = new Slider(950, 30, 100, 20, Slider.Type.stabilizer, this);
        opacitySlider = new Slider(1100, 30, 100, 20, Slider.Type.opacity, this);
        tabSizeSlider = new Slider(50, 50, 100, 20, Slider.Type.fileTabSize, this);

        sliders.add(brushSlider);
        sliders.add(redSlider);
        sliders.add(greenSlider);
        sliders.add(blueSlider);
        sliders.add(stabilizerSlider);
        sliders.add(opacitySlider);

        sliders.add(tabSizeSlider);

        fullscreen();

        UIManager = new UIManager();

        //change ui look to that of the platform (for JFileChooser)
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("EXCEPTION IN START() when changing UI look and feels");
        }
    }

    public Program() {

        //buffered image
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = image.getGraphics();

        GraphicsUtilities.drawBackground(g, Color.WHITE, WIDTH, HEIGHT);

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

    public Project getSelectedProject() {
        return selectedProject;
    }

    public void openFileExplorer() {
        fileExplorer = new JFileChooser();
        // why the Program.this???
        @SuppressWarnings("unused")
        int testVal = fileExplorer.showOpenDialog(Program.this); // please fix I'm scared
    }


    // this is a useless method you bumbling ape.

	/*public ArrayList<Layer> imageToLayers(ArrayList<BufferedImage> images) {

    	ArrayList<Layer> layers = new ArrayList<Layer>();

    	for (int i = 0; i < images.size(); i++) {
    		layers.add(new Layer(images.get(i)));
    	}

    	return layers;
    }*/

    public void undo() {

        ArrayList<ArrayList<Layer>> undos = getUndos();

        if (undos.size() >= 1) {
            getCanvas().setLayers(undos.get(0));
            undos.remove(0);
            System.out.println("undo succesfull");
        }

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

            Canvas canvas = getCanvas();

            switch (e.getKeyCode()) {
                case (KeyEvent.VK_Z):

                    if (e.isControlDown()) {
                        System.out.println("control-z pressed");

                        if (!drawing) {
                            undo();
                            canvas.updateDisplay();
                        }
                    }

                    break;
                case (KeyEvent.VK_X):

                    System.out.println("x pressed");
                    canvas.resetCanvas();
                    canvas.updateDisplay();

                    break;

                case (KeyEvent.VK_C):

                    if (e.isControlDown() && selectedProject.hasSelected()) {
                        ClipBoardUser clipBoard = new ClipBoardUser();

                        clipBoard.copyToClipboard(selectedProject.getSelected());
                    }
                    break;

                case (KeyEvent.VK_DELETE):

                    System.out.println("delete pressed");

                    ArrayList<Layer> layers = canvas.getLayers();

                    if (layers.size() > 1) {
                        int deleteIndex = canvas.getSelectedLayerIndex();

                        layers.remove(canvas.getSelectedLayerIndex());

                        for (int i = deleteIndex; i < layers.size(); i++) { // starts at deleteIndex because down-shift
                            Layer layer = layers.get(i);
                            layer.setIndex(layer.getIndex() - 1);
                            layer.getButton().findToY();
                        }

                        canvas.updateDisplay();
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

                        if (!fileMenu.folderRead) {
                            fileMenu.setUp();
                        }

                    } else {
                        mode = modes.program;
                    }

                    break;

                case (KeyEvent.VK_F):
                    System.out.println("f pressed");

                    openFileExplorer();

                    break;
                case (KeyEvent.VK_ESCAPE):
                    System.out.println("escape pressed");
                    System.exit(0);
                    break;
                case (KeyEvent.VK_R):
                    System.out.println("r pressed");
                    canvas.resetPosition();
                    break;
                case (KeyEvent.VK_W):
                    System.out.println("w pressed");
                    selectedBrush = brushes.colorGrab;
                    break;
                case (KeyEvent.VK_A):
                    System.out.println("a pressed");
                    selectedBrush = brushes.brush;
                    break;
                case (KeyEvent.VK_Q):
                    System.out.println("q pressed");
                    selectedBrush = brushes.rectSelect;
                    break;
                case (KeyEvent.VK_S):

                    if (e.isControlDown()) {
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
                    System.out.println("n pressed");
                    addUndo();
                    canvas.addLayer(canvas.getSelectedLayerIndex() + 1);
                    canvas.setSelectedLayerIndex(canvas.getSelectedLayerIndex() + 1);
                    break;
                case (KeyEvent.VK_V):

                    if (e.isControlDown()) {
                        System.out.println("control v pressed");

                        ClipBoardUser clipBoard = new ClipBoardUser();

                        copyFromClipboard(clipBoard.getCopied());
                    } else {

                        System.out.println("v pressed");
                        addUndo();
                        selectedProject.copySelected(canvas.getSelectedLayerIndex() + 1);
                        canvas.setSelectedLayerIndex(canvas.getSelectedLayerIndex() + 1);
                    }
                    break;
                case (KeyEvent.VK_DOWN):
                    System.out.println("down key pressed");
                    canvas.selectedLayer(canvas.getSelectedLayerIndex() - 1);
                    break;
                case (KeyEvent.VK_UP):
                    System.out.println("up key pressed");
                    canvas.selectedLayer(canvas.getSelectedLayerIndex() + 1);
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

    private class MouseWheel implements MouseWheelListener {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

            switch (mode) {

                case program:

                    float wheelMult = 0.1F;
                    float moveDivide = 1 / wheelMult;

                    Canvas canvas = getCanvas();

                    canvas.setWidth(canvas.getWidth() * (1 + e.getWheelRotation() * wheelMult));
                    canvas.setHeight(canvas.getHeight() * (1 + e.getWheelRotation() * wheelMult));

                    canvas.setX(canvas.getX() - ((mouseX - canvas.getX()) / moveDivide) * e.getWheelRotation());
                    canvas.setY(canvas.getY() - ((mouseY - canvas.getY()) / moveDivide) * e.getWheelRotation());
                    break;

                case fileMenu:

                    float scrollMult = 150;

                    fileMenu.scroll += e.getWheelRotation() * scrollMult;

                    break;
            }
        }

    }

    public Canvas getCanvas() {
        return selectedProject.getCanvas();
    }

    private class MouseMotion implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
            // TODO Auto-generated method stub
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
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            // TODO Auto-generated method stub
            mouseX = e.getX();
            mouseY = e.getY();

            Canvas canvas = getCanvas();

            canvas.setBrushX(canvas.toBrushCanvasX(mouseX));
            canvas.setBrushY(canvas.toBrushCanvasY(mouseY));
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

            switch (mode) {

                case program:

                    Canvas canvas = getCanvas();

                    canvas.setBrushX(canvas.toBrushCanvasX(mouseX));
                    canvas.setBrushY(canvas.toBrushCanvasY(mouseY));

                    //int[][] canvasCopy = new int[canvas.getCanvas().length][canvas.getCanvas()[0].length];

                    //ArrayList<BufferedImage> layersCopy = new ArrayList<BufferedImage>();
                    //layersCopy = canvas.getLayers();

                    // magic nums are from overlay size
                    if (mouseX <= WIDTH - 120 && mouseY >= 60) {

                        addUndo();

                        drawing = true;
                        initialClick = true;

                        colorWheel.checkClick(mouseX, mouseY); // ADDS UNDO please pls fix

                    } else {

                        for (Slider slider : sliders) {
                            slider.checkClick(mouseX, mouseY);
                        }

                        for (Layer layer : canvas.getLayers()) {
                            layer.getButton().checkClick(mouseX, mouseY);
                        }

                    }

                    break;

                case fileMenu:

                    tabSizeSlider.checkClick(mouseX, mouseY);

                    fileMenu.checkClicks(mouseX, mouseY);

                    break;

            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            selectedProject.selectRelease();

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

    public ArrayList<ArrayList<Layer>> getUndos() {
        return selectedProject.getUndos();
    }

    public void addUndo() {
        getUndos().add(0, copyLayers());
    }

    public ArrayList<Layer> copyLayers() { // hey this will probably be really laggy sooooooo...
        // bro why are you shallow copying, you're an actual nincompoop, please fix
        ArrayList<Layer> layers = getCanvas().getLayers();
        ArrayList<Layer> layersCopy = new ArrayList<Layer>();

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

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            // draws a background color to cover up all the previous graphics
            GraphicsUtilities.drawBackground(g, Color.DARK_GRAY, WIDTH, HEIGHT);

            // frames
            totalFrames++;
            if (System.nanoTime() > lastFpsCheck + 1000000000) {
                lastFpsCheck = System.nanoTime();
                currentFps = totalFrames;
                totalFrames = 0;
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
        frame.setLocation(200, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new Program());
        frame.setVisible(true);

    }

}