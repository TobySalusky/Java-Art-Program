package general;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ClipBoardUser implements ClipboardOwner {

    @Override
    public void lostOwnership(Clipboard clip, Transferable trans) {

        System.out.println("WARNING: Clipboard ownership lost - general.ClipBoardUser(lostOwnership)");

    }

    public BufferedImage getCopied() {

        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {

            return (BufferedImage) c.getData(DataFlavor.imageFlavor);

        } catch (UnsupportedFlavorException ufe) {

            System.out.println("clipboard doesn't contain image!");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void copyToClipboard(Image image) {

        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        c.setContents(new TransferableImage(image), this);

    }

    public static void copyToClipboard(String text) {

        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        c.setContents(new StringSelection(text), null);

    }

    private static class TransferableImage implements Transferable {

        private final Image image;

        private TransferableImage(Image image) {
            this.image = image;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor.equals(DataFlavor.imageFlavor) && image != null) {
                return image;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {

            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {

            return flavor == DataFlavor.imageFlavor;
        }

    }

}
