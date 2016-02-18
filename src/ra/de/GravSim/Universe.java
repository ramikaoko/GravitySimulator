package ra.de.GravSim;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class contains the list of all created particles and the principlies
 * applying to them, such as gravitational forces.
 */
public class Universe extends TimerTask {

	private AtomicInteger particleCounter = new AtomicInteger(0);
	// TODO: Math for Gravity and Distance

	public Universe() {
		new Timer(false).scheduleAtFixedRate(this, 500, 100);
	}

	@Override
	public void run() {
		for (Particle particle : particleList) {
			particle.move();
		}

		/*
		 * TODO: DrawPane wird Listener von universe und aktualisiert sich bei
		 * Änderungen (Subscriber, Listener oder Observer sind alles das
		 * gleiche!)
		 * 
		 * Universe ist Subjekt - DrawPane ist Beobachter
		 * 
		 */
	}

	/** the list array which stores every particle in the universe */
	private final LinkedList<Particle> particleList = new LinkedList<Particle>();

	public List<Particle> getParticleList() {
		return Collections.unmodifiableList(particleList);
	}

	public Particle createParticle(float mass, float x, float y) {
		int id = particleCounter.getAndIncrement();
		Particle particle = new Particle(mass, x, y, id);
		particleList.add(particle);
		return particle;
	}

}
