package ra.de.GravSim;

import java.awt.Point;
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
	private Point2D.Float vector = new Point2D.Float(0, 0);

	/** a unique, human readable id which starts with 0 */
	private int id;

	public Point2D.Float getVector() {
		return vector;
	}

	public void setVector(Point start, Point end) {
		double dx = end.getX() - start.getX();
		double dy = end.getY() - start.getY();
		dx /= 100;
		dy /= 100;
		vector.setLocation(dx, dy);
		System.out.println(this.vector);
	}

	public Shape getShape() {
		Ellipse2D circle = new Ellipse2D.Float(location.x - radius, location.y - radius, 2 * radius, 2 * radius);
		return circle;
	}

	public void move() {
		location.setLocation(location.getX() + vector.getX(), location.getY() + vector.getY());
	}

	public Particle(float mass, float x, float y, int id) {

		this.mass = mass;
		this.radius = (float) Math.log(Math.E + mass / 1000);
		this.location = new Point2D.Float(x, y);
		this.id = id;
	}

}
