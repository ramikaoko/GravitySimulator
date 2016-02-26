package de.ra.simulation;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

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

	/*
	 * This flag indicates whether the simulation is on halt or not, it is set
	 * in Controller() by the pauseButton
	 */
	private static boolean pauseFlag = false;

	/* TODO */
	TreeSet<CollisionFlag> collisionSet = new TreeSet<>();

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

	public void setWindowSize(Dimension windowSize) {
		this.windowSize = windowSize;
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

	/* TODO */
	public Universe() {
		Timer timer = new Timer(false);

		int period = 4;
		/*
		 * timeSteps will determine the calculation speed so it doesn't conflict
		 * with the time to draw the image, 1000d/4 ≈ 250 steps per cycle
		 */
		double timeSteps = 1000d / period;
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {

				/* check for pause status, continue if its on false */
				if (isPauseFlag())
					return;

				List<Particle> particleListCopy = getParticleList();

				/*
				 * we got through the whole particleList and check the movement
				 * for each particle, also we check whether the particle
				 * collided with the border
				 */
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

						/* TODO */
						final Particle particleTwo = particleListCopy.get(j);

						/* TODO */
						CollisionFlag collisionFlag = new CollisionFlag(particleOne, particleTwo);

						if (collisionSet.contains(collisionFlag)) {
							if (collisionFlag.stillColliding(Universe.this))
								continue;
							System.out.println("before: " + collisionSet.size());
							collisionSet.remove(collisionFlag);
							System.out.println("after: " + collisionSet.size());
						}

						// if (checkForIntersection(particleOne, particleTwo)) {
						if (checkForCollision(particleOne, particleTwo)) {
							elasticTwoDimensionalCollision(particleOne, particleTwo);
							collisionSet.add(collisionFlag);
						}
						// }
					}
				}

				setChanged();
				notifyObservers();
			}
		}, 100, period);
	}

	/*
	 * --- Movement calculations ---
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
	/* TODO wird nicht aufgerufen, benötigt? */
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
		/* TODO delete ret marker and sysout */
		boolean ret = false;
		if (c <= (particleOne.getRadius() + particleTwo.getRadius())) {
			ret = true;
			System.out
					.println("collision: " + ret + " (" + particleOne.toString() + "," + particleTwo.toString() + ")");
		}
		return ret;
	}

	/* TODO */
	protected void decideInteraction(Particle particleOne, Particle particleTwo) {
		int random = new Random().nextInt(11);
		int davidBeatsGoliath = 2;
		if (davidBeatsGoliath * particleOne.getRadius() < particleTwo.getRadius()
				&& particleOne.getVelocity() > davidBeatsGoliath * particleTwo.getVelocity()) {
			particleSplit(particleOne, particleTwo);
		} else if (random <= 2) {
			particleAbsorption(particleOne, particleTwo);
		} else if (random > 2) {
			elasticTwoDimensionalCollision(particleOne, particleTwo);
		}
	}

	/*
	 * This method detects the collision point between two particles, this can
	 * be used to add particle effects and to localize the point at which new
	 * particles are created if the bigger one gets destroyed
	 */
	/* TODO wird nicht aufgerufen, benötigt? */
	protected void detectCollisionPoint(Particle particleOne, Particle particleTwo) {

		double collisionPointX = (particleOne.getLocation().getX() * particleTwo.getRadius())
				+ (particleTwo.getLocation().getX() * particleOne.getRadius())
						/ (particleOne.getRadius() + particleTwo.getRadius());

		double collisionPointY = (particleOne.getLocation().getY() * particleTwo.getRadius())
				+ (particleTwo.getLocation().getY() * particleOne.getRadius())
						/ (particleOne.getRadius() + particleTwo.getRadius());

		Point2D collisionPoint = new Point2D.Double(collisionPointX, collisionPointY);
	}

	/*
	 * The actual collision handling with vector calculation is implemented in
	 * here. The formula for a two dimensional elastic collision can be refered
	 * here: https://de.wikipedia.org/wiki/Sto%C3%9F_%28Physik%29
	 */
	protected void elasticTwoDimensionalCollision(Particle particleOne, Particle particleTwo) {

		/*
		 * calculate the unit normal vector, this vector connects the center
		 * points of both particles. We normalize the vector afterwards to
		 * prevent strange behavior
		 */
		Vector2d vectorNormal = new Vector2d(particleTwo.getLocation().getX() - particleOne.getLocation().getX(),
				particleTwo.getLocation().getY() - particleOne.getLocation().getY());
		vectorNormal.normalize();

		/*
		 * calculate the orthogonal vector by switching x and y and multiply one
		 * of these values by -1
		 */
		Vector2d vectorTangent = new Vector2d(-vectorNormal.y, vectorNormal.x);
		vectorTangent.normalize();

		/* TODO */
		double vectorNormalOne = vectorNormal.dot(particleOne.getVector());
		double vectorTangentOne = vectorTangent.dot(particleOne.getVector());
		double vectorNormalTwo = vectorNormal.dot(particleTwo.getVector());
		double v2t = vectorTangent.dot(particleTwo.getVector());

		/* TODO */
		double v1nPrime = (vectorNormalOne * (particleOne.getMass() - particleTwo.getMass())
				+ 2. * particleTwo.getMass() * vectorNormalTwo) / (particleOne.getMass() + particleTwo.getMass());
		double v2nPrime = (vectorNormalTwo * (particleTwo.getMass() - particleOne.getMass())
				+ 2. * particleOne.getMass() * vectorNormalOne) / (particleOne.getMass() + particleTwo.getMass());

		/* TODO */
		Vector2d v_v1nPrime = new Vector2d(vectorNormal.x * v1nPrime, vectorNormal.y * v1nPrime);
		Vector2d v_v1tPrime = new Vector2d(vectorTangent.x * vectorTangentOne, vectorTangent.y * vectorTangentOne);
		Vector2d v_v2nPrime = new Vector2d(vectorNormal.x * v2nPrime, vectorNormal.y * v2nPrime);
		Vector2d v_v2tPrime = new Vector2d(vectorTangent.x * v2t, vectorTangent.y * v2t);

		/* TODO */
		Vector2d n1 = new Vector2d(v_v1nPrime.x + v_v1tPrime.x, v_v1nPrime.y + v_v1tPrime.y);
		Vector2d n2 = new Vector2d(v_v2nPrime.x + v_v2tPrime.x, v_v2nPrime.y + v_v2tPrime.y);

		/* TODO */
		particleOne.setVector(n1);
		particleTwo.setVector(n2);

	}

	/* the vector rotates about the angle around a fixed point */
	protected Vector2d rotate(Vector2d vector, double theta) {
		Point2D point = new Point2D.Double(vector.x, vector.y);
		AffineTransform.getRotateInstance(theta, 0, 0).transform(point, point);
		vector.x = point.getX();
		vector.y = point.getY();
		return vector;
	}

	/* TODO */
	protected void particleSplit(Particle particleOne, Particle particleTwo) {
		double dx = particleTwo.getLocation().getX() - particleOne.getLocation().getX();
		double dy = particleTwo.getLocation().getY() - particleOne.getLocation().getY();
		double newMass = particleTwo.getMass() / 2;
		double newVelocityParticleOne = particleOne.getVelocity() * 0.8;
		double newVelocityParticleSplit = particleOne.getVelocity() * 0.1;
		/* TODO Particle split */

	}

	/* TODO */
	protected void particleAbsorption(Particle particleOne, Particle particleTwo) {
		double random = new Random().nextInt(11);
		double dx = particleTwo.getLocation().getX() - particleOne.getLocation().getX();
		double dy = particleTwo.getLocation().getY() - particleOne.getLocation().getY();
		/* the little shift in position due to the collision */
		double particleDisplacement = Math.sqrt(dx * dx + dy * dy);
		double newMass = particleOne.getMass() + particleTwo.getMass();
		double newDensity = particleOne.getDensity() + particleTwo.getDensity();
		double newVelocity = particleTwo.getMass() / (particleDisplacement * particleDisplacement);

		particleOne.setMass(newMass);
		particleOne.setDensity(newDensity);
		particleOne.setVelocity(newVelocity);
		/* particleTwo gets destroyed */
		particleList.remove(particleTwo);

		/* in 5% of those cases, both particles are destroyed */
		if (random <= 0.5) {
			particleList.remove(particleOne);
		}
	}

	/*
	 * --- Particle handling ---
	 */
	public Particle createParticle(double x, double y) {
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
