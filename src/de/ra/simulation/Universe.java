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

	/* A bunch of counter to analyse whats happening in the simulation */
	private int creationCounter = 0;
	private int collisionCounter = 0;
	private int destructionCounter = 0;
	private int bounceCounter = 0;
	private int absorptionCounter = 0;
	private int splitCounter = 0;

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

	/* A set of collisionFlags */
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

		int period = 15;
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
				 * we go through the whole particleList and check the movement
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

						CollisionFlag collisionFlag = new CollisionFlag(particleOne, particleTwo);

						if (collisionSet.contains(collisionFlag)) {
							if (collisionFlag.stillColliding(Universe.this))
								continue;
							collisionSet.remove(collisionFlag);
						}

						if (checkForCollision(particleOne, particleTwo)) {
							decideInteraction(particleOne, particleTwo);
							collisionSet.add(collisionFlag);
							collisionCounter++;
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
		if (c <= (particleOne.getRadius() + particleTwo.getRadius())) {
			return true;
		}
		return false;
	}

	/* TODO */
	protected void decideInteraction(Particle particleOne, Particle particleTwo) {
		/* The 11 ensures a random int from 0 to 10 */
		int random = new Random().nextInt(11);

		/*
		 * A multiplier to ensure that the bigger particle is the one which will
		 * be destroyed or splitted
		 */
		double diffValue = 0.3;

		double sizeDifference = particleOne.getRadius() / particleTwo.getRadius();
		if (sizeDifference > (1d + diffValue) || sizeDifference < (1d - diffValue)) {
			System.out.println("Reaction: split");
			splitCounter++;
			if (sizeDifference > 1)
				particleSplit(particleOne, particleTwo);
			else
				particleSplit(particleTwo, particleOne);

		} else if (random <= 2)

		{
			particleAbsorption(particleOne, particleTwo);
			System.out.println("Reaction: absorption");
			absorptionCounter++;
		} else if (random > 2)

		{
			elasticTwoDimensionalCollision(particleOne, particleTwo);
			System.out.println("Reaction: bounce");
			bounceCounter++;
		}

	}

	/*
	 * This method detects the collision point between two particles, this can
	 * be used to add particle effects or to localize the point at which new
	 * particles are created if the bigger one gets destroyed
	 */
	protected Point2D detectCollisionPoint(Particle particleOne, Particle particleTwo) {

		Point2D p1 = particleOne.getLocation();
		Point2D p2 = particleTwo.getLocation();

		Vector2d vec = new Vector2d(p2.getX() - p1.getX(), p2.getY() - p1.getY());
		vec.normalize();

		double nx = p1.getX() + (vec.x * particleOne.getRadius());
		double ny = p1.getY() + (vec.y * particleOne.getRadius());
		return new Point2D.Double(nx, ny);

	}

	/*
	 * The actual collision handling with vector calculation is implemented in
	 * here. The formula for a two dimensional elastic collision can be refered
	 * here: https://de.wikipedia.org/wiki/Sto%C3%9F_%28Physik%29
	 */
	protected void elasticTwoDimensionalCollision(Particle particleOne, Particle particleTwo) {

		/*
		 * calculate the unit normal vector (also called central vector), this
		 * vector connects the center points of both particles. We normalize the
		 * vector afterwards to prevent strange behavior
		 */
		Vector2d vectorCentral = new Vector2d(particleTwo.getLocation().getX() - particleOne.getLocation().getX(),
				particleTwo.getLocation().getY() - particleOne.getLocation().getY());
		vectorCentral.normalize();

		/*
		 * calculate the orthogonal vector (or tangent vector) by switching x
		 * and y and multiply one of these values by -1
		 */
		Vector2d vectorTangent = new Vector2d(-vectorCentral.y, vectorCentral.x);
		vectorTangent.normalize();

		/* TODO */
		double vectorNormalOne = vectorCentral.dot(particleOne.getVector());
		double vectorTangentOne = vectorTangent.dot(particleOne.getVector());
		double vectorNormalTwo = vectorCentral.dot(particleTwo.getVector());
		double vectorTangentTwo = vectorTangent.dot(particleTwo.getVector());

		/* TODO umbenennen und kommentieren */
		double v1nPrime = (vectorNormalOne * (particleOne.getMass() - particleTwo.getMass())
				+ 2. * particleTwo.getMass() * vectorNormalTwo) / (particleOne.getMass() + particleTwo.getMass());
		double v2nPrime = (vectorNormalTwo * (particleTwo.getMass() - particleOne.getMass())
				+ 2. * particleOne.getMass() * vectorNormalOne) / (particleOne.getMass() + particleTwo.getMass());

		/* TODO */
		Vector2d v_v1nPrime = new Vector2d(vectorCentral.x * v1nPrime, vectorCentral.y * v1nPrime);
		Vector2d v_v1tPrime = new Vector2d(vectorTangent.x * vectorTangentOne, vectorTangent.y * vectorTangentOne);
		Vector2d v_v2nPrime = new Vector2d(vectorCentral.x * v2nPrime, vectorCentral.y * v2nPrime);
		Vector2d v_v2tPrime = new Vector2d(vectorTangent.x * vectorTangentTwo, vectorTangent.y * vectorTangentTwo);

		/* TODO */
		Vector2d n1 = new Vector2d(v_v1nPrime.x + v_v1tPrime.x, v_v1nPrime.y + v_v1tPrime.y);
		Vector2d n2 = new Vector2d(v_v2nPrime.x + v_v2tPrime.x, v_v2nPrime.y + v_v2tPrime.y);

		/* TODO */
		particleOne.setVector(n1);
		particleTwo.setVector(n2);

		bounceCounter++;
	}

	/*
	 * if two particles collide under certain circumstances (see
	 * decideInteraction() for conditions) it is possible that one particle
	 * splits the other, the splitted particle will always be the bigger one and
	 * breaks into two smaller one. those smaller particles will move in a 90°
	 * angle to each other and a 45° angle to the central vector of the two
	 * collided particles. the splitting particles loses 20% of its speed and
	 * each of the smaller particles gets 10% of that velocity
	 */
	protected void particleSplit(Particle big, Particle fast) {

		/*
		 * calculate the collision point to detecte where we need to create to
		 * two smaller particles
		 */
		Point2D collisionPoint = detectCollisionPoint(big, fast);

		/* particleOne loses 20% of its velocity after a collision */
		fast.setVelocity(fast.getVelocity() * 0.8);

		double particleSmallMass = big.getMass() / 2;
		double particleSmallVelocity = fast.getVelocity() * 0.8;

		double vx = Math.abs(fast.getVector().getX() - big.getVector().getX()) * fast.getVector().x > 0 ? 1d : -1d;
		double vy = Math.abs(fast.getVector().getY() - big.getVector().getY()) * fast.getVector().y > 0 ? 1d : -1d;

		Vector2d collisionVector = new Vector2d(vx, vy);

		Vector2d escapeOne = rotate(new Vector2d(collisionVector), Math.PI / 4d);
		escapeOne.normalize();
		Vector2d escapeTwo = rotate(new Vector2d(collisionVector), -Math.PI / 4d);
		escapeTwo.normalize();

		particleList.remove(big);
		double distance = big.getRadius() * 2;

		Vector2d[] vectors = new Vector2d[] { escapeOne, escapeTwo };
		Particle last = null;
		for (Vector2d vec : vectors) {

			double nx = collisionPoint.getX() + (vec.x * distance);
			double ny = collisionPoint.getY() + (vec.y * distance);

			Particle small = createParticle(nx, ny);
			small.setVector(vec);
			small.setMass(particleSmallMass);
			small.setVelocity(particleSmallVelocity);
			small.setDensity(big.getDensity());
			if (last == null)
				last = small;
			else
				collisionSet.add(new CollisionFlag(last, small));

			collisionSet.add(new CollisionFlag(fast, small));
		}
		System.out.println(particleList.size());
	}

	/* TODO */
	protected void particleAbsorption(Particle particleOne, Particle particleTwo) {
		/* the 11 ensures a random int from 0 to 10 */
		double random = new Random().nextInt(11);
		double dx = particleTwo.getLocation().getX() - particleOne.getLocation().getX();
		double dy = particleTwo.getLocation().getY() - particleOne.getLocation().getY();
		/* the little shift in position due to the collision */
		double particleDisplacement = Math.sqrt(dx * dx + dy * dy);
		double newMass = particleOne.getMass() + particleTwo.getMass();
		double newDensity = particleOne.getDensity() + particleTwo.getDensity();
		double newVelocity = particleTwo.getMass() / (particleDisplacement * particleDisplacement);

		/* particle one assmiliates all properties of particle two */
		particleOne.setMass(newMass);
		particleOne.setDensity(newDensity);
		particleOne.setVelocity(newVelocity);

		/* particleTwo gets destroyed */
		particleList.remove(particleTwo);
		absorptionCounter++;

		/* in 5% of those cases, both particles are destroyed */
		if (random <= 0.5) {
			particleList.remove(particleOne);
			destructionCounter++;
		}
	}

	/* the vector rotates about the angle around a fixed point */
	protected Vector2d rotate(Vector2d vector, double theta) {
		Point2D point = new Point2D.Double(vector.x, vector.y);
		AffineTransform.getRotateInstance(theta, 0, 0).transform(point, point);
		vector.x = point.getX();
		vector.y = point.getY();
		return vector;
	}

	/*
	 * --- Particle handling ---
	 */
	public Particle createParticle(double x, double y) {
		Particle particle = new Particle(getParticleMass(), getParticleDensity(), x, y);
		synchronized (particleList) {
			particleList.add(particle);
			creationCounter++;
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
