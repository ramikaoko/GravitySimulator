package de.ra.simulation;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.vecmath.Vector2d;

/*
 * This class contains the list of all created particles and the principlies
 * applying to them, such as gravitational forces, collision detection and so on. 
 * It is observable by drawPane and will notify that class if changes occur.
 */
public class Universe extends Observable {

	/*
	 * the LinkedList which stores every particle in the universe, it has to be
	 * synchronized so multiple methods can access the list without a
	 * concurrentModificationException
	 */
	private final List<Particle> particleList = Collections.synchronizedList(new LinkedList<Particle>());

	/*
	 * the getter of our particleList creates is synchronized and creates a copy
	 * of the particleList<>, otherwise a concurrentModificationException occurs
	 * because the createParticle() and moveParticle() methods are trying to
	 * access simultaneously
	 */
	public synchronized LinkedList<Particle> getParticleList() {
		return new LinkedList<>(particleList);
	}

	/*
	 * the particleCounter is used to get a specific particle out of the
	 * particleList, this is useful for managing the list after a collison and
	 * other things. It is an AtomicInteger so we can automatically
	 * getAndIncrement it after each particle is created
	 */
	private AtomicInteger particleCounter = new AtomicInteger(0);

	/*
	 * the mass of a defined particle is set to 100 as the default value but it
	 * can be changed through user input in Controller()
	 */
	private double particleMass = 1000000d;

	/*
	 * analog to particleMass, the default value is 1 and can vary between 0,1
	 * and 15
	 */
	private double particleDensity = 1d;

	/*
	 * the size of the contentPane, it's used for collision detection with the
	 * borders of our panel
	 */
	private Dimension windowSize;

	/* TODO */
	private static boolean pauseFlag = false; // reaktionszeit von max 15ms

	/*
	 * --- getter and setter ---
	 */

	public double getParticleMass() {
		return particleMass;
	}

	public void setParticleMass(double particleMass) {
		this.particleMass = particleMass;
	}

	public double getParticleDensity() {
		return particleDensity;
	}

	public void setParticleDensity(double particleDensity) {
		this.particleDensity = particleDensity;
	}

	public void setWindowSize(Dimension dimension) {
		windowSize = dimension;
	}

	public boolean isPauseFlag() {
		return pauseFlag;
	}

	public void setPauseFlag(boolean pauseFlag) {
		Universe.pauseFlag = pauseFlag;
	}

	/*
	 * --- Constructor ---
	 */

	HashSet<CollisionFlag> collisionSet = new HashSet<>();

