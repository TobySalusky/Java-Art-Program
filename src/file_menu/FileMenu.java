package file_menu;

import general.Program;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FileMenu {

    String folderPath;
    boolean folderRead = false;

    private List<FileTab> fileTabs = new ArrayList<>();

    private Rectangle bound;

    private int tabSize = 150;
    private int oldTabSize = tabSize;
    private int tabWidth = tabSize;
    private int tabHeight = tabSize;

    private int firstTabX;
    private int tabsPerRow;
    private int tabSpacing = 20;

    protected float scroll = 0;

    private int folderCount;

    private Program program;

    private static final boolean readSubFolders = false;

    // note, make dynamic value class with .value/ .getValue() + incorporates sliders, text fields, check boxes, etc...

    private FullPath clickPath;

    public FileMenu(Program program, String folderPath, Rectangle boundaries) {

        this.program = program;

        setFolderPath(folderPath);
        this.bound = boundaries;
    }

    private void setFolderPath(String folderPath) {
        this.folderPath = folderPath;

        clickPath = new FullPath(program, folderPath);

        if (folderPath.endsWith("\\")) {
            Program.setFilePath(folderPath);
        } else {
            Program.setFilePath(folderPath + "\\");
        }
    }

    public void resetTo(String newFolderPath) {

        setFolderPath(newFolderPath);

        clearTabs();
        readFolder();
        setTabPositions();
    }


    public void fixWidthAndHeight() {
        tabWidth = tabSize;
        tabHeight = tabSize;
    }

    public void defineBoundaries() {

        tabsPerRow = 0;

        firstTabX = bound.x + tabWidth / 2;

        int thisX = firstTabX;

        while (thisX + tabWidth / 2 <= bound.x + bound.width) {

            tabsPerRow++;

            thisX += tabSpacing + tabWidth;

        }

        thisX -= tabSpacing + tabWidth;

        int adjust = (bound.x + bound.width) - (thisX + tabWidth / 2);

        firstTabX += adjust / 2;

    }

    public void setUp() {

        defineBoundaries();

        readFolder();
        setTabPositions();
    }

    public void readFolder() {

        final File folder = new File(folderPath);

        readFolder(folder);
    }

    public void clearTabs() {

        fileTabs = new ArrayList<>();
        folderCount = 0;

    }

    public void readFolder(File folder) {

        File[] fileList = folder.listFiles();

        if (fileList != null) {

            for (final File file : fileList) {

                if (file == null) {
                    continue;
                }

                if (file.isDirectory()) {

                    fileTabs.add(folderCount, new FolderTab(program, file));
                    folderCount++;

                } else {

            /*if (readSubFolders && file.isDirectory()) {
                readFolder(file);
            }*/

                    if (hasExtension(file, "png") || hasExtension(file, "art")) {
                        fileTabs.add(new ImageTab(program, file));
                    }
                }
            }
        }

        folderRead = true;

    }

    public void setTabPositions() {

        int tabCount = 0;
        int row = 0;


        for (FileTab fileTab : fileTabs) {

            if (tabCount >= tabsPerRow) {
                tabCount = 0;
                row++;
            }

            fileTab.setDimensions(firstTabX + ((tabWidth + tabSpacing) * tabCount), bound.y + tabHeight / 2F + (row * (tabHeight + tabSpacing)), tabWidth, tabHeight);

            tabCount++;
        }

    }

    public String getExtension(File file) {

        String name = file.getName();

        String extension = "";

        if (name.indexOf('.') != -1) {
            extension = name.substring(name.indexOf('.') + 1);
        }

        return extension;
    }

    public boolean hasExtension(File file, String extension) {

        return (getExtension(file).equalsIgnoreCase(extension)); // perhaps add trim?

    }

    public void checkClicks(float mouseX, float mouseY) {

        for (FileTab tab : fileTabs) {

            tab.checkClick(mouseX, mouseY);

        }

        clickPath.checkClicks(mouseX, mouseY);

    }

    public void draw(Graphics g) {

        if (tabSize != oldTabSize) {
            oldTabSize = tabSize;

            fixWidthAndHeight();
            defineBoundaries();

        }

        // limits scroll
        int scrollDivide = 5;

        if (scroll < 0) {
            scroll -= scroll / scrollDivide;
        }

        if (fileTabs.size() > 0) {
            FileTab lastTab = fileTabs.get(fileTabs.size() - 1);
            float upperLimit = lastTab.getToY() - tabHeight / 2F - tabSpacing / 2F;
            if (scroll > upperLimit) {
                scroll += (upperLimit - scroll) / scrollDivide;
            }

            for (FileTab tab : fileTabs) {

                tab.tick(scroll);
                tab.draw(g);

            }
        }

        clickPath.scroll(scroll);
        clickPath.draw(g);
    }

    public void checkHovers(float mouseX, float mouseY) {

        for (FileTab tab : fileTabs) {

            tab.ifHover(mouseX, mouseY);

        }

        clickPath.checkHovers(mouseX, mouseY);

    }

    public int getTabSize() {
        return tabSize;
    }

    public void setTabSize(int tabSize) {
        this.tabSize = tabSize;
    }

    public boolean isFolderRead() {
        return folderRead;
    }

    public void addScroll(float scroll) {
        this.scroll += scroll;
    }
}
