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

    private Program program;

    private static final boolean readSubFolders = false;

    // note, make dynamic value class with .value/ .getValue() + incorporates sliders, text fields, check boxes, etc...


    public FileMenu(Program program, String folderPath, Rectangle boundaries) {

        this.program = program;

        this.folderPath = folderPath;
        this.bound = boundaries;
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

    public void readFolder(File folder) {

        for (final File file : Objects.requireNonNull(folder.listFiles())) {

            if (readSubFolders && file.isDirectory()) {
                readFolder(file);
            }

            if (hasExtension(file, "png") || hasExtension(file, "art")) {
                fileTabs.add(new FileTab(program, file));
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

	/*public void check() {

		if (!folderRead) {

			setUp();

		}

	}*/

    public void checkClicks(float mouseX, float mouseY) {

        for (FileTab tab : fileTabs) {

            tab.checkClick(mouseX, mouseY);

        }

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

        FileTab lastTab = fileTabs.get(fileTabs.size() - 1);
        float upperLimit = lastTab.getToY() - tabHeight / 2F - tabSpacing / 2F;
        if (scroll > upperLimit) {
            scroll += (upperLimit - scroll) / scrollDivide;
        }

        for (FileTab tab : fileTabs) {

            tab.tick(scroll);
            tab.draw(g);

        }

		/*int tabCount = 0;
		int row = 0;


		for (int i = 0; i < fileImages.size(); i++) {

			BufferedImage image = fileImages.get(i);

			if (tabCount >= tabsPerRow) {
				tabCount = 0;
				row++;
			}

			g.drawImage(image, firstTabX-tabWidth/2 + ((tabWidth + tabSpacing) * tabCount), bound.y + (row * (tabHeight + tabSpacing)), tabWidth, tabHeight, null);

			tabCount++;
		}

		g.setColor(Color.red);
		g.drawRect(bound.x, bound.y, bound.width, bound.height);*/

    }

    public void checkHovers(float mouseX, float mouseY) {

        for (FileTab tab : fileTabs) {

            tab.ifHover(mouseX, mouseY);

        }

    }

    public int getTabSize() {
        return tabSize;
    }

    public void setTabSize(int tabSize) {
        this.tabSize = tabSize;
    }

}
