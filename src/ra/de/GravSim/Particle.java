package ra.de.GravSim;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Particle {

	private int mass;
	private double radius;

	/** indicate whether a particle collided with another one */
	private boolean collisionFlag;

	private Point2D.Double location;
	private Point2D.Double vector;

	public Particle(int mass, int x, int y, int vx, int vy) {

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
