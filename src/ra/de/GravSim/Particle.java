package ra.de.GravSim;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

/* TODO: particle description */
public class Particle {

	/* the mass of a particle */
	private double mass;

	/* the density of a particle */
	private double density;

	/* the radius of a particle */
	private double radius;

	/* the location of a particle in the jframe */
	private Point2D location;

	/* the vector of a particle in the jframe */
	private Vector2d vector = new Vector2d();

	private double velocity;

	/*
	 * --- getter and setter ---
	 */
	public double getMass() {
		return mass;
	}

	public double getDensity() {
		return density;
	}

	public double getRadius() {
		return radius;
	}

	public Vector2d getVector() {
		return new Vector2d(vector);
	}

	public Point2D getLocation() {
		return location;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public void setVector(Vector2d vector) {

		this.vector.set(vector);
		this.vector.normalize();
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

	/*
	 * --- Particle calculations ---
	 */

	/* TODO */
	public void calculateVector(Point start, Point end) {
		double dx = (double) (end.getX() - start.getX());
		double dy = (double) (end.getY() - start.getY());
		dx /= 10;
		dy /= 10;
		this.vector = new Vector2d(dx, dy);
		velocity = vector.length();
		vector.normalize();
	}

	/* TODO */
	public double calculateRadius() {
		return Math.log(Math.E + mass / Math.pow(2, density));
	}

	/* TODO */
	public void moveParticle(double timeSteps) {
		double nx = vector.getX() * velocity / timeSteps;
		double ny = vector.getY() * velocity / timeSteps;
		location.setLocation(location.getX() + nx, location.getY() + ny);
	}

	/* TODO */
	public Shape getHullShape() {
		Ellipse2D circle = new Ellipse2D.Double(location.getX() - radius, location.getY() - radius, 2 * radius,
				2 * radius);
		return circle;
	}

	/* TODO */
	public Shape getCoreShape() {
		double coreRadius = radius * 0.5d;
		Ellipse2D circle = new Ellipse2D.Double(location.getX() - coreRadius, location.getY() - coreRadius,
				2 * coreRadius, 2 * coreRadius);
		return circle;
	}

}
