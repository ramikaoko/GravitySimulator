package de.ra.simulation;

/* We memorize two colliding particles, so those two wont collide again. we ignore collisions between them until they have separated */
public class CollisionFlag implements Comparable<CollisionFlag> {

	private final Particle particleOne;

	private final Particle particleTwo;

	public CollisionFlag(Particle particleOne, Particle particleTwo) {
		this.particleOne = particleOne;
		this.particleTwo = particleTwo;
	}

	/* set the flag to false if the collision is over */
	public boolean stillColliding(Universe universe) {
		boolean stillColliding = universe.checkForCollision(particleOne, particleTwo);
		return stillColliding;
	}

	/*
	 * two flags are equal if their particles are equal so we check if particle
	 * one and two of flag one with particle two and one of flag two
	 */
	@Override
	public int compareTo(CollisionFlag flag) {

		if ((flag.particleOne.getId() == particleOne.getId() && flag.particleTwo.getId() == particleTwo.getId())
				|| (flag.particleTwo.getId() == particleOne.getId() && flag.particleOne.getId() == particleTwo.getId()))
			return 0;

		return (particleOne.toString() + particleTwo.toString())
				.compareToIgnoreCase(flag.particleOne.toString() + flag.particleTwo.toString());
	}

}
