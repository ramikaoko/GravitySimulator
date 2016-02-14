package ra.de.GravSim;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Particle {

	/** the mass of a defined particle */
	private int mass;
	
	/** the radius of a defined particle */
	private double radius;

	/** indicate whether a particle collided with another one */
	private boolean collisionFlag;
	
	/** the location of a particle in the jframe */
	private Point2D.Float location;
	
	/** the vector of a particle in the jframe */
	private Point2D.Float vector;

	public Particle(int mass, float x, float y) {

		this.mass = mass;
		this.collisionFlag = false;
		this.radius = Math.log(Math.E + mass / 1000);
	}

	public Particle() {

	}

	public Shape getShape() {

		Ellipse2D circle = new Ellipse2D.Double(location.x - radius, location.y - radius, 2 * radius, 2 * radius);
		return circle;
	}

}
