package file_menu;

import general.Program;

import java.awt.*;
import java.io.File;

public class PathClickable extends FolderTab {

    public static final float TAB_WIDTH = 150F;
    public static final float TAB_HEIGHT = 40F;

    public PathClickable(Program program, File file) {
        super(program, file);
    }

    @Override
    public Font findFont() {
        return new Font("Impact", Font.PLAIN, 25);
    }
}