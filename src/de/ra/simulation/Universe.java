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

	private static boolean startFlag = false;

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

	public boolean isPausedFlag() {
		return pauseFlag;
	}

	public void setPauseFlag(boolean pauseFlag) {
		Universe.pauseFlag = pauseFlag;
	}

	public boolean isStartedFlag() {
		return startFlag;
	}

	public void setStartFlag(boolean startFlag) {
		Universe.startFlag = startFlag;
	}

	/*
	 * --- Constructor ---
	 */

	public Universe() {
		// Auto-generated constructor stub
	}

	public void startSimulation() {
		if (!startFlag) {

			startFlag = true;

			randomCreationInterval();

			Timer timer = new Timer(false);
			int delay = 100;
			int period = 15;
			/*
			 * timeSteps will determine the calculation speed so it doesn't
			 * conflict with the time to draw the image, 1000d/15 ≈ 66 steps per
			 * cycle
			 */
			double timeSteps = 1000d / period;
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {

					/* check for pause status, continue if its on false */
					if (isPausedFlag())
						return;

					List<Particle> particleListCopy = getParticleList();

					/*
					 * we go through the whole particleList and check the
					 * movement for each particle, also we check whether the
					 * particle collided with the border
					 */
					for (Particle particle : particleListCopy) {
						particle.moveParticle(timeSteps);
						bounceOffBorder(particle);
					}

					Particle particleOne;

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
			}, delay, period);
		}
	}

	protected void randomCreationInterval() {
		Timer timer = new Timer(false);
		/* multiplie by 1000 to get the time in seconds */
		int simulationTime = Controller.getSimulationTime() * 1000;
		int period = Controller.getInterval() * 1000;
		int particlePerInterval = Controller.getParticlesPerInterval();
		int delay = period;
		timer.scheduleAtFixedRate(new TimerTask() {

			/*
			 * TODO: Pause Button wirkt hier noch nicht, trotz Pause wird weiter
			 * gezeichnet
			 */

			@Override
			public void run() {

				/*
				 * Create a random number between 25 and maxWidth/maxHeight for
				 * the x and y coordinates, the actual size of the window can
				 * change and we don't want the particles to be created in the
				 * corners or near the edges.
				 */
				int minX = 25;
				int minY = 25;
				int maxX = windowSize.width - 25;
				int maxY = windowSize.height - 25;
				int randomX = new Random().nextInt(((maxX - minX) + 1) + minX);
				int randomY = new Random().nextInt(((maxY - minY) + 1) + minY);
				int randomVelocity = new Random().nextInt(((100) + 1) + 50);
				/* TODO the x and y values of the vector are always positive */
				Vector2d randomVector = new Vector2d(randomX, randomY);
				randomVector.normalize();

				Particle particleRandom = createParticle(randomX, randomY);
				particleRandom.setMass(particleMass);
				particleRandom.setDensity(particleDensity);
				particleRandom.setVelocity(randomVelocity);
				particleRandom.setVector(randomVector);

			}
		}, delay, period);
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

	/*
	 * This method decides wheter a particle splits another particle, bounces
	 * off or gets absorbed
	 */
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
				splitParticle(particleOne, particleTwo);
			else
				splitParticle(particleTwo, particleOne);

		} else if (random <= 1)

		{
			System.out.println("Reaction: absorption");
			particleAbsorption(particleOne, particleTwo);
			absorptionCounter++;
		} else if (random > 1)

		{
			System.out.println("Reaction: bounce");
			elasticTwoDimensionalCollision(particleOne, particleTwo);
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
		 * TODO: resultierende Geschwindigkeit scheint zu klein zu sein, daher
		 * resultiert vermutlich die Verklumpung der Partikel!
		 */

		/*
		 * calculate the unit normal vector (also called central vector), this
		 * vector connects the center points of both particles. We normalize the
		 * vector afterwards to prevent strange behavior
		 */
		Vector2d vectorCentral = new Vector2d(particleTwo.getLocation().getX() - particleOne.getLocation().getX(),
				particleTwo.getLocation().getY() - particleOne.getLocation().getY());
				// vectorCentral.normalize();

		/*
		 * calculate the orthogonal vector (also called tangent vector) by
		 * switching x and y of the central vector and multiply one of these
		 * values by -1
		 */
		Vector2d vectorTangent = new Vector2d(-vectorCentral.y, vectorCentral.x);
		// vectorTangent.normalize();

		/*
		 * compute the dot product or scalar product of the central and tangent
		 * vectors with the vectors of particleOne and particleTwo. These values
		 * are used to determine the velocity with which the particles will
		 * travel after the collision
		 */
		double vectorCentralOne = vectorCentral.dot(particleOne.getVector());
		double vectorTangentOne = vectorTangent.dot(particleOne.getVector());
		double vectorCentralTwo = vectorCentral.dot(particleTwo.getVector());
		double vectorTangentTwo = vectorTangent.dot(particleTwo.getVector());

		/*
		 * calculate the new central velocity using the formula for an
		 * one-dimensional elastic collision. The mass will be included to get a
		 * physically correct solution. The tangential velocity doesn't change
		 * after the collision so we don't need to make another calculation and
		 * can use the dot product vectorTangentOne and Two for further
		 * calculations
		 */
		double newVelocityVectorCentralOne = (vectorCentralOne * (particleOne.getMass() - particleTwo.getMass())
				+ 2d * particleTwo.getMass() * vectorCentralTwo) / (particleOne.getMass() + particleTwo.getMass());
		double newVelocityVectorCentralTwo = (vectorCentralTwo * (particleTwo.getMass() - particleOne.getMass())
				+ 2d * particleOne.getMass() * vectorCentralOne) / (particleOne.getMass() + particleTwo.getMass());

		/*
		 * multiply the vectors centralOne/Two and tangentOne/Two with the
		 * values of the calculated velocities
		 */
		Vector2d vectorCentralOneWithVelocity = new Vector2d(vectorCentral.x * newVelocityVectorCentralOne,
				vectorCentral.y * newVelocityVectorCentralOne);
		Vector2d vectorTangentOneWithVelocity = new Vector2d(vectorTangent.x * vectorTangentOne,
				vectorTangent.y * vectorTangentOne);
		Vector2d vectorCentralTwoWithVelocity = new Vector2d(vectorCentral.x * newVelocityVectorCentralTwo,
				vectorCentral.y * newVelocityVectorCentralTwo);
		Vector2d vectorTangentTwoWithVelocity = new Vector2d(vectorTangent.x * vectorTangentTwo,
				vectorTangent.y * vectorTangentTwo);

		/*
		 * calculate the actual vectors for particleOne and Two and therefore
		 * get the direction in which the particles bounce off after the
		 * collision.
		 */
		Vector2d finalVectorOne = new Vector2d(vectorCentralOneWithVelocity.x + vectorTangentOneWithVelocity.x,
				vectorCentralOneWithVelocity.y + vectorTangentOneWithVelocity.y);
		Vector2d finalVectorTwo = new Vector2d(vectorCentralTwoWithVelocity.x + vectorTangentTwoWithVelocity.x,
				vectorCentralTwoWithVelocity.y + vectorTangentTwoWithVelocity.y);

		/*
		 * extract the velocity from the final vectors, otherwise they'll be
		 * lost due to the normalize() method in Particle.setVector()
		 */
		double finalVelocityOne = finalVectorOne.length();
		double finalVelocityTwo = finalVectorTwo.length();

		/* set the vector for both particles */
		particleOne.setVector(finalVectorOne);
		System.out.println("finalVectorOne: " + finalVectorOne);
		particleTwo.setVector(finalVectorTwo);
		System.out.println("finalVectorTwo: " + finalVectorTwo);

		/* set the velocity for both particles */
		particleOne.setVelocity(finalVelocityOne);
		System.out.println("finalVelocityOne: " + finalVelocityOne);
		particleTwo.setVelocity(finalVelocityTwo);
		System.out.println("finalVelocityTwo: " + finalVelocityTwo);

	}

	/*
	 * if two particles collide under certain circumstances (see
	 * decideInteraction() for conditions) it is possible that one particle
	 * splits the other, the splitted particle will always be the bigger one and
	 * breaks into two smaller one. those smaller particles will move in a 90°
	 * angle to each other and a 45° angle to the central vector of the two
	 * collided particles. The splitting particle loses 20% of its speed and
	 * each of the smaller particles gets 10% of that velocity
	 */
	protected void splitParticle(Particle big, Particle fast) {

		/*
		 * calculate the collision point to detecte where we need to create to
		 * two smaller particles
		 */
		Point2D collisionPoint = detectCollisionPoint(big, fast);

		/* particleOne loses 20% of its velocity after a collision */
		fast.setVelocity(fast.getVelocity() * 0.8);

		/*
		 * TODO Geschwindigkeitsrechnung scheint unsinnig, erst Verlust dann
		 * erhalten die neuen Partikel einen Teil des Restwertes...
		 */
		double particleSmallMass = big.getMass() / 2;
		double particleSmallVelocity = fast.getVelocity() * 0.8;

		/* TODO */
		double velocityX = Math.abs(fast.getVector().getX() - big.getVector().getX()) * fast.getVector().x > 0 ? 1d
				: -1d;
		double velocityY = Math.abs(fast.getVector().getY() - big.getVector().getY()) * fast.getVector().y > 0 ? 1d
				: -1d;

		Vector2d collisionVector = new Vector2d(velocityX, velocityY);
		/* TODO normalize() ?? */

		/*
		 * create the new vector by rotating the collision vector about 45° with
		 * and angainst its current direction
		 */
		Vector2d escapeOne = rotateVector(new Vector2d(collisionVector), Math.PI / 4d);
		escapeOne.normalize();
		Vector2d escapeTwo = rotateVector(new Vector2d(collisionVector), -Math.PI / 4d);
		escapeTwo.normalize();

		particleList.remove(big);
		double distance = big.getRadius() * 2;

		/* TODO */
		Vector2d[] vectors = new Vector2d[] { escapeOne, escapeTwo };
		Particle last = null;
		for (Vector2d vector : vectors) {

			double newX = collisionPoint.getX() + (vector.x * distance);
			double newY = collisionPoint.getY() + (vector.y * distance);

			Particle particleSmall = createParticle(newX, newY);
			particleSmall.setVector(vector);
			particleSmall.setMass(particleSmallMass);
			particleSmall.setVelocity(particleSmallVelocity);
			particleSmall.setDensity(big.getDensity());
			if (last == null)
				last = particleSmall;
			else
				collisionSet.add(new CollisionFlag(last, particleSmall));

			collisionSet.add(new CollisionFlag(fast, particleSmall));
		}
		/* TODO: wofür das sysout? */
		System.out.println(particleList.size());
	}

	/*
	 * This method calculates the absorption process of two particles, sometimes
	 * (more precisely in 10% of all absorption cases) both particles are
	 * destroyed
	 */
	protected void particleAbsorption(Particle particleOne, Particle particleTwo) {
		/* the 11 ensures a random int from 0 to 10 */
		double random = new Random().nextInt(11);
		double dx = particleTwo.getLocation().getX() - particleOne.getLocation().getX();
		double dy = particleTwo.getLocation().getY() - particleOne.getLocation().getY();
		/* the little shift in position due to the collision */
		double particleDisplacement = Math.sqrt(dx * dx + dy * dy);
		double newMass = particleOne.getMass() + particleTwo.getMass();
		double newDensity = (particleOne.getDensity() + particleTwo.getDensity()) / 2;
		double newVelocity = particleTwo.getMass() / (particleDisplacement * particleDisplacement);

		/* particle one assmiliates all properties of particle two */
		particleOne.setMass(newMass);
		particleOne.setDensity(newDensity);
		particleOne.setVelocity(newVelocity);

		/* particleTwo gets destroyed */
		particleList.remove(particleTwo);

		/*
		 * in 10% of those cases (and therefore in every 100th collision), both
		 * particles are destroyed
		 */
		if (random <= 1) {
			particleList.remove(particleOne);
			System.out.println("Reaction: destruction");
			destructionCounter++;
		}
	}

	/* the vector rotates about the angle around a fixed point */
	protected Vector2d rotateVector(Vector2d vector, double theta) {
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
