import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ColorWheel {

	private double x = 0;
	private double y = 0;
	private double width = 100;
	private double height = 100;
	
	private double mouseOnX = 0;
	private double mouseOnY = height;
	
	private BufferedImage wheel;

	
	private boolean clicked = false;
		
	private Color color = Color.RED;
	private Color leftColor = Color.WHITE;
	private Color bottomColor = Color.BLACK;
	
	private Color selectedColor = color;

	
	public ColorWheel() {
		
		wheel = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_ARGB);
		createWheel();
		
	}
	
	public ColorWheel(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		wheel = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_ARGB);
		createWheel();
	}

	public void draw(Graphics g) {
        g.drawImage(wheel,(int)(x-width/2),(int)(y-height/2),(int)width,(int)height,null);
        g.setColor(Color.LIGHT_GRAY);
        int pickerSize = 4;
        g.drawRect((int)(x-width/2+mouseOnX-pickerSize/2),(int)(y-height/2+mouseOnY-pickerSize/2),pickerSize,pickerSize);
	}
	
	public void createWheel() {
		
		for (int row = 0; row < wheel.getWidth(); row++) {
			for (int col = 0; col < wheel.getHeight(); col++) {
				
				wheel.setRGB(row,col,findColor(row,col).getRGB());
				
			}
		}
		
	}
	
	public Color findColor(double xPixel, double yPixel) {
		
		double xCount = xPixel/(wheel.getWidth()); //subtract 1?
		double yCount = yPixel/(wheel.getHeight());
				
		int red = (int)((1-yCount)*(xCount*color.getRed()+(1-xCount)*leftColor.getRed())+yCount*bottomColor.getRed());
		int green = (int)((1-yCount)*(xCount*color.getGreen()+(1-xCount)*leftColor.getGreen())+yCount*bottomColor.getGreen());
		int blue = (int)((1-yCount)*(xCount*color.getBlue()+(1-xCount)*leftColor.getBlue())+yCount*bottomColor.getBlue());
		
		//red = Math.min(Math.max(red,0),255);
		//green = Math.min(Math.max(green,0),255);
		//blue = Math.min(Math.max(blue,0),255);

		return new Color(red,green,blue);
	}
	
	public void tick(double mouseX, double mouseY) {
        
        if (clicked) {
            
            toMouse(mouseX, mouseY);
            changeColor();
            
        }
         
    }
	
	public void changeColor() {
		selectColor();
        Program.brushColor = selectedColor;
	}
	
	public void selectColor() {
		selectedColor = findColor(mouseOnX,mouseOnY);
	}
	
	public void toMouse(double mouseX, double mouseY) {
		
		mouseOnX = Math.max(Math.min((mouseX-(x-width/2)),wheel.getWidth()),0);
		mouseOnY = Math.max(Math.min((mouseY-(y-height/2)),wheel.getHeight()),0);
		
	}
	
	public void checkClick(double mouseX, double mouseY) {
        
        if (mouseX >= x-width/2 && mouseX <= x+width/2) {
            if (mouseY >= y-height/2 && mouseY <= y+height/2) {
                
            	clicked = true;
                Program.drawing = false;
            	
            }
        }
    }
	
	public void removeClick() {
        clicked = false;
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

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isClicked() {
		return clicked;
	}

	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}
	
}
