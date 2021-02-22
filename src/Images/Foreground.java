package Images;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Foreground extends Image {

	public Foreground(String fileName) {
		super(fileName);
	}

	public Foreground(String fileName, int x, int y) {
		super(fileName, x, y);
	}
	
	public void draw( Graphics2D g ){
		
		g.drawImage(this.getImage(), this.getX(), this.getY(), null);
		
	}
	
	public void move() {
		this.setX( this.getX()-1 );
		if( this.getX() < -this.getImage().getWidth() ){
			this.setX( this.getImage().getWidth() -1 );
		}
	}

	public Rectangle getRectangle() {
		return new Rectangle( this.getX(), this.getY(),
							  this.getWidth(), this.getHeight()
							);
	}

}
