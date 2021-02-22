package Images;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Bird extends Image {
	
	public double falling = 0.15;

	public Bird(String fileName) {
		super(fileName);
	}

	public Bird(String fileName, int x, int y) {
		super(fileName, x, y);
	}
	
	public void draw( Graphics2D g ){
		
		g.drawImage(this.getImage(),this.getX(), this.getY(), null);
		
	}
	
	public boolean collide(Image image){
		Rectangle r1 = getRectangle();
		Rectangle r2 = image.getRectangle();
		if(r1.intersects(r2)){
			return false;
		}
		
		return true;
	}
	
	public Rectangle getRectangle(){
		return new Rectangle( this.getX()+5, this.getY() + 5, 
							  this.getWidth() - 10, this.getHeight()
							);
	}
	
	public void move() {
		this.setY( this.getY() + (int)(falling += 0.058) );
		
		if(this.getY() < -this.getHeight()){
			this.setY(-this.getHeight());
		}
	}
	
	public double getFalling(){
		return falling;
	}
	
	public void setFalling(double speed){
		falling = speed;
	}
	
}