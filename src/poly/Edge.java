package poly;

import util.Vector;

public class Edge {

    private final Vector start, end;

    public Edge(Vector start, Vector end) {
        this.start = start;
        this.end = end;
    }

    public boolean yInRange(float y) {
        return ((y >= start.y && y <= end.y) || (y >= end.y && y <= start.y));
    }

    public float xAtY(float y) {
        Vector low, high;

        if (start.y < end.y) {
            low = start;
            high = end;
        } else {
            low = end;
            high = start;
        }

        float progress = (y - low.y) / (high.y - low.y);

        return low.x + (high.x - low.x) * progress;
    }

}
