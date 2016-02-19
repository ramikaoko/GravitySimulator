package ra.de.GravSim;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/** TODO: particle description */
public class Particle {

	/** the mass of a defined particle */
	private Double mass;

	/** the radius of a defined particle */
	private Double radius;

	/** the location of a particle in the jframe */
	private Point2D location;

	/** the vector of a particle in the jframe */
	private Point2D vector = new Point2D.Double(0, 0);

	/*
	 * --- getter and setter ---
	 */
	public Double getRadius() {
		return radius;
	}

	public void setRadius(Double radius) {
		this.radius = radius;
	}

	public Double getMass() {
		return mass;
	}

	public void setMass(Double mass) {
		this.mass = mass;
	}

	public Point2D getVector() {
		return vector;
	}

	/*
	 * --- calculations ---
	 */
	public void calculateVector(Point start, Point end) {
		Double dx = (Double) (end.getX() - start.getX());
		Double dy = (Double) (end.getY() - start.getY());
		dx /= 10;
		dy /= 10;
		this.vector = new Point2D.Double(dx, dy);
	}

	public Double calculateRadius() {
		return Math.log(Math.E + mass / 1000);
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
	public Particle(Double mass, Double x, Double y) {

		this.mass = mass;
		this.radius = calculateRadius();
		this.location = new Point2D.Double(x, y);
	}

}
