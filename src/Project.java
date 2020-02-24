import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Project {
	
	private Canvas canvas;
	private ArrayList<ArrayList<Layer>> undos = new ArrayList<ArrayList<Layer>>();
	
	private String lastSavedByName;
	
	private double initialCanvasX;
	private double initialCanvasY;
	private double initialCanvasWidth;
	private double initialCanvasHeight;
	
	private double defaultCanvasWidth;
	private double defaultCanvasHeight;
	
	private boolean selecting = false;
	private BufferedImage selected;
	private Point rectSelectStart;
	private Point rectSelectEnd;
	
	private Program program;
	
	public Project(Program program, double x, double y, double width, double height) {
		this.program = program;
		
		canvas = new Canvas(this, x, y, width, height);
		initialCanvasX = x;
		initialCanvasY = y;
		defaultCanvasWidth = width;
		defaultCanvasHeight = height;
		
		initialCanvasWidth = defaultCanvasWidth;
		initialCanvasHeight = defaultCanvasHeight;
	}

	public void copySelected(int index) {
		
		if (hasSelected()) {
			canvas.addLayer(index);
			
			BufferedImage image = canvas.getLayers().get(index).getImage();
			
			for (int xPixel = rectSelectStart.x; xPixel < rectSelectEnd.x; xPixel++) {
				for (int yPixel = rectSelectStart.y; yPixel < rectSelectEnd.y; yPixel++) {
					image.setRGB(xPixel, yPixel, selected.getRGB(xPixel-rectSelectStart.x, yPixel-rectSelectStart.y));
					System.out.println(xPixel+yPixel);
				}
			}
		}
	}
	
	public void setSelecting(boolean selecting) {
		this.selecting = selecting;
	}
	
	public boolean isSelecting() {
		return selecting;
	}
	
	public boolean hasSelected() {
		if (selected != null) {
			return true;
		}
		return false;
	}
	
	public void selectDrag() {
		if (selecting) {
			rectSelectStart = getCanvas().getRectSelectStart();
			rectSelectEnd = getCanvas().getRectSelectEnd();
		}
	}
	
	public void selectRelease() {
		if (selecting) {
			
			rectSelectStart = getCanvas().getRectSelectStart();
			rectSelectEnd = getCanvas().getRectSelectEnd();
			
			if (rectSelectStart.x == rectSelectEnd.x || rectSelectStart.y == rectSelectEnd.y) {
				selected = null;
			} else {
				selected = getCanvas().getDisplayLayer().getSubimage(rectSelectStart.x, rectSelectStart.y, rectSelectEnd.x-rectSelectStart.x, rectSelectEnd.y-rectSelectStart.y);
			}
			
			selecting = false;
		}
	}
	
	
	
	public BufferedImage getSelected() {
		return selected;
	}

	public void setSelected(BufferedImage selected) {
		this.selected = selected;
	}

	public Point getRectSelectStart() {
		return rectSelectStart;
	}

	public void setRectSelectStart(Point rectSelectStart) {
		this.rectSelectStart = rectSelectStart;
	}

	public Point getRectSelectEnd() {
		return rectSelectEnd;
	}

	public void setRectSelectEnd(Point rectSelectEnd) {
		this.rectSelectEnd = rectSelectEnd;
	}

	public Program getProgram() {
		return program;
	}



	public void setProgram(Program program) {
		this.program = program;
	}



	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public ArrayList<ArrayList<Layer>> getUndos() {
		return undos;
	}

	public void setUndos(ArrayList<ArrayList<Layer>> undos) {
		this.undos = undos;
	}

	public double getInitialCanvasX() {
		return initialCanvasX;
	}

	public void setInitialCanvasX(double initialCanvasX) {
		this.initialCanvasX = initialCanvasX;
	}

	public double getInitialCanvasY() {
		return initialCanvasY;
	}

	public void setInitialCanvasY(double initialCanvasY) {
		this.initialCanvasY = initialCanvasY;
	}

	public double getInitialCanvasWidth() {
		return initialCanvasWidth;
	}

	public void setInitialCanvasWidth(double initialCanvasWidth) {
		this.initialCanvasWidth = initialCanvasWidth;
	}

	public double getInitialCanvasHeight() {
		return initialCanvasHeight;
	}

	public void setInitialCanvasHeight(double initialCanvasHeight) {
		this.initialCanvasHeight = initialCanvasHeight;
	}

	public double getDefaultCanvasWidth() {
		return defaultCanvasWidth;
	}

	public void setDefaultCanvasWidth(double defaultCanvasWidth) {
		this.defaultCanvasWidth = defaultCanvasWidth;
	}

	public double getDefaultCanvasHeight() {
		return defaultCanvasHeight;
	}

	public void setDefaultCanvasHeight(double defaultCanvasHeight) {
		this.defaultCanvasHeight = defaultCanvasHeight;
	}

	public String getLastSavedByName() {
		return lastSavedByName;
	}

	public void setLastSavedByName(String lastSavedByName) {
		this.lastSavedByName = lastSavedByName;
	}
	
	
}
