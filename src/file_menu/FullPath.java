package file_menu;

import general.Program;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FullPath {

    private final Program program;
    private final List<PathClickable> paths;

    public FullPath(Program program, String path) {

        this.program = program;

        paths = new ArrayList<>();

        createPaths(path);

    }

    public void checkClicks(float mouseX, float mouseY) {

        for (FileTab tab : paths) {

            tab.checkClick(mouseX, mouseY);

        }

    }

    public void draw(Graphics g) {

        for (FileTab tab : paths) {

            tab.draw(g);

        }

    }

    public void checkHovers(float mouseX, float mouseY) {

        for (FileTab tab : paths) {

            tab.ifHover(mouseX, mouseY);

        }

    }

    private void createPaths(String path) {

        if (path.endsWith("\\")) {
            path = path.substring(0, path.length() - 1);
        }

        int index = path.lastIndexOf("\\");
        while (index != -1) {

            createPath(path);
            path = path.substring(0, index);

            index = path.lastIndexOf("\\");
        }
        situatePaths();
    }

    public void scroll(float amount) {

        for (FileTab tab : paths) {
            tab.tick(amount);
        }

    }

    private void situatePaths() {

        for (int i = 0; i < paths.size(); i++) {

            paths.get(i).setDimensions(200 + PathClickable.TAB_WIDTH * i, 50F, PathClickable.TAB_WIDTH, PathClickable.TAB_HEIGHT);

        }

    }

    private void createPath(String path) {
        paths.add(0, new PathClickable(program, new File(path)));
    }
}
