package general;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
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

            BufferedImage image = (BufferedImage) c.getData(DataFlavor.imageFlavor);
            return image;

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

    private class TransferableImage implements Transferable {

        private Image image;

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

            DataFlavor[] flavors = {DataFlavor.imageFlavor};

            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {

            if (flavor == DataFlavor.imageFlavor) {
                return true;
            }

            return false;
        }

    }

}