	/* TODO */
	public Universe() {
		Timer timer = new Timer(false);
		int period = 15;
		/*
		 * timeSteps will determine the calculation speed so it doesn't conflict
		 * with the time to draw the image, 1000d/15 ≈ 66fps
		 */
		double timeSteps = 1000d / period;
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {

				/* check for pause status, continue if its on false */
				if (isPauseFlag())
					return;

				List<Particle> particleListCopy = getParticleList();

				/* TODO */
				for (Particle particle : particleListCopy) {
					particle.moveParticle(timeSteps);
					bounceOffBorder(particle);
				}

				/* TODO */
				Particle particleOne;

				/* TODO */
				for (int i = 0; i < particleListCopy.size() - 1; i++) {
					particleOne = particleListCopy.get(i);
					for (int j = i + 1; j < particleListCopy.size(); j++) {
						final Particle particleTwo = particleListCopy.get(j);

						CollisionFlag collisionFlag = new CollisionFlag(particleOne, particleTwo);
						if (collisionSet.contains(collisionFlag)) {
							if (collisionFlag.stillColliding(Universe.this))
								continue;
							collisionSet.remove(collisionFlag);
						}

						if (checkForIntersection(particleOne, particleTwo)) {
							System.out.println("Intersection: true");
							if (checkForCollision(particleOne, particleTwo)) {
								System.out.println("Collision: true");
								elasticTwoDimensionalCollision(particleOne, particleTwo);
								collisionSet.add(collisionFlag);
							}
						}
					}
				}

				setChanged();
				notifyObservers();
			}
		}, 100, period);
	}

	/*
	 * --- Movement calculations ---
	 * 
	 * TODO: Methods for gravity, distance and collision
	 */

	/*
	 * if a particle collides with one of the 4 site of the panel, the according
	 * x or y value will be multiplied with -1 so the angle of incidence is
	 * equal to the angle of emergence
	 */
	protected void bounceOffBorder(Particle particle) {
		Rectangle2D bounds = particle.getHullShape().getBounds2D();
		if (bounds.getMinX() <= 0 || bounds.getMaxX() >= windowSize.getWidth()) {
			Vector2d vector = particle.getVector();
			particle.setVector(new Vector2d(vector.getX() * -1d, vector.getY()));
		} else if (bounds.getMinY() <= 0 || bounds.getMaxY() >= windowSize.getHeight()) {
			Vector2d vector = particle.getVector();
			particle.setVector(new Vector2d(vector.getX(), vector.getY() * -1d));
		}
	}

	/*
	 * using the AABB-collision check (axis-aligned bounding box) to check
	 * whether two particles are near each other.
	 */
	protected boolean checkForIntersection(Particle particleOne, Particle particleTwo) {
		Rectangle2D boundsOne = particleOne.getHullShape().getBounds2D();
		Rectangle2D boundsTwo = particleTwo.getHullShape().getBounds2D();
		if (boundsOne.intersects(boundsTwo)) {
			return true;
		}
		return false;
	}

	/*
	 * Using trigonometery, we can determine the distance between the two points
	 * and therefore the point of collision.
	 */
	protected boolean checkForCollision(Particle particleOne, Particle particleTwo) {

		/*
		 * Phytagorean theorem: a^2+b^2=c^2, so Math.sqrt(c^2) will give us the
		 * distance of the two particles
		 */
		double a = particleOne.getLocation().getX() - particleTwo.getLocation().getX();
		double b = particleOne.getLocation().getY() - particleTwo.getLocation().getY();
		double c = Math.sqrt((a * a) + (b * b));

		/*
		 * A collision occured if the sum of the particle radii is less than c
		 */
		if (c < (particleOne.getRadius() + particleTwo.getRadius())) {
			return true;
		}
		return false;
	}

	/*
	 * this method detects the collision point between two particles, this can
	 * be used to add particle effects and to localize the point at which new
	 * particles are created if the bigger one gets destroyed
	 */
	protected void detectCollisionPoint(Particle particleOne, Particle particleTwo) {

		double collisionPointX = (particleOne.getLocation().getX() * particleTwo.getRadius())
				+ (particleTwo.getLocation().getX() * particleOne.getRadius())
						/ (particleOne.getRadius() + particleTwo.getRadius());

		double collisionPointY = (particleOne.getLocation().getY() * particleTwo.getRadius())
				+ (particleTwo.getLocation().getY() * particleOne.getRadius())
						/ (particleOne.getRadius() + particleTwo.getRadius());
	}

	/*
	 * The actual collision handling with velocity/vector calculation is
	 * implemented in here. The formula for an elastic collision (which this
	 * will be for now) is:
	 * 
	 * v1' = 2*((m1*v1 + m2*v2)/(m1+m2))-v1 with as the velocity and m as the
	 * mass for the particles
	 * 
	 * each vector will be normalized to prevent strange behavior, e.g. gaining
	 * speed after bumping into each other
	 */
	protected void elasticTwoDimensionalCollision(Particle particleOne, Particle particleTwo) {

		Vector2d unitVector = new Vector2d(particleTwo.getLocation().getX() - particleOne.getLocation().getX(),
				particleTwo.getLocation().getY() - particleOne.getLocation().getY());

		unitVector.normalize();

		Vector2d tangentVector = new Vector2d(-unitVector.y, unitVector.x);

		double v1n = unitVector.dot(particleOne.getVector());
		double v1t = tangentVector.dot(particleOne.getVector());
		double v2n = unitVector.dot(particleTwo.getVector());
		double v2t = tangentVector.dot(particleTwo.getVector());

		double v1nPrime = (v1n * (particleOne.getMass() - particleTwo.getMass()) + 2. * particleTwo.getMass() * v2n)
				/ (particleOne.getMass() + particleTwo.getMass());
		double v2nPrime = (v2n * (particleTwo.getMass() - particleOne.getMass()) + 2. * particleOne.getMass() * v1n)
				/ (particleOne.getMass() + particleTwo.getMass());

		Vector2d v_v1nPrime = new Vector2d(unitVector.x * v1nPrime, unitVector.y * v1nPrime);
		Vector2d v_v1tPrime = new Vector2d(tangentVector.x * v1t, tangentVector.y * v1t);
		Vector2d v_v2nPrime = new Vector2d(unitVector.x * v2nPrime, unitVector.y * v2nPrime);
		Vector2d v_v2tPrime = new Vector2d(tangentVector.x * v2t, tangentVector.y * v2t);

		Vector2d n1 = new Vector2d(v_v1nPrime.x + v_v1tPrime.x, v_v1nPrime.y + v_v1tPrime.y);
		Vector2d n2 = new Vector2d(v_v2nPrime.x + v_v2tPrime.x, v_v2nPrime.y + v_v2tPrime.y);
		particleOne.setVector(n1);
		particleTwo.setVector(n2);

	}

	/* TODO */
	protected Vector2d rotate(Vector2d v, double theta) {
		Point2D point = new Point2D.Double(v.x, v.y);
		AffineTransform.getRotateInstance(theta, 0, 0).transform(point, point);
		v.x = point.getX();
		v.y = point.getY();
		return v;
	}

	protected void particleFission(Particle particleOne, Particle particleTwo) {

	}

	/*
	 * --- Particle handling ---
	 */
	public Particle createParticle(double x, double y) {
		particleCounter.getAndIncrement();
		Particle particle = new Particle(getParticleMass(), getParticleDensity(), x, y);
		synchronized (particleList) {
			particleList.add(particle);
		}
		return particle;
	}

	/*
	 * delete the content of the particleList so we can start with a fresh
	 * contentPane without drawings
	 */
	public void clearParticles() {
		synchronized (particleList) {
			particleList.clear();
		}
	}
}