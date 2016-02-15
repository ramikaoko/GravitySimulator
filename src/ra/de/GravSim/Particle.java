package ra.de.GravSim;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Particle {

	/** the mass of a defined particle */
	private float mass;

	/** the radius of a defined particle */
	private float radius;

	/** the location of a particle in the jframe */
	private Point2D.Float location;

	/** the vector of a particle in the jframe */
	private Point2D.Float vector;

	public Particle(float mass, float x, float y) {

		this.mass = mass;
		this.radius = (float) Math.log(Math.E + mass / 1000);
		this.location = new Point2D.Float(x, y);
	}

	public Shape getShape() {

		Ellipse2D circle = new Ellipse2D.Float(location.x - radius, location.y - radius, 2 * radius, 2 * radius);
		return circle;
	}

}
