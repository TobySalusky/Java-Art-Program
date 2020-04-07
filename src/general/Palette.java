package general;

import java.awt.*;
import java.util.List;

public class Palette {

    private Program program;
    private PaletteTab[] tabs;
    private boolean update = true;
    private int reselect = -999;

    private PaletteTab tabClicked;

    private List<Integer> lastColors;

    private final int perRow = 5;
    private final float distBetween = 30;
    private final float startX = 30;
    private final float startY = 200;

    public Palette(Program program) {

        this.program = program;
        PaletteTab.palette = this;

    }

    public void createTabs(List<Integer> colors) {

        tabs = new PaletteTab[colors.size()];

        int i = 0;
        for (int rgb : colors) {

            tabs[i] = new PaletteTab(rgb, startX + i % perRow * distBetween, startY + i / perRow * distBetween);

            i++;
        }

    }

    public void setReselect(int rgb) {

        this.reselect = rgb;

    }

    private void reselectTab(int rgb) {

        for (PaletteTab tab : tabs) {

            if (tab.getRGB() == rgb) {
                tabClicked = tab;
                tab.setClicked(true);
                break;
            }

        }

    }

    public void checkClicks(float mouseX, float mouseY) {

        for (PaletteTab tab : tabs) {
            if (tab.checkClick(mouseX, mouseY)) {

                if (tab.isClicked()) {

                    if (tabClicked != tab && tabClicked != null) {
                        tabClicked.setClicked(false);
                    }

                    tabClicked = tab;
                    break;

                } else {

                    tabClicked = null;

                }
            }
        }
    }

    public void close() {

        unselect();
    }

    public void unselect() {

        if (tabClicked != null) {
            tabClicked.setClicked(false);
            tabClicked = null;
        }

    }

    public PaletteTab getTabClicked() {
        return tabClicked;
    }

    public Program getProgram() {
        return program;
    }

    public void findColors() {

        Canvas canvas = program.getCanvas();

        List<Integer> colors = canvas.getUniqueColors();

        if (!colors.equals(lastColors)) {
            createTabs(colors);
        }
        lastColors = colors;
    }

    public void draw(Graphics g) {

        if (update) {
            findColors();
            update = false;

            if (reselect != -999) {

                reselectTab(reselect);
                reselect = -999;
            }
        }

        g.setColor(program.getOverlayColor());
        g.fillRect(0, (int) startY - 20, (int) (perRow * distBetween + startX + 10), ((lastColors.size() - 1) / perRow + 1) * 30 + 10);
        g.setColor(Color.WHITE);
        g.drawRect(-1, (int) startY - 20, (int) (perRow * distBetween + startX + 10), ((lastColors.size() - 1) / perRow + 1) * 30 + 10);

        for (PaletteTab tab : tabs) {

            tab.draw(g);

        }

    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}
