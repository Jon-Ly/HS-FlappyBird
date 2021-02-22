package Images;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class Image {

	private int x;// X position of Image
	private int y;// Y position of Image
	private String fileName;// file name for the image
	private BufferedImage image;// every 'Image' object is a BufferedImage
	
	public Image( String fileName ){//only using a file name
		
		this.fileName = fileName;
		try {
			this.image = ImageIO.read(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.print("File Not Found");
			System.exit(0);
		}
		
	}
	
	public Image( String fileName, int x, int y ){//using a file name with its X and Y
		
		this.fileName = fileName;
		this.x = x;
		this.y = y;
		try{
			this.image = ImageIO.read(new File(fileName));
		}catch (IOException e){
			e.printStackTrace();
			System.out.print("File Not Found");
		}
		
	}
	
	//abstract method(s)
	public abstract void move();
	public abstract Rectangle getRectangle();//needed to check collisions
	public abstract void draw(Graphics2D g);
	
	//getters and setters
	public BufferedImage getImage(){
		return this.image;
	}
	
	public void setImage(BufferedImage image){
		this.image = image;
	}
	
	public int getWidth(){
		return this.image.getWidth();
	}
	
	public int getHeight(){
		return this.image.getHeight();
	}
	
	public int getX(){
		return this.x;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public String getFileName(){
		return this.fileName;
	}
}
