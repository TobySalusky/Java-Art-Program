package poly;

import util.Gizmo;
import util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Polygon {

    private final List<Vector> vertices = new ArrayList<>(3);
    private Color color;

    public Polygon(Color color) {
        this.color = color;
    }

    public void insertPoint(Vector vertex, int index) {
        vertices.add(index, vertex);
    }

    public void addPoint(Vector vertex) {
        vertices.add(vertex);
    }

    public void renderExtras(Graphics g) {
        renderEdges(g);
        renderVertices(g);
    }

    private void renderVertices(Graphics g) {

        for (int i = 0; i < vertices.size(); i++) {
            Vector vector = vertices.get(i);

            float progress = (float) (i + 1) / vertices.size();
            g.setColor(new Color((int) (255 * progress), 0, (int) (255 * (1 - progress))));
            Gizmo.dot(g, vector, 4);
        }
    }

    public boolean pointInside(Vector vector) {
        Edge[] edges = genEdges();

        int left = 0;
        for (Edge edge : edges) {
            if (edge.yInRange(vector.y) && edge.xAtY(vector.y) < vector.x) {
                left++;
            }
        }

        return left % 2 == 1;
    }

    private Edge[] genEdges() {

        Edge[] edges = new Edge[vertices.size()];

        for (int i = 0; i < vertices.size() - 1; i++) {
            edges[i] = new Edge(vertices.get(i), vertices.get(i + 1));
        }
        edges[edges.length - 1] = new Edge(vertices.get(vertices.size() - 1), vertices.get(0));

        return edges;
    }

    private void renderEdges(Graphics g) {
        g.setColor(Color.BLACK);

        int[] xPoints = new int[vertices.size()];
        int[] yPoints = new int[xPoints.length];

        for (int i = 0; i < vertices.size(); i++) { // TODO: camera
            Vector vert = vertices.get(i);
            xPoints[i] = (int) vert.x;
            yPoints[i] = (int) vert.y;
        }

        g.drawPolygon(xPoints, yPoints, xPoints.length);
    }

    public void render(Graphics g) {
        g.setColor(color);

        int[] xPoints = new int[vertices.size()];
        int[] yPoints = new int[xPoints.length];

        for (int i = 0; i < vertices.size(); i++) { // TODO: camera
            Vector vert = vertices.get(i);
            xPoints[i] = (int) vert.x;
            yPoints[i] = (int) vert.y;
        }

        g.fillPolygon(xPoints, yPoints, xPoints.length);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
