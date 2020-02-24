import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Canvas {

	private double x;
	private double y;
	private double width;
	private double height;

	private double displayX;
	private double displayY;
	private double displayWidth;
	private double displayHeight;
	private double displayDivider = 5;

	private int programWidth = Program.WIDTH;
	private int programHeight = Program.HEIGHT;
	private int canvasDivider = Program.canvasDivider;

	private int xPixel = programWidth/canvasDivider;
	private int yPixel = programHeight/canvasDivider;

	//private int xPixel = 1920;
	//private int yPixel = 1080;

	private Project project;

	private int selectedLayer = 0;
	private ArrayList<Layer> layers = new ArrayList<Layer>();

	private double lastX = 0;
	private double lastY = 0;

	private double brushX = 0;
	private double brushY = 0;
	private double brushCanvasX = 0;
	private double brushCanvasY = 0;

	private Point rectSelect1;
	private Point rectSelect2;
	private Point rectSelectStart;
	private Point rectSelectEnd;

	private int pixelsFilled = 0;

	private Color erasedColor = new Color(255,255,255,0);
	private int erasedColorRGB = erasedColor.getRGB();
	private Color backgroundColor = Color.LIGHT_GRAY;

	private int fillColor = new Color(13, 138, 17, 29).getRGB();

	private BufferedImage displayLayer = null;

	// THIS NEEDS TO BE UPDATED!!! please fix
	private BufferedImage nullLayer = new BufferedImage((int)xPixel, (int)yPixel, BufferedImage.TYPE_INT_ARGB);

	public Canvas(Project project, double x, double y, double width, double height) {
		this.project = project;

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.displayX = x;
		this.displayY = y;
		this.displayWidth = width;
		this.displayHeight = height;

		// setup
		addLayer();
		displayLayer = singleLayer();

		fillNullLayer();
	}



	public BufferedImage getDisplayLayer() {
		return displayLayer;
	}



	public void setDisplayLayer(BufferedImage displayLayer) {
		this.displayLayer = displayLayer;
	}



	public void resortLayers() {

		for (int i = 0; i < layers.size(); i++) {

			Layer layer = layers.get(i);

			if (layer.getIndex() != i) {
				moveLayerTo(layer, layer.getIndex());
				i = 0;
			}

		}

	}

	public void moveLayerTo(Layer layer, int index) {
		layers.remove(layer);
		layers.add(index, layer);
	}

	public void fillNullLayer() {
		for (int i = 0; i < xPixel; i++) {
			for (int j = 0; j < yPixel; j++) {
				nullLayer.setRGB(i, j, erasedColorRGB);
			}
		} 
	}

	public void findSize() {
		int xPixel = getxPixel();
		int yPixel = getyPixel();

		double sizeMult = (double)project.getDefaultCanvasWidth()/xPixel;
		sizeMult = Math.min(sizeMult, (double)project.getDefaultCanvasHeight()/yPixel);

		project.setInitialCanvasWidth(sizeMult*xPixel);
		project.setInitialCanvasHeight(sizeMult*yPixel);

		width = Program.initialCanvasWidth;
		height = Program.initialCanvasHeight;
		x = Program.initialCanvasX;
		y = Program.initialCanvasY;

		nullLayer = new BufferedImage((int)xPixel, (int)yPixel, BufferedImage.TYPE_INT_ARGB);
		fillNullLayer();
	}

	public void setSelectedLayerIndex(int index) {
		selectedLayer = index;
	}

	public void updateDisplay() {
		displayLayer = singleLayer();
	}

	public BufferedImage singleLayer() {

		BufferedImage singleLayer;

		Layer firstLayer = layers.get(0);
		
		if (firstLayer.isVisible()) {
			if (firstLayer.getOpacity() == 100) {
				singleLayer = firstLayer.getImage();
			} else {
				singleLayer = combineOpacity(firstLayer.getImage(), nullLayer, firstLayer.getOpacity());
			}
		} else {
			singleLayer = nullLayer; // please fix will cause issues with changing size
		}

		for (int i = 1; i < layers.size(); i++) {
			Layer topLayer = layers.get(i);
			
			if (topLayer.isVisible()) {
				if (firstLayer.getOpacity() == 100) {
					singleLayer = combine(topLayer.getImage(), singleLayer);
				} else {
					singleLayer = combineOpacity(topLayer.getImage(), singleLayer, topLayer.getOpacity());
				}
			}
		}

		return singleLayer;
	}

	public void updateFrom(int x1, int y1, int x2, int y2) {

		x1 = bindX(x1);
		y1 = bindY(y1);
		x2 = bindX(x2);
		y2 = bindY(y2);

		for (int i = 0; i < layers.size(); i++) {
			for (int row = x1; row <= x2; row++) {
				for (int col = y1; col <= y2; col++) {
					// OPTIMIZE PLEASE FIX
					int pixelColor = layers.get(i).getImage().getRGB(row,col);

					if (pixelColor != erasedColorRGB && layers.get(i).isVisible()) {
						displayLayer.setRGB(row,col,pixelColor);
					} else {
						if (i == 0) {
							if (layers.get(i).isVisible()) {
								displayLayer.setRGB(row,col,pixelColor);
							} else {
								displayLayer.setRGB(row,col,erasedColorRGB);
							}
						}
					}
				}
			}
		}
	}

	public BufferedImage combine(BufferedImage topLayer, BufferedImage bottomLayer) {

		BufferedImage newLayer = new BufferedImage((int)xPixel, (int)yPixel, BufferedImage.TYPE_INT_ARGB);

		for (int row = 0; row < xPixel; row++) {
			for (int col = 0; col < yPixel; col++) {
				newLayer.setRGB(row,col,bottomLayer.getRGB(row,col));
			}
		}

		for (int row = 0; row < xPixel; row++) {
			for (int col = 0; col < yPixel; col++) {
				int pixelColor = topLayer.getRGB(row,col);

				if (pixelColor != erasedColorRGB) {
					newLayer.setRGB(row,col,pixelColor);
				}
			}
		}

		return newLayer;
	}
	
	public BufferedImage combineOpacity(BufferedImage topLayer, BufferedImage bottomLayer, double topOpacity) {

		topOpacity /= 100;
		
		BufferedImage newLayer = new BufferedImage((int)xPixel, (int)yPixel, BufferedImage.TYPE_INT_ARGB);

		for (int row = 0; row < xPixel; row++) {
			for (int col = 0; col < yPixel; col++) {
				newLayer.setRGB(row,col,bottomLayer.getRGB(row,col));
			}
		}

		for (int row = 0; row < xPixel; row++) {
			for (int col = 0; col < yPixel; col++) {
				int pixelColor = topLayer.getRGB(row,col);

				if (pixelColor != erasedColorRGB) {
					
					Color bottomColor = new Color(bottomLayer.getRGB(row,col), true);
					
					int bottomOpacity = bottomColor.getAlpha();
					
					Color topColor = new Color(pixelColor);
					
					int red = (int)(topColor.getRed()*topOpacity+bottomColor.getRed()*(1-topOpacity));
					int green = (int)(topColor.getGreen()*topOpacity+bottomColor.getGreen()*(1-topOpacity));
					int blue = (int)(topColor.getBlue()*topOpacity+bottomColor.getBlue()*(1-topOpacity));
					// 255 = max
					int opacity = (int)(Math.min((bottomOpacity + topOpacity*255), 255));
					
					Color newColor = new Color(red, green, blue, opacity);
					
					newLayer.setRGB(row,col,newColor.getRGB());
				}
			}
		}

		return newLayer;
	}
	
	public void addLayer() {
		layers.add(new Layer(new BufferedImage((int)xPixel, (int)yPixel, BufferedImage.TYPE_INT_ARGB),layers.size(),project.getProgram()));
		resetLayer(layers.size()-1);
	}

	public void addLayer(int index) {
		layers.add(index, new Layer("Layer " + (layers.size()+1), new BufferedImage((int)xPixel, (int)yPixel, BufferedImage.TYPE_INT_ARGB),index,project.getProgram()));
		resetLayer(index);

		for (int i = index+1; i < layers.size(); i++) {
			Layer layer = layers.get(i);
			layer.setIndex(layer.getIndex()+1);
			layer.getButton().findToY();
		}
	}

	public void moveLayer(Layer layer, int newPosition) {
		layers.remove(layer);
		layers.add(newPosition, layer); //please fix?
	}

	public Layer getSelectedLayer() {
		return layers.get(getSelectedLayerIndex());
	}

	public int getSelectedLayerIndex() {
		selectedLayer(selectedLayer);
		return selectedLayer;
	}

	public int getLayerCount() {
		return layers.size();
	}

	public void selectedLayer(int index) {
		selectedLayer = Math.min(Math.max(index,0),layers.size()-1);
	}

	public void resetPosition() {

		setX(project.getInitialCanvasX());
		setY(project.getInitialCanvasY());
		setWidth(project.getInitialCanvasWidth());
		setHeight(project.getInitialCanvasHeight());

	}

	public void resetCanvas() {

		// reset layers
		for (int i = 0; i < xPixel; i++) {
			for (int j = 0; j < yPixel; j++) {

				for (int layer = 0; layer < layers.size(); layer++) {
					layers.get(layer).getImage().setRGB(i, j, erasedColorRGB);
				}
			}
		}   
	}

	public void resetLayer(int layerIndex) {
		for (int i = 0; i < xPixel; i++) {
			for (int j = 0; j < yPixel; j++) {
				layers.get(layerIndex).getImage().setRGB(i, j, erasedColorRGB);
			}
		} 
	}

	public double toBrushCanvasX(double mouseX) {
		return xPixel*((mouseX-(displayX-displayWidth/2))/displayWidth);
	}

	public double toBrushCanvasY(double mouseY) {
		return yPixel*((mouseY-(displayY-displayHeight/2))/displayHeight);
	}

	public void fill(int x, int y, int oldColor, int newColor) {

		Layer onLayer = getSelectedLayer();

		ArrayList<Point> points = new ArrayList<Point>();

		onLayer.getImage().setRGB(x,y,newColor);
		points.add(new Point(x,y));

		while (points.size() > 0) {
			Point point = points.get(0);

			onLayer.getImage().setRGB(point.x,point.y,newColor);

			tryFill(onLayer, points, point.x-1 ,point.y ,oldColor ,newColor);
			tryFill(onLayer, points, point.x+1 ,point.y ,oldColor ,newColor);
			tryFill(onLayer, points, point.x ,point.y-1 ,oldColor ,newColor);
			tryFill(onLayer, points, point.x ,point.y+1 ,oldColor ,newColor);
			points.remove(0);
		}

	}

	public void tryFill(Layer onLayer, ArrayList<Point> points, int x, int y, int oldColor, int newColor) {
		if (mayFill(x,y,oldColor,newColor)) {
			onLayer.getImage().setRGB(x,y,newColor);
			updateFrom(x,y,x,y);
			points.add(new Point(x,y));
		}
	}

	public boolean mayFill(int x, int y, int oldColor, int newColor) {

		if (oldColor == newColor) {
			return false;
		}

		if (x >= 0 && x < xPixel && y >= 0 && y < yPixel) {

			if (displayLayer.getRGB(x, y) == oldColor) {
				return true;
			}

		}

		return false;
	}

	// these methods are for the outdated filling
	public void fill(int brushColor, int onColor) {

		BufferedImage layer = layers.get(selectedLayer).getImage();

		int fillIterations = 0;

		pixelsFilled = 0;
		do {
			fillIterations++;
			pixelsFilled = 0;

			if (fillIterations % 2 == 0) {
				for (int row = layer.getWidth()-1; row >= 0; row--) {
					for (int col = layer.getHeight()-1; col >= 0; col--) {
						fillAround(row,col,onColor);
					}
				}
			} else {
				for (int row = 0; row < layer.getWidth(); row++) {
					for (int col = 0; col < layer.getHeight(); col++) {
						fillAround(row,col,onColor);
					}
				}
			}

		} while (pixelsFilled > 0);

		// change 2's to 1's
		for (int row = 0; row < layer.getWidth(); row++) {

			for (int col = 0; col < layer.getHeight(); col++) {

				if (layer.getRGB(row,col) == fillColor) { //make fill color rgb, please fix
					layer.setRGB(row,col,brushColor); //please fix
				}

			}
		}

		System.out.println("DEBUG: fill iterations: " + fillIterations);

	}

	public void fillAround(int row, int col, int onColor) {

		BufferedImage layer = layers.get(selectedLayer).getImage();

		if (layer.getRGB(row, col) == fillColor) { // sub-par code, please fix

			int pixelIntValue;

			for (int i = 0; i < 2; i++) {
				pixelIntValue = (Math.min(Math.max(row-(i*2-1),0),layer.getWidth()-1));

				canFill(pixelIntValue,col,onColor);
			}

			for (int i = 0; i < 2; i++) {
				pixelIntValue = (Math.min(Math.max(col-(i*2-1),0),layer.getHeight()-1));

				canFill(row,pixelIntValue,onColor);
			}
		}
	}

	public void fillPixel(int xPixel, int yPixel, BufferedImage layer) {
		layer.setRGB(xPixel,yPixel,fillColor);
		pixelsFilled++;
	}

	public void canFill(int xPixel, int yPixel, int onColor) {

		int pixelColor = displayLayer.getRGB(xPixel,yPixel);

		BufferedImage image = layers.get(selectedLayer).getImage();

		if (pixelColor == onColor && image.getRGB(xPixel,yPixel) != fillColor) {
			fillPixel(xPixel,yPixel,image);
		}
	}
	// this concludes the methods are for the outdated filling


	public boolean onCanvas(double brushX, double brushY) {

		if (brushX >= 0 && brushX <= layers.get(selectedLayer).getImage().getWidth()-1) {
			if (brushY >= 0 && brushY <= layers.get(selectedLayer).getImage().getHeight()-1) {
				return true;
			}
		}

		return false;
	}

	public int bindX(int brushX) { //please fix, use this for fill, please bud
		return (Math.min(Math.max(brushX,0),layers.get(selectedLayer).getImage().getWidth()-1));
	}
	public int bindY(int brushY) { // also mostly unnecessary
		return (Math.min(Math.max(brushY,0),layers.get(selectedLayer).getImage().getHeight()-1));
	}

	public void brush(double mouseX, double mouseY, double brushSize, double stabilizer, Color brushColor) {

		if (layers.get(selectedLayer).isVisible()) {

			BufferedImage layer = layers.get(selectedLayer).getImage();

			brushCanvasX = toBrushCanvasX(mouseX);
			brushCanvasY = toBrushCanvasY(mouseY);

			brushX += (brushCanvasX-brushX)/(Math.max(stabilizer, 1));
			brushY += (brushCanvasY-brushY)/(Math.max(stabilizer, 1));

			switch (Program.selectedBrush) {

			case fill:
				if (onCanvas(brushX,brushY) && Program.initialClick) {

					long startTime = System.nanoTime();
					
					Program.initialClick = false;

					int colorOn = displayLayer.getRGB((int)brushX, (int)brushY);

					fill((int)brushX, (int)brushY, colorOn, brushColor.getRGB());

					long endTime = System.nanoTime();
					long duration = (endTime - startTime)/1000000;
					System.out.println("fill took " + duration + " miliseconds");

					
					// outdated method
					/*Program.initialClick = false;

					int colorOn = displayLayer.getRGB((int)brushX, (int)brushY);

					long startTime = System.nanoTime();

					layer.setRGB((int)brushX, (int)brushY, fillColor); //somewhat faster for large areas
					fill(brushColor.getRGB(),colorOn);

					updateDisplay();

					long endTime = System.nanoTime();
					long duration = (endTime - startTime)/1000000;
					System.out.println("fill took " + duration + " miliseconds");*/
				}
				break;

			case colorGrab:
				if (onCanvas(brushX,brushY)) {
					int colorGrabbed = displayLayer.getRGB((int)brushX, (int)brushY);
					Program.brushColor = new Color(colorGrabbed);

					Program.setSliders();
				}
				break;

			case brush:

				if (Program.initialClick) {
					Program.initialClick = false;

					lastX = brushX;
					lastY = brushY;
				}

				drawAt(brushX,brushY,layer,brushSize/2,brushColor);

				drawLineFrom(lastX,lastY,brushX,brushY,layer,brushSize/2,brushColor);

				lastX = brushX;
				lastY = brushY;

				// OPTIMIZE PLEASE FIX
				//updateFrom((int)(brushX-brushSize/2-1),(int)(brushY-brushSize/2-1),(int)(brushX+brushSize/2+1),(int)(brushY+brushSize/2+1));
				//updateDisplay();
				break;

			case eraser:

				if (Program.initialClick) {
					Program.initialClick = false;

					lastX = brushX;
					lastY = brushY;
				}

				eraseAt(brushX,brushY,layer,brushSize/2);

				eraseLineFrom(lastX,lastY,brushX,brushY,layer,brushSize/2);

				lastX = brushX;
				lastY = brushY;

				//updateDisplay();
				break;

			case rectSelect:

				if (Program.initialClick) {
					Program.initialClick = false;

					rectSelect1 = new Point(bindX((int)brushX), bindY((int)brushY));

					project.setSelecting(true);
				}
				int rectSelect2X = bindX((int)brushX);
				int rectSelect2Y = bindY((int)brushY);

				int xAdd = 0;
				int yAdd = 0;

				if (rectSelect2X > rectSelect1.x) xAdd = 1;
				if (rectSelect2Y > rectSelect1.y) yAdd = 1;

				rectSelect2 = new Point(rectSelect2X+xAdd, rectSelect2Y+yAdd);

				rectSelectStart = new Point(Math.min(rectSelect1.x,rectSelect2.x), Math.min(rectSelect1.y,rectSelect2.y));
				rectSelectEnd = new Point(Math.max(rectSelect1.x,rectSelect2.x), Math.max(rectSelect1.y,rectSelect2.y));

				break;

			default:
				break;
			}       
		}
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



	public Point getRectSelect1() {
		return rectSelect1;
	}

	public void setRectSelect1(Point rectSelect1) {
		this.rectSelect1 = rectSelect1;
	}

	public Point getRectSelect2() {
		return rectSelect2;
	}

	public void setRectSelect2(Point rectSelect2) {
		this.rectSelect2 = rectSelect2;
	}

	public void eraseAt(double atX, double atY, BufferedImage layer, double brushSize) {
		drawAt(atX,atY,layer,brushSize,erasedColor);
	}

	public void eraseLineFrom(double x1, double y1, double x2, double y2, BufferedImage layer, double brushSize) {
		drawLineFrom(x1,y1,x2,y2,layer,brushSize,erasedColor);
	}

	public void drawLineFrom(double x1, double y1, double x2, double y2, BufferedImage layer, double brushSize, Color brushColor) {

		double distanceBetween = getDistance(x1,y1,x2,y2)-brushSize;
		double dotDivider = 1; // line bellow fixes single line breaks...
		if (brushSize <= 3) dotDivider = 0.1; // PLEASE FIX pls optimise - super janky

		int dotCount = (int)(distanceBetween/dotDivider);
		double angle = -Math.atan2(x1 - x2, y1 - y2)+Math.PI/2*3;

		for (int i = 1; i < dotCount+1; i++) {
			double lineX = x1+Math.cos(angle)*(dotDivider*i);
			double lineY = y1+Math.sin(angle)*(dotDivider*i);
			drawAt(lineX,lineY,layer,brushSize,brushColor);
		}


	}

	public double findAngle(double x1,double y1,double x2,double y2) {
		double angle = -Math.toDegrees(Math.atan2(x2 - x1, y2 - y1))+90;
		return angle;
	}

	public void drawAt(double atX, double atY, BufferedImage layer, double brushSize, Color brushColor) {
		// POORLY OPTIMISED!!! laggy, my man, please fix this garbo
		if (brushSize < 2) { // OH LORD REFACTOR PLEEAAASE PLEASE pls
			atX = (int)atX;
			atY = (int)atY;
		}

		int minX = Math.max(0,(int)(atX-brushSize)-1);
		int maxX = Math.min(layer.getWidth(),(int)(atX+brushSize)+1);

		int minY = Math.max(0,(int)(atY-brushSize)-1);
		int maxY = Math.min(layer.getHeight(),(int)(atY+brushSize)+1);

		for (int row = minX; row < maxX; row++) {
			for (int col = minY; col < maxY; col++) {
				if (getDistance(atX,atY,row,col) <= brushSize) {
					layers.get(selectedLayer).getImage().setRGB(row, col, brushColor.getRGB());
				}
			}
		}
		updateFrom((int)(atX-brushSize-1),(int)(atY-brushSize-1),(int)(atX+brushSize+1),(int)(atY+brushSize+1));
	}

	public double getDistance(double fromX, double fromY, double toX, double toY) {

		return Math.sqrt((Math.pow((toX-fromX),2)+Math.pow((toY-fromY),2)));

	}

	public void tick() {
		// useless so far
	}


	public void draw(Graphics g) {

		displayX += (x-displayX)/displayDivider;
		displayY += (y-displayY)/displayDivider;
		displayWidth += (width-displayWidth)/displayDivider;
		displayHeight += (height-displayHeight)/displayDivider;

		// draw image //idk what null is for

		int cornerX = (int)(displayX-displayWidth/2);
		int cornerY =(int)(displayY-displayHeight/2);

		g.setColor(backgroundColor);
		g.fillRect(cornerX,cornerY,(int)displayWidth,(int)displayHeight);

		/*for (int i = 0; i < layers.size(); i++) {
            g.drawImage(layers.get(i), cornerX,cornerY,(int)displayWidth,(int)displayHeight,null);
        }*/
		g.drawImage(displayLayer,cornerX,cornerY,(int)displayWidth,(int)displayHeight,null);

		if (project.isSelecting() || project.hasSelected()) {
			drawSelectDarkness(g);
		}

	}

	public void drawSelectDarkness(Graphics g) {
		g.setColor(new Color(125,125,255,100));

		int cornerX = (int)(displayX-displayWidth/2);
		int cornerY =(int)(displayY-displayHeight/2);

		int startX = (int)(displayWidth * ((double)rectSelectStart.x/xPixel));
		int endX = (int)(displayWidth * ((double)rectSelectEnd.x/xPixel));
		int startY = (int)(displayHeight * ((double)rectSelectStart.y/yPixel));
		int endY = (int)(displayHeight * ((double)rectSelectEnd.y/yPixel));

		g.fillRect(cornerX, cornerY, (int)displayWidth, startY);
		g.fillRect(cornerX, cornerY+startY, startX, (int)displayHeight-(startY+(int)displayHeight-endY));
		g.fillRect(cornerX+endX, cornerY+startY, (int)displayWidth-endX, (int)displayHeight-(startY+(int)displayHeight-endY));
		g.fillRect(cornerX, cornerY+endY, (int)displayWidth, (int)displayHeight-endY);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public int getProgramWidth() {
		return programWidth;
	}

	public void setProgramWidth(int programWidth) {
		this.programWidth = programWidth;
	}

	public int getProgramHeight() {
		return programHeight;
	}

	public void setProgramHeight(int programHeight) {
		this.programHeight = programHeight;
	}

	public int getCanvasDivider() {
		return canvasDivider;
	}

	public void setCanvasDivider(int canvasDivider) {
		this.canvasDivider = canvasDivider;
	}

	public int getxPixel() {
		return xPixel;
	}

	public void setxPixel(int xPixel) {
		this.xPixel = xPixel;
	}

	public int getyPixel() {
		return yPixel;
	}

	public void setyPixel(int yPixel) {
		this.yPixel = yPixel;
	}

	public ArrayList<Layer> getLayers() {
		return layers;
	}

	public void setLayers(ArrayList<Layer> layers) {
		this.layers = layers;
	}

	public void setLayer(Layer layer, int index) {
		this.layers.add(index, layer);
		this.layers.remove(index+1);
	}

	public double getBrushX() {
		return brushX;
	}

	public void setBrushX(double brushX) {
		this.brushX = brushX;
	}

	public double getBrushY() {
		return brushY;
	}

	public void setBrushY(double brushY) {
		this.brushY = brushY;
	}

	public double getBrushCanvasX() {
		return brushCanvasX;
	}

	public void setBrushCanvasX(double brushCanvasX) {
		this.brushCanvasX = brushCanvasX;
	}

	public double getBrushCanvasY() {
		return brushCanvasY;
	}

	public void setBrushCanvasY(double brushCanvasY) {
		this.brushCanvasY = brushCanvasY;
	}

	public void setSelectedLayer(int selectedLayer) {
		this.selectedLayer = selectedLayer;
	}



}