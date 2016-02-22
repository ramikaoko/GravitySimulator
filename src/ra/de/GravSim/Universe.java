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

	/* TODO */
	public List<Particle> getParticleList() {
		return particleList;
	}

	/*
	 * the particleCounter is used to get a specific particle out of the
	 * particleList, this is useful for managing the list after a collison and
	 * other things. It is an AtomicInteger so we can automatically
	 * getAndIncrement it after each particle is created
	 */
	private AtomicInteger particleCounter = new AtomicInteger(0);

	/* the list array which stores every particle in the universe */
	private final List<Particle> particleList = Collections.synchronizedList(new LinkedList<Particle>());

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

	/* the size of the contentPane used for collision detection */
	private Dimension windowSize;

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
		 * with the time to draw the image, 1000d/15 = 66fps
		 */
		double timeSteps = 1000d / period;
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				for (Particle particle : particleList) {
					particle.moveParticle(timeSteps);
					bounceOffBorder(particle);
					particleCollision(particle);
				}
				setChanged();
				notifyObservers();
			}
		}, 100, period);
	}

	/*
	 * --- Movement calculations ---
	 * 
	 * TODO: Methods for Gravity, Distance and collision
	 */

	/* TODO */
	protected void bounceOffBorder(Particle particle) {
		Rectangle2D bounds2d = particle.getHullShape().getBounds2D();
		if (bounds2d.getMinX() <= 0 || bounds2d.getMaxX() >= windowSize.getWidth()) {
			Point2D vector = particle.getVector();
			particle.setVector(new Point2D.Double(vector.getX() * -1d, vector.getY()));
		} else if (bounds2d.getMinY() <= 0 || bounds2d.getMaxY() >= windowSize.getHeight()) {
			Point2D vector = particle.getVector();
			particle.setVector(new Point2D.Double(vector.getX(), vector.getY() * -1d));
		}
	}

	/* TODO */
	protected void particleCollision(Particle particle) {
		/* TODO */
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
