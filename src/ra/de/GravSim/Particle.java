package ra.de.GravSim;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/* TODO: particle description */
public class Particle {

	/* the mass of a defined particle */
	private double mass;

	private double density;

	/* the radius of a defined particle */
	private double radius;

	/* the location of a particle in the jframe */
	private Point2D location;

	/* the vector of a particle in the jframe */
	private Point2D vector = new Point2D.Double(0, 0);

	/*
	 * --- getter and setter ---
	 */
	public double getRadius() {
		return radius;
	}

	public double getMass() {
		return mass;
	}

	public double getDensity() {
		return density;
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
		return Math.log(Math.E + mass / density);
	}

	public void moveParticle(double times) {
		double nx = vector.getX() / times;
		double ny = vector.getY() / times;
		location.setLocation(location.getX() + nx, location.getY() + ny);
	}

	public Shape getHullShape() {
		Ellipse2D circle = new Ellipse2D.Double(location.getX() - radius, location.getY() - radius, 2 * radius,
				2 * radius);
		return circle;
	}

	public Shape getCoreShape() {
		double r = radius * 0.5d;
		Ellipse2D circle = new Ellipse2D.Double(location.getX() - r, location.getY() - r, 2 * r, 2 * r);
		return circle;
	}

	/*
	 * --- Constructor ---
	 */
	public Particle(double mass, double density, double x, double y) {

		this.mass = mass;
		this.density = density;
		this.radius = calculateRadius();
		this.location = new Point2D.Double(x, y);
	}

}
