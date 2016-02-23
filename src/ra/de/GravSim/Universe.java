package ra.de.GravSim;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

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
	private double particleMass = 100d;

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

	private boolean firstCollisionOccuredFlag;

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

	/* TODO */
	protected void bounceOffBorder(Particle particle) {
		Rectangle2D bounds = particle.getHullShape().getBounds2D();
		if (bounds.getMinX() <= 0 || bounds.getMaxX() >= windowSize.getWidth()) {
			Point2D vector = particle.getVector();
			particle.setVector(new Point2D.Double(vector.getX() * -1d, vector.getY()));
		} else if (bounds.getMinY() <= 0 || bounds.getMaxY() >= windowSize.getHeight()) {
			Point2D vector = particle.getVector();
			particle.setVector(new Point2D.Double(vector.getX(), vector.getY() * -1d));
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
			elasticCollision(particleOne, particleTwo);
		}
	}

	/*
	 * The actual collision handling with velocity/vector calculation is
	 * implemented in here. The formula for an elastic collision (which this
	 * will be for now) is:
	 * 
	 * v1' = 2*((m1*v1 + m2*v2)/(m1+m2))-v1 with as the velocity and m as the
	 * mass for the particles
	 */
	protected void elasticCollision(Particle particleOne, Particle particleTwo) {

		/*
		 * the variables used in the calculation are shortened so the
		 * calculation for each velocity is not 3 lines long and difficult to
		 * read
		 */
		double massDiffOne = particleOne.getMass() - particleTwo.getMass();
		double massDiffTwo = particleTwo.getMass() - particleOne.getMass();
		double massSum = particleOne.getMass() + particleTwo.getMass();
		double pOneX = particleOne.getVector().getX();
		double pOneY = particleOne.getVector().getY();
		double pTwoX = particleTwo.getVector().getX();
		double pTwoY = particleTwo.getVector().getY();

		/* new X and Y velocitys for particleOne */
		double newParticleOneVelocityX = pOneX * massDiffOne + (2 * particleTwo.getMass() * pTwoX) / (massSum);
		double newParticleOneVelocityY = pOneY * massDiffOne + (2 * particleTwo.getMass() * pTwoY) / (massSum);

		/* new X and Y velocitys for particleTwo */
		double newParticleTwoVelocityX = pTwoX * massDiffTwo + (2 * particleOne.getMass() * pOneX) / (massSum);
		double newParticleTwoVelocityY = pTwoY * massDiffTwo + (2 * particleOne.getMass() * pOneY) / (massSum);

		/* apply the new velocitys */
		particleOne.setVector(new Point2D.Double(newParticleOneVelocityX, newParticleOneVelocityY));
		particleTwo.setVector(new Point2D.Double(newParticleTwoVelocityX, newParticleTwoVelocityY));
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
}
