package poly;

import general.Canvas;
import general.Program;
import general.Project;
import util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PolyCanvas extends Canvas {

    private List<Polygon> polyList = new ArrayList<>();

    private Polygon polygonCreating;

    public PolyCanvas(Project project, int xPixel, int yPixel, float x, float y, float width, float height) {
        super(project, xPixel, yPixel, x, y, width, height);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);

        for (Polygon polygon : polyList) {
            polygon.render(g);
            if (polygon == polygonCreating) {
                polygon.renderExtras(g);
            }
        }
    }

    @Override
    public void polySelect(float mouseX, float mouseY) {

        polygonCreating = null;

        Vector vec = new Vector(mouseX, mouseY);
        for (Polygon polygon : polyList) {
            if (polygon.pointInside(vec)) {
                polygonCreating = polygon;
                break;
            }
        }
    }

    @Override
    public void colorChanged(Color color) {
        if (polygonCreating != null) {
            polygonCreating.setColor(color);
        }
    }

    @Override
    public void polyAction(float mouseX, float mouseY) {

        if (Program.initialClick) {
            Program.initialClick = false;
            Vector vec = new Vector(mouseX, mouseY);

            if (polygonCreating == null) {
                polygonCreating = new Polygon(Program.brushColor);
                polyList.add(polygonCreating);
            }
            polygonCreating.addPoint(vec);
        }
    }

    @Override
    public void enterAction() {
        polygonCreating = null;
    }
}
