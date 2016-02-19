package ra.de.GravSim;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class contains the list of all created particles and the principlies
 * applying to them, such as gravitational forces. It is observable by drawPane
 * and will notify it if changes occur.
 */
public class Universe extends Observable {

	/**
	 * the particleCounter is used to get a specific particle out of the
	 * particleList, this is unseful for managing the list after a collison and
	 * other things. It is an AtomicInteger so we can automatically
	 * getAndIncrement it after each particle is created
	 */
	private AtomicInteger particleCounter = new AtomicInteger(0);

	/** the list array which stores every particle in the universe */
	private final LinkedList<Particle> particleList = new LinkedList<Particle>();

	/*
	 * particleList is set to read-only so all classes except universe can't
	 * change its content. Attempts to modify the returned list, whether direct
	 * or via its iterator, result in an UnsupportedOperationException
	 */
	public List<Particle> getParticleList() {
		// return Collections.unmodifiableList(particleList);
		return new LinkedList<>(particleList);
	}

	/*
	 * --- TimerTask ---
	 */

	public Universe() {
		Timer timer = new Timer(false);
		int period = 15;
		double times = 1000d / period;
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				for (Particle particle : particleList) {
					particle.moveParticle(times);
				}
				setChanged();
				notifyObservers();
			}
		}, 100, period);
	}

	/*
	 * --- calculations ---
	 */

	// TODO: Math for Gravity and Distance

	/*
	 * --- Constructor ---
	 */
	public Particle createParticle(double mass, double x, double y) {
		particleCounter.getAndIncrement();
		Particle particle = new Particle(mass, x, y);
		particleList.add(particle);
		return particle;
	}

}
