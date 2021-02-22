package Images;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Obstacle extends Image {
	
	public Obstacle( String fileName ){
		super(fileName);
	}
	
	public Obstacle( String fileName, int x, int y){
		super( fileName, x, y);
	}
	
	public void draw( Graphics2D g ){
		
		g.drawImage(this.getImage(), this.getX(), this.getY(), null);
		
	}

	@Override
	public void move() {
		this.setX( this.getX()-1 );
	}

	public Rectangle getRectangle() {
		return new Rectangle( this.getX(), this.getY(),
				  			  this.getWidth(), this.getHeight()
				  			);
	}
	
}
