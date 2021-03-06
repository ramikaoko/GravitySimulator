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
	 * The LinkedList which stores every particle in the universe, it has to be
	 * synchronized so multiple methods can access the list without a
	 * concurrentModificationException
	 */
	private final List<Particle> particleList = Collections.synchronizedList(new LinkedList<Particle>());

	/*
	 * The getter of our particleList is synchronized and creates a copy of the
	 * particleList<>, otherwise a concurrentModificationException occurs
	 * because the createParticle() and moveParticle() methods are trying to
	 * access simultaneously
	 */
	public synchronized LinkedList<Particle> getParticleList() {
		return new LinkedList<>(particleList);
	}

	/*
	 * The mass of a defined particle is set to 100 as the default value but it
	 * can be changed through user input in Controller()
	 */
	private double particleMass = 1000000d;

	/*
	 * Analog to particleMass, the default value is 1 and can vary between 0,1
	 * and 15
	 */
	private double particleDensity = 1d;

	/*
	 * These values are used to analyse what's happening in the simulation and
	 * to show these results in a dialog
	 */
	protected int collisionCounter = 0;

	protected int destructionCounter = 0;

	protected int bounceCounter = 0;

	protected int absorptionCounter = 0;

	protected int splitCounter = 0;

	/*
	 * The size of the contentPane, it's used for collision detection with the
	 * borders of our panel
	 */
	protected Dimension windowSize;

	/*
	 * This flag indicates whether the simulation is on halt or not, it is set
	 * in Controller() by the pauseButton
	 */
	private static boolean pauseFlag = false;

	/*
	 * This flag indicates whether the simulation has started yet, it is used by
	 * the startButton in Controller()
	 */
	private static boolean startFlag = false;

	/* A set of collisionFlags */
	TreeSet<CollisionFlag> collisionSet = new TreeSet<>();

	/*
	 * This is the timer for our timerTasks, we set it as a protected field so
	 * we can stop all timerTasks by calling the stopSimulation() method
	 */
	protected Timer timer = null;

	/* The simulationTime is set in Controller(), this is the default value */
	protected long simulationTimeInSeconds = 20;

	/*
	 * Analog to simulationTimerInSeconds, interval will be set by the user in
	 * Controller()
	 */
	protected int interval = 1;

	/* Same as simulationTimeInSeconds and interval */
	protected int particlesPerInterval = 1;

	/* The remaining simultation time in seconds */
	protected long remainingTime = 0;

	/*
	 * A flag to indicate that the simulation stopped and the evaluationDialog
	 * can be shown
	 */
	public static final String SIMULATION_FINISHED = "SimulationFinished";

	/*
	 * A LinkedList of all events that happened during a simulation, it is used
	 * in ResultDialog to plot these events
	 */
	private final LinkedList<int[]> resultList = new LinkedList<>();

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

	public long getSimulationTime() {
		return simulationTimeInSeconds;
	}

	public void setSimulationTime(long simulationTime) {
		this.simulationTimeInSeconds = simulationTime;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getParticlesPerInterval() {
		return particlesPerInterval;
	}

	public void setParticlesPerInterval(int particlesPerInterval) {
		this.particlesPerInterval = particlesPerInterval;
	}

	public long getRemainingTime() {
		return remainingTime;
	}

	/* This getter will pass our List of event values */
	public LinkedList<int[]> getEventResults() {
		return resultList;
	}

	/* This getter is used to create a legend in ResultDialog */
	public String[] getEventNames() {
		return new String[] { "Anzahl Partikel", "Kollisionen", "Abstoßungen", "Absorptionen", "Auflösungen",
				"Spaltungen" };
	}

	/*
	 * --- Constructor ---
	 */

	public Universe() {
		// Auto-generated constructor stub
	}

	/*
	 * --- Simulation starter ---
	 */

	/*
	 * Create three timerTasks which will run all calculations for the
	 * simulation, the evaluation (which will show the results in the
	 * resultDialog) and the interval settings to automatically create new
	 * particles in a defined time
	 */
	public void startSimulation() {
		if (!startFlag) {

			timer = new Timer();
			particleList.clear();
			resultList.clear();
			startFlag = true;
			/*
			 * The period is set to 15ms beacuse the drawing process needs 16ms.
			 * If we reduce both values the calculations wont be finished in
			 * time or the movement stutters
			 */
			final int period = 15;

			TimerTask simulationTask = createSimulationTask(period);
			TimerTask intervalTask = createRandomParticlePerIntervalTask();
			TimerTask evaluationTask = createEvaluationTask();

			/* To get the time in seconds we multiply it by 1000 */
			remainingTime = (simulationTimeInSeconds * 1000);

			timer.scheduleAtFixedRate(simulationTask, 0, period);
			timer.scheduleAtFixedRate(intervalTask, 0, interval * 1000);
			timer.scheduleAtFixedRate(evaluationTask, 0, 1000);

		}
	}

	/*
	 * --- Timertasks ---
	 */

	private TimerTask createSimulationTask(int period) {

		/*
		 * timeSteps will determine the calculation speed so it doesn't conflict
		 * with the time to draw the image, 1000d/15 ≈ 66 steps per cycle
		 */
		double timeSteps = 1000d / period;

		final TimerTask task = new TimerTask() {

			@Override
			public void run() {
				/* check for pause status, continue if its on false */
				if (isPausedFlag())
					return;

				remainingTime -= period;

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

				if (remainingTime < period)
					stopSimulation();

				setChanged();
				notifyObservers();
			}
		};
		return task;
	}

	private TimerTask createEvaluationTask() {
		final TimerTask task = new TimerTask() {

			@Override
			public void run() {
				int[] array = new int[6];
				/* number of particles */
				array[0] = particleList.size();
				resultList.add(array);
				array[1] = collisionCounter;
				collisionCounter = 0;
				array[2] = bounceCounter;
				bounceCounter = 0;
				array[3] = absorptionCounter;
				absorptionCounter = 0;
				array[4] = destructionCounter;
				destructionCounter = 0;
				array[5] = splitCounter;
				splitCounter = 0;
			}
		};
		return task;
	}

	protected TimerTask createRandomParticlePerIntervalTask() {

		final TimerTask task = new TimerTask() {

			@Override
			public void run() {
				if (isPausedFlag())
					return;

				/*
				 * Create a random number between 17 and maxWidth/maxHeight-17
				 * for the x and y coordinates, the actual size of the window
				 * can change and we don't want the particles to be created in
				 * the corners or near the edges. The value is 17 because the
				 * radius of a particle can't be bigger than 16.05
				 */

				for (int i = 0; i < particlesPerInterval; i++) {
					int minX = 17;
					int minY = 17;
					int maxX = windowSize.width - 17;
					int maxY = windowSize.height - 17;
					Random random = new Random();

					/* random positions within the contentPane */
					int randomX = random.nextInt((maxX - minX) + 1) + minX;
					int randomY = random.nextInt((maxY - minY) + 1) + minY;

					/* random mass between 100k and 5M */
					double randomMass = random.nextInt((5000000 - 100000) + 1) + 100000;

					/* random density between 1 and 5 */
					double randomDensity = random.nextInt((5 - 1) + 1) + 1;

					/* random velocity between 50 and 150 */
					int randomVelocity = random.nextInt((150 - 50) + 1) + 50;

					/* random vector for the direction */
					double vectorX = random.nextDouble() * (random.nextBoolean() ? 1d : -1d);
					double vectorY = random.nextDouble() * (random.nextBoolean() ? 1d : -1d);
					Vector2d randomVector = new Vector2d(vectorX, vectorY);

					/* create a particle with the calculated values */
					Particle particleRandom = createParticle(randomX, randomY, randomMass, randomDensity);
					particleRandom.setVelocity(randomVelocity);
					particleRandom.setVector(randomVector);
				}
			}
		};
		return task;
	}

	/*
	 * Stop the current simulation, delete all created particles and show the
	 * resultdialog
	 */
	protected void stopSimulation() {
		timer.cancel();

		startFlag = false;
		clearParticles();
		setChanged();
		notifyObservers(SIMULATION_FINISHED);
	}

	/*
	 * --- Particle interactions ---
	 */

	/*
	 * If a particle collides with one of the 4 site of the panel, the according
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
	 * off, gets absorbed or bot particles are destroyed
	 */
	protected void decideInteraction(Particle particleOne, Particle particleTwo) {
		/* The 11 ensures a random int from 0 to 10 */
		int random = new Random().nextInt(11);

		/*
		 * A multiplier to ensure that the bigger particle is the one which will
		 * be destroyed or splitted
		 */
		double diffValueSize = 0.5;
		double diffValueVelocity = 0.75;
		double sizeDifference = particleOne.getRadius() / particleTwo.getRadius();
		double velocityDifference = particleOne.getVelocity() / particleTwo.getVelocity();
		if (sizeDifference > (1d + diffValueSize)
				|| sizeDifference < (1d - diffValueSize) && velocityDifference > (1d + diffValueVelocity)
				|| velocityDifference < (1d - diffValueVelocity)) {
			/*
			 * If the values for size and velocity are valid, one particle (the
			 * faster one) splits the other (the bigger one)
			 */
			splitCounter++;
			if (sizeDifference > 1)
				splitParticle(particleOne, particleTwo);
			else
				splitParticle(particleTwo, particleOne);
		} else if (random > 0.1 && random <= 1) {
			/* In about 10% of all contacts an absorption occures */
			absorbParticle(particleOne, particleTwo);
			absorptionCounter++;
		} else if (random > 1) {
			/* In 90% of all contacts a collision occures */
			twoDimensionalElasticCollision(particleOne, particleTwo);
			bounceCounter++;
		} else if (random <= 0.1) {
			/* Every 100 contacts both particles are destroyed */
			particleList.remove(particleOne);
			destructionCounter++;
		}
	}

	/*
	 * This method detects the collision point between two particles, this can
	 * be used to add particle effects or to localize the point at which new
	 * particles are created if the bigger one is splitted
	 */
	protected Point2D detectCollisionPoint(Particle particleOne, Particle particleTwo) {

		Point2D pointOne = particleOne.getLocation();
		Point2D pointTwo = particleTwo.getLocation();

		Vector2d vector = new Vector2d(pointTwo.getX() - pointOne.getX(), pointTwo.getY() - pointOne.getY());
		vector.normalize();

		double newX = pointOne.getX() + (vector.x * particleOne.getRadius());
		double newY = pointOne.getY() + (vector.y * particleOne.getRadius());
		return new Point2D.Double(newX, newY);

	}

	/*
	 * The actual collision handling with vector calculation is implemented in
	 * here. The formula for a two dimensional elastic collision can be refered
	 * here: https://de.wikipedia.org/wiki/Sto%C3%9F_%28Physik%29
	 */
	protected void twoDimensionalElasticCollision(Particle particleOne, Particle particleTwo) {
		/*
		 * Calculate the unit normal vector (also called central vector), this
		 * vector connects the center points of both particles. We normalize the
		 * vector afterwards to prevent strange behavior
		 */
		Vector2d vectorCentral = new Vector2d(particleTwo.getLocation().getX() - particleOne.getLocation().getX(),
				particleTwo.getLocation().getY() - particleOne.getLocation().getY());

		/*
		 * Calculate the orthogonal vector (also called tangent vector) by
		 * switching x and y of the central vector and multiply one of these
		 * values by -1
		 */
		Vector2d vectorTangent = new Vector2d(-vectorCentral.y, vectorCentral.x);

		/*
		 * Compute the dot product or scalar product of the central and tangent
		 * vectors with the vectors of particleOne and particleTwo. These values
		 * are used to determine the velocity with which the particles will
		 * travel after the collision
		 */
		double vectorCentralOne = vectorCentral.dot(particleOne.getVector());
		double vectorTangentOne = vectorTangent.dot(particleOne.getVector());
		double vectorCentralTwo = vectorCentral.dot(particleTwo.getVector());
		double vectorTangentTwo = vectorTangent.dot(particleTwo.getVector());

		/*
		 * Calculate the new central velocity using the formula for an
		 * one-dimensional elastic collision. The mass will be included to get a
		 * physically correct solution. The tangential velocity doesn't change
		 * after the collision so we don't need to make another calculation and
		 * can use the dot product vectorTangentOne/Two for further calculations
		 */
		double newVelocityVectorCentralOne = (vectorCentralOne * (particleOne.getMass() - particleTwo.getMass())
				+ 2d * particleTwo.getMass() * vectorCentralTwo) / (particleOne.getMass() + particleTwo.getMass());
		double newVelocityVectorCentralTwo = (vectorCentralTwo * (particleTwo.getMass() - particleOne.getMass())
				+ 2d * particleOne.getMass() * vectorCentralOne) / (particleOne.getMass() + particleTwo.getMass());

		/*
		 * Multiply the vectors centralOne/Two and tangentOne/Two with the
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
		 * Calculate the actual vectors for particleOne and Two and therefore
		 * get the direction in which the particles bounce off after the
		 * collision
		 */
		Vector2d finalVectorOne = new Vector2d(vectorCentralOneWithVelocity.x + vectorTangentOneWithVelocity.x,
				vectorCentralOneWithVelocity.y + vectorTangentOneWithVelocity.y);
		Vector2d finalVectorTwo = new Vector2d(vectorCentralTwoWithVelocity.x + vectorTangentTwoWithVelocity.x,
				vectorCentralTwoWithVelocity.y + vectorTangentTwoWithVelocity.y);

		/*
		 * Extract the velocity from the final vectors, otherwise they'll be
		 * lost due to the normalize() method in Particle.setVector()
		 */
		double finalVelocityOne = finalVectorOne.length();
		double finalVelocityTwo = finalVectorTwo.length();

		/* Set the vector and velocity for both particles */
		particleOne.setVector(finalVectorOne);
		particleTwo.setVector(finalVectorTwo);
		particleOne.setVelocity(finalVelocityOne);
		particleTwo.setVelocity(finalVelocityTwo);

	}

	/*
	 * If two particles collide under certain circumstances (see
	 * decideInteraction() for conditions) it is possible that one particle
	 * splits the other (the fast), the splitted particle will always be the
	 * bigger one and breaks into two small particles. Those smaller particles
	 * will move in a 90° angle to each other and a 45° angle to the central
	 * vector of the two collided particles.
	 */
	protected void splitParticle(Particle big, Particle fast) {

		/*
		 * Calculate the collision point to detecte where we need to create to
		 * two smaller particles
		 */
		Point2D collisionPoint = detectCollisionPoint(big, fast);

		/*
		 * Each of the small particles gains half the mass of the splitted
		 * particle
		 */
		double particleSmallMass = big.getMass() / 2;

		/*
		 * Calculate the x and y component of the collision vector, the
		 * orientation is important therefore figure out in which direction the
		 * collisionVector is showing
		 */
		double vectorX = Math.abs(fast.getVector().getX() - big.getVector().getX()) * fast.getVector().x > 0 ? 1d : -1d;
		double vectorY = Math.abs(fast.getVector().getY() - big.getVector().getY()) * fast.getVector().y > 0 ? 1d : -1d;

		/*
		 * Create the collisionVector, we will use it to calculate the angle at
		 * which the two smaller particles will escape
		 */
		Vector2d collisionVector = new Vector2d(vectorX, vectorY);

		/*
		 * Create the new vector by rotating the collision vector about 45° with
		 * and angainst its current direction. As always we normalize to prevent
		 * strange behaviour
		 */
		Vector2d escapeOne = rotateVector(new Vector2d(collisionVector), Math.PI / 4d);
		escapeOne.normalize();
		Vector2d escapeTwo = rotateVector(new Vector2d(collisionVector), -Math.PI / 4d);
		escapeTwo.normalize();

		/* particleTwo gets destroyed */
		particleList.remove(big);

		/*
		 * The little shift in position due to the collision. To ensure the new
		 * particles don't overlap with the collided particles we use multiply
		 * the radius of the bigger one with 2
		 */
		double displacement = big.getRadius() * 2;

		/*
		 * Create the two small particles and enter them into the collision set
		 * to prevent further splitting with the fast particle. The density for
		 * both small particles will be the same as the density from the
		 * splitted one
		 */
		Vector2d[] vectors = new Vector2d[] { escapeOne, escapeTwo };
		Particle last = null;
		for (Vector2d vector : vectors) {

			double newX = collisionPoint.getX() + (vector.x * displacement);
			double newY = collisionPoint.getY() + (vector.y * displacement);

			Particle particleSmall = createParticle(newX, newY, particleSmallMass, big.getDensity());
			particleSmall.setVector(vector);
			particleSmall.setVelocity(fast.getVelocity());
			if (last == null)
				last = particleSmall;
			else
				collisionSet.add(new CollisionFlag(last, particleSmall));

			collisionSet.add(new CollisionFlag(fast, particleSmall));
		}
	}

	/*
	 * This method calculates the absorption process of two particles, sometimes
	 * (more precisely in 10% of all absorption cases) both particles are
	 * destroyed
	 */
	protected void absorbParticle(Particle particleOne, Particle particleTwo) {
		double dx = particleTwo.getLocation().getX() - particleOne.getLocation().getX();
		double dy = particleTwo.getLocation().getY() - particleOne.getLocation().getY();
		/* The little shift in position due to the collision */
		double displacement = Math.sqrt(dx * dx + dy * dy);
		double newMass = particleOne.getMass() + particleTwo.getMass();
		double newDensity = (particleOne.getDensity() + particleTwo.getDensity()) / 2;
		double newVelocity = particleTwo.getMass() / (displacement * displacement);

		/* particleOne assmiliates all properties of particleTwo */
		particleOne.setMass(newMass);
		particleOne.setDensity(newDensity);
		particleOne.setVelocity(newVelocity);

		/* particleTwo gets destroyed */
		particleList.remove(particleTwo);
	}

	/* The vector rotates about the angle (in rad) around a fixed point */
	protected Vector2d rotateVector(Vector2d vector, double theta) {
		Point2D point = new Point2D.Double(vector.x, vector.y);
		AffineTransform.getRotateInstance(theta, 0, 0).transform(point, point);
		vector.x = point.getX();
		vector.y = point.getY();
		return vector;
	}

	/*
	 * --- Particle creation and destruction ---
	 */

	public Particle createParticle(double x, double y) {
		return createParticle(getParticleMass(), getParticleDensity(), x, y);
	}

	public Particle createParticle(double mass, double density, double x, double y) {
		Particle particle = new Particle(mass, density, x, y);
		synchronized (particleList) {
			particleList.add(particle);
		}
		return particle;
	}

	/*
	 * Delete the content of the particleList so we can start with a fresh
	 * contentPane without drawings other than the background
	 */
	public void clearParticles() {
		synchronized (particleList) {
			particleList.clear();
		}
	}

}
