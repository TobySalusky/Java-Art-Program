package misc;

import general.Canvas;

import java.awt.image.BufferedImage;

public class AsciiGenerator {

    private static final String empty = "  ", up = "| ", flat = "_ ", leftDiag = "\\ ", rightDiag = "/ ", dot = "o ";

    public static String toAscii(BufferedImage image) {

        StringBuilder ascii = new StringBuilder();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                removeBlockyCorner(image, x, y);
            }
        }

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                ascii.append(findChar(image, x, y));
            }
            ascii.append("\n");
        }

        return ascii.toString();
    }

    private static void removeBlockyCorner(BufferedImage image, int x, int y) {

        if (pixelFilled(image, x, y)) {
            if ((pixelFilled(image, x + 1, y) && pixelFilled(image, x, y - 1)) ||
                    (pixelFilled(image, x - 1, y) && pixelFilled(image, x, y + 1)) ||
                    (pixelFilled(image, x - 1, y) && pixelFilled(image, x, y - 1)) ||
                    (pixelFilled(image, x + 1, y) && pixelFilled(image, x, y + 1))) {

                image.setRGB(x, y, Canvas.erasedColorRGB);

            }
        }

    }

    private static String findChar(BufferedImage image, int x, int y) {

        if (!pixelFilled(image, x, y)) {
            return empty;
        }

        if (pixelFilled(image, x - 1, y) && pixelFilled(image, x + 1, y)) {
            return flat;
        }

        if (pixelFilled(image, x, y - 1) && pixelFilled(image, x, y + 1)) {
            return up;
        }

        if (pixelFilled(image, x - 1, y - 1)) {
            return leftDiag;
        }

        if (pixelFilled(image, x + 1, y - 1)) {
            return rightDiag;
        }

        if (pixelFilled(image, x, y - 1) || pixelFilled(image, x, y + 1)) {
            return up;
        }

        if (pixelFilled(image, x - 1, y) || pixelFilled(image, x + 1, y)) {
            return flat;
        }

        if (pixelFilled(image, x + 1, y + 1)) {
            return leftDiag;
        }

        if (pixelFilled(image, x - 1, y + 1)) {
            return rightDiag;
        }

        return dot;
    }

    public static boolean pixelFilled(BufferedImage image, int x, int y) {

        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
            return false;
        }

        return image.getRGB(x, y) != Canvas.erasedColorRGB;
    }
}
