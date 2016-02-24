package de.ra.simulation;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
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

	private boolean pauseFlag;

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

	/*
	 * --- Constructor ---
	 */

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
				List<Particle> particleListCopy = getParticleList();

				for (Particle particle : particleListCopy) {
					particle.moveParticle(timeSteps);
					bounceOffBorder(particle);
				}

				Particle particle;
				for (int i = 0; i < particleListCopy.size() - 1; i++) {
					particle = particleListCopy.get(i);
					for (int j = i + 1; j < particleListCopy.size(); j++) {
						checkForCollision(particle, particleListCopy.get(j));
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
	protected void checkForIntersection(Particle particleOne, Particle particleTwo) {
		Rectangle2D boundsOne = particleOne.getHullShape().getBounds2D();
		Rectangle2D boundsTwo = particleTwo.getHullShape().getBounds2D();
		if (boundsOne.intersects(boundsTwo)) {

		}
	}

	/*
	 * Using trigonometery, we can determine the distance between the two points
	 * and therefore the point of collision.
	 */
	protected void checkForCollision(Particle particleOne, Particle particleTwo) {

		/*
		 * Phytagorean theorem: a^2+b^2=c^2, so Math.sqrt(c^2) will give us the
		 * distance of the two particles
		 */
		double a = particleOne.getLocation().getX() - particleTwo.getLocation().getX();
		double b = particleOne.getLocation().getY() - particleTwo.getLocation().getY();
		double c = Math.sqrt((a * a) + (b * b));

		/*
		 * A collision occured if the radii of both particles added together is
		 * smaller than the value of c
		 */
		if (c <= (particleOne.getRadius() + particleTwo.getRadius())) {
			elasticTwoDimensionalCollision(particleOne, particleTwo);
		}
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
		/* the angle at which the two particles collide */
		double vectorAngleAlpha;

		/*
		 * create a new Vector2D Object out or our Point2D-particleVectors so we
		 * can use all those fancy methods from the vecmath library
		 */
		Vector2d vectorOne = new Vector2d(particleOne.getVector().getX(), particleOne.getVector().getY());
		vectorOne.normalize();
		Vector2d vectorTwo = new Vector2d(particleTwo.getVector().getX(), particleTwo.getVector().getY());
		vectorTwo.normalize();

		/* the vector between the central points of both particles... */
		Vector2d vectorCentral = new Vector2d(vectorOne.getX() - vectorTwo.getX(), vectorOne.getY() - vectorTwo.getY());
		vectorCentral.normalize();

		/*
		 * ...and the vector which stays right-angled to vectorCentral. This
		 * will actually be two vector which are pointing towards opposite
		 * directions. To get vectorOrthogonalOne you have to switch the x and y
		 * coordinates and multiply one of those by -1. To get number two you
		 * have to do the same with the other point.
		 * 
		 * e.g. one = (vC.y , vC.x*-1) & two = (vC.y*-1 , vC.x)
		 */
		Vector2d vectorOrthogonalOne = new Vector2d(vectorCentral.getY(), -vectorCentral.getX());
		vectorOrthogonalOne.normalize();
		Vector2d vectorOrthogonalTwo = new Vector2d(-vectorCentral.getY(), vectorCentral.getX());
		vectorOrthogonalTwo.normalize();

		/* calculate the two angles between vectorCentral and vectorOrhogonal */
		vectorAngleAlpha = vectorOne.angle(vectorTwo);

		System.out.println("Winkel Alpha: " + vectorAngleAlpha + " - " + Math.toDegrees(vectorAngleAlpha));

		if (vectorAngleAlpha < Math.PI / 2) {
			rotate(vectorOrthogonalOne, vectorAngleAlpha);
			rotate(vectorOrthogonalTwo, vectorAngleAlpha);
		} else if (vectorAngleAlpha > Math.PI / 2) {
			rotate(vectorOrthogonalOne, Math.PI - vectorAngleAlpha);
			rotate(vectorOrthogonalTwo, Math.PI - vectorAngleAlpha);
		}

		/*
		 * set the vector for both particles to the jsut calculated central and
		 * orthogonal vectors
		 */
		particleOne.setVector(vectorOrthogonalTwo);
		particleTwo.setVector(vectorOrthogonalOne);

	}

	public Vector2d rotate(Vector2d v, double theta) {
		Point2D point = new Point2D.Double(v.x, v.y);
		AffineTransform.getRotateInstance(theta, 0, 0).transform(point, point);
		v.x = point.getX();
		v.y = point.getY();
		return v;
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

	public void clearParticles() {
		synchronized (particleList) {
			particleList.clear();
		}
	}

	public void pauseParticleMovement() {
		if (pauseFlag == false)
			for (Particle particle : particleList) {
				double tempVelocity;
				tempVelocity = particle.getVelocity();
				particle.setVelocity(0);
			}
		else {
			for (Particle particle : particleList) {
				double tempVelocity;
				tempVelocity = particle.getVelocity();
				particle.setVelocity(0);
			}
		}

	}
}
