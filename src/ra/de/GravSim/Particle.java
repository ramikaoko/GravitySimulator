package ra.de.GravSim;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/** TODO: particle description */
public class Particle {

	/** the mass of a defined particle */
	private double mass;

	/** the radius of a defined particle */
	private double radius;

	/** the location of a particle in the jframe */
	private Point2D location;

	/** the vector of a particle in the jframe */
	private Point2D vector = new Point2D.Double(0, 0);

	/*
	 * --- getter and setter ---
	 */
	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Point2D getVector() {
		return vector;
	}

	/*
	 * --- Particle calculations ---
	 */
	public void calculateVector(Point start, Point end) {
		double dx = (double) (end.getX() - start.getX());
		double dy = (double) (end.getY() - start.getY());
		dx /= 10;
		dy /= 10;
		this.vector = new Point2D.Double(dx, dy);
	}

	public double calculateRadius() {
		return Math.log(Math.E + mass / 10);
	}

	public void moveParticle(double times) {
		double nx = vector.getX() / times;
		double ny = vector.getY() / times;
		location.setLocation(location.getX() + nx, location.getY() + ny);
	}

	public Shape getShape() {
		Ellipse2D circle = new Ellipse2D.Double(location.getX() - radius, location.getY() - radius, 2 * radius,
				2 * radius);
		return circle;
	}

	/*
	 * --- Constructor ---
	 */
	public Particle(double mass, double x, double y) {

		this.mass = mass;
		this.radius = calculateRadius();
		this.location = new Point2D.Double(x, y);
	}

}
