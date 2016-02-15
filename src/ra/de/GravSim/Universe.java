package ra.de.GravSim;

import java.util.LinkedList;

public class Universe {

	/** the list array which stores every particle in the universe */
	LinkedList<Particle> particleList = new LinkedList<Particle>();

	/** a defined particle */
	Particle particle;

	public void saveParticle(Particle particle) {
		particleList.add(particle);
	}

	// TODO: Math for Gravity and Distance

}
