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
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class ImageTab extends FileTab {

    private float picScaleX, picScaleY;
    private BufferedImage image;


    public ImageTab(Program program, File file) {

        super(program, file);


        if (hasExtension("png")) {
            readPNG();
        } else if (hasExtension("art")) {
            readArtFile();
        }
        findImageDimensions();
    }

    public void findImageDimensions() {

        if (image.getWidth() > image.getHeight()) {
            picScaleX = 1;
            picScaleY = (float) image.getHeight() / image.getWidth();
        } else {
            picScaleX = (float) image.getWidth() / image.getHeight();
            picScaleY = 1;
        }
    }

    public static String getExtension(File file) {

        String name = file.getName();

        String extension = "";

        if (name.indexOf('.') != -1) {
            extension = name.substring(name.indexOf('.') + 1);
        }

        return extension;
    }

    @Override
    public boolean hasExtension(String extension) {

        return (getExtension(file).equalsIgnoreCase(extension)); // perhaps add trim?

    }

    public void readPNG() {

        try {
            this.image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void readArtFile() {

        Project oldProject = program.getSelectedProject();

        program.createProject();

        Canvas canvas = program.getCanvas();

        program.deleteUndos();

        List<Layer> layers = canvas.getLayers();

        // remove all layers
        int removeCount = layers.size();
        for (int i = 0; i < removeCount; i++) {
            layers.remove(0);
        }

        try {
            File savedImage = file;
            Scanner sc = new Scanner(savedImage);

            int layerCount = Integer.parseInt(sc.next());
            int width = Integer.parseInt(sc.next());
            int height = Integer.parseInt(sc.next());

            int pixelColor = 0;
            int pixelCount = 0;

            canvas.setxPixels(width);
            canvas.setyPixels(height);
            canvas.findSize();

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

                Layer layer = new Layer(layerImage, i, program); //LAYCHECK

                if (layerVisible == 1) {
                    layer.setVisible(true);
                } else {
                    layer.setVisible(false);
                }

                layers.add(layer);
            }


            sc.close();
            canvas.updateDisplay();
            image = canvas.singleLayer();

        } catch (FileNotFoundException e) {
            System.out.println("no " + file.getName() + " found... -FILETAB");
        }

        program.getProjects().remove(program.getProjects().size() - 1);
        program.setSelectedProject(oldProject);
    }

    @Override
    public void draw(Graphics g) {

        super.draw(g);

        float imageMult = 0.67F;
        g.fillRect((int) (x - width / 2 * imageMult * picScaleX), (int) (y - height / 2 * 0.8 * picScaleY), (int) (width * imageMult * picScaleX), (int) (height * imageMult * picScaleY));
        g.drawImage(image, (int) (x - width / 2 * imageMult * picScaleX), (int) (y - height / 2 * 0.8 * picScaleY), (int) (width * imageMult * picScaleX), (int) (height * imageMult * picScaleY), null);
    }

    @Override
    public void clickEvent() {

        program.setToProgramMode();
        if (hasExtension("png")) {
            program.readPNG(file);
        } else if (hasExtension("art")) {
            program.readFile(file);
        }

    }
}
