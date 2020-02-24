import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class FileTab {


	private double x;
	private double y;

	private double toX;
	private double toY;

	private double width, height;

	private float picScaleX, picScaleY;
	
	private double toWidth;
	private double toHeight;

	private boolean hovered = false;
	
	//private double imageSize;

	private BufferedImage image;
	private File file;

	private Program program;

	private Color tabColor = new Color(45,45,45);

	private Font font;
	private String name;
	private String displayName;
	
	//private double angle = Math.random()*Math.PI*2;

	public FileTab(Program program, File file) {

		this.program = program;

		this.file = file;
		this.name = file.getName();

		if (hasExtension("png")) {
			readPNG();
		} else if (hasExtension("art")) {
			readArtFile();
		}

		findImageDimensions();
	}
	
	public void findImageDimensions() {
		
		if (image.getWidth() > image.getHeight()) {
			picScaleX = 1;
			picScaleY = (float) image.getHeight() / image.getWidth();
		} else {
			picScaleX = (float) image.getWidth() / image.getHeight();
			picScaleY = 1;
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

	public boolean hasExtension(String extension) {

		if (getExtension(file).equalsIgnoreCase(extension)) { // perhaps add trim?
			return true;
		}

		return false;
	}
	
	public void readPNG() {

		try {
			this.image = ImageIO.read( file );
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void readArtFile() {

		Project oldProject = program.getSelectedProject();
		
		program.createProject();

		Canvas canvas = program.getCanvas();

		program.deleteUndos();

		ArrayList<Layer> layers = canvas.getLayers();

		// remove all layers
		int removeCount = layers.size();
		for (int i = 0; i < removeCount; i++) {
			layers.remove(0);
		}

		try {
			File savedImage = file;
			Scanner sc = new Scanner(savedImage);

			//int layerIndex = 0;
			//int iteration = 1;

			int layerCount = Integer.parseInt(sc.next());
			int width = Integer.parseInt(sc.next());
			int height = Integer.parseInt(sc.next());

			int pixelColor = 0;
			int pixelCount = 0;

			canvas.setxPixel(width);
			canvas.setyPixel(height);
			canvas.findSize();

			//while(sc.hasNext()) {
			for (int i = 0; i < layerCount; i++) {	
				System.out.println("hi: " + i); // test

				BufferedImage layerImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

				int layerVisible = Integer.parseInt(sc.next().substring(1));

				for (int row = 0; row < width; row++) {
					for (int col = 0; col < height; col++) {
						if (pixelCount <= 0) {
							pixelColor = Integer.parseInt(sc.next());
							pixelCount = Integer.parseInt(sc.next().substring(1));
						}
						layerImage.setRGB(row,col,pixelColor);
						pixelCount--;
					}
				}

				Layer layer = new Layer(layerImage,i,program); //LAYCHECK

				if (layerVisible == 1) {
					layer.setVisible(true);
				} else {
					layer.setVisible(false);
				}

				layers.add(layer);
				//addLayerButton();
			}



			sc.close();
			canvas.updateDisplay();
			image = canvas.singleLayer();

		} catch (FileNotFoundException e) {
			System.out.println("no " + file.getName() +" found... -FILETAB");
		}
		
		program.getProjects().remove(program.getProjects().size()-1);
		program.setSelectedProject(oldProject);
	}

	public void draw(Graphics g) {

		g.setColor(tabColor);

		g.fillRect((int)(x-width/2), (int)(y-height/2), (int)width, (int)height);

		g.setColor(Color.LIGHT_GRAY);

		g.drawRect((int)(x-width/2), (int)(y-height/2), (int)width, (int)height);

		double imageMult = 0.67;
		g.fillRect((int)(x-width/2*imageMult*picScaleX), (int)(y-height/2*0.8*picScaleY), (int)(width*imageMult*picScaleX), (int)(height*imageMult*picScaleY));
		g.drawImage(image, (int)(x-width/2*imageMult*picScaleX), (int)(y-height/2*0.8*picScaleY), (int)(width*imageMult*picScaleX), (int)(height*imageMult*picScaleY), null);

		g.setFont(font);

		if (displayName == null) { // find display name

			for (int i = 0; i < name.length(); i++) { // POLISH PLEASE
				if (g.getFontMetrics().stringWidth(name.substring(0, i+1)) > (width*0.8) ) {
					displayName = name.substring(0, i-3) + "..."; //can crash if tooo short
					break;
				}
			}

			if (displayName == null) displayName = name;
		}

		g.drawString(displayName, (int)(x-width/2*0.8), (int)(y+height/2*0.8));
	}

	//double angleChange = 0.5*Math.random();
	//double speed = 5*Math.random();
	
	public void tick(double scroll) {
		
		//angle += Math.random() * angleChange*2 - angleChange;
		//toX += Math.cos(angle) * speed;
		//toY += Math.sin(angle) * speed;
		
		int divide = 10;

		if (hovered) {
			width /= 1.1;
			height /= 1.1;
		}
		
		x += (toX - x)/divide;
		y += ((toY - scroll) - y)/divide;
		width += (toWidth - width)/divide;
		font = new Font("Impact", Font.PLAIN, (int)(width/10.5) );

		height += (toHeight - height)/divide;
		
		if (hovered) {
			width *= 1.1;
			height *= 1.1;
		}
	}

	public void ifHover(double mouseX, double mouseY) {
		
		hovered = false;
		
		if (mouseX >= x-width/2 && mouseX <= x+width/2) {
			if (mouseY >= y-height/2 && mouseY <= y+height/2) {
				
				hovered = true;
				
			}
		}
	}
	
	public void checkClick(double mouseX, double mouseY) {

		if (mouseX >= x-width/2 && mouseX <= x+width/2) {
			if (mouseY >= y-height/2 && mouseY <= y+height/2) {

				program.setToProgramMode();
				if (hasExtension("png")) {
					program.readPNG( file );
				} else if (hasExtension("art")) {
					program.readFile( file );
				}
			}
		}
	}

	public double getY() {
		return this.y;
	}
	
	public void setDimensions(double x, double y, double width, double height) {

		this.toX = x;
		this.toY = y;
		this.toWidth = width;
		this.toHeight = height;

		if (font == null) this.x = x; // if first time, will set x and y
		if (font == null) this.y = y;
		if (font == null) this.width = width;
		if (font == null) this.height = height;

		//this.imageSize = width * 0.75;

		this.font = new Font("Impact", Font.PLAIN, (int)(width/10.5) );
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getToX() {
		return toX;
	}

	public void setToX(double toX) {
		this.toX = toX;
	}

	public double getToY() {
		return toY;
	}

	public void setToY(double toY) {
		this.toY = toY;
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

	public double getToWidth() {
		return toWidth;
	}

	public void setToWidth(double toWidth) {
		this.toWidth = toWidth;
	}

	public double getToHeight() {
		return toHeight;
	}

	public void setToHeight(double toHeight) {
		this.toHeight = toHeight;
	}

	public boolean isHovered() {
		return hovered;
	}

	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public Color getTabColor() {
		return tabColor;
	}

	public void setTabColor(Color tabColor) {
		this.tabColor = tabColor;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	
	
	
	
}
