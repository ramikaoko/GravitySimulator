package de.ra.simulation;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.concurrent.atomic.AtomicInteger;

import javax.vecmath.Vector2d;

/* TODO: particle description */
public class Particle {

	/*
	 * the particleCounter is used to get a specific particle out of the
	 * particleList, this is useful for managing the list after a collison and
	 * other things. It is an AtomicInteger so we can automatically
	 * getAndIncrement it after each particle is created
	 */
	private static AtomicInteger COUNTER = new AtomicInteger(0);

	/* TODO */
	private final int id;

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

	/*
	 * the velocity of a particle is determined by the distance of the start and
	 * end point of the mouse movement in MainFrame()
	 */
	private double velocity;

	/* a multiplier for velocity so the user can change the it if he wants to */
	private int velocityMultiplier;

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

	public int getVelocityMultiplier() {
		return velocityMultiplier;
	}

	public void setVelocityMultiplier(int velocityMultiplier) {
		this.velocityMultiplier = velocityMultiplier;
	}

	public int getId() {
		return id;
	}

	/* TODO */
	private static int getNewId() {
		synchronized (COUNTER) {
			int newID = COUNTER.incrementAndGet();
			return newID;
		}
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
		id = getNewId();
	}

	/*
	 * --- Particle calculations ---
	 */
	/* TODO */
	public void calculateVector(Point start, Point end) {
		double dx = (double) (end.getX() - start.getX());
		double dy = (double) (end.getY() - start.getY());
		dx *= velocityMultiplier;
		dy *= velocityMultiplier;
		this.vector = new Vector2d(dx, dy);
		velocity = vector.length();
		vector.normalize();
	}

	/*
	 * we calculate the radius with the Math.log() method to get different sizes
	 * for particles without those radii getting to big, otherwise they would
	 * fill the whole panel. The density let the radius shrink exponential to
	 * the base 2 (the range is from 2^0,1 = 1,07 to 2^15 = 32768). A heavy
	 * particle with a high density is small, a light particle with a high
	 * density is tiny.
	 */
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

	@Override
	public String toString() {
		return id + "";
	}

}
