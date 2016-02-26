package de.ra.simulation;

/* TODO */
public class CollisionFlag implements Comparable<CollisionFlag> {

	private final Particle particleOne;

	private final Particle particleTwo;

	public CollisionFlag(Particle particleOne, Particle particleTwo) {
		this.particleOne = particleOne;
		this.particleTwo = particleTwo;
	}

	/* TODO */
	public boolean stillColliding(Universe universe) {
		boolean still = universe.checkForCollision(particleOne, particleTwo);
		System.out.println("still: " + still + " (" + particleOne.toString() + "," + particleTwo.toString() + ")");
		return still;
	}

	/* TODO */
	@Override
	public int compareTo(CollisionFlag flag) {

		if ((flag.particleOne.getId() == particleOne.getId() && flag.particleTwo.getId() == particleTwo.getId())
				|| (flag.particleTwo.getId() == particleOne.getId() && flag.particleOne.getId() == particleTwo.getId()))
			return 0;

		return (particleOne.toString() + particleTwo.toString())
				.compareToIgnoreCase(flag.particleOne.toString() + flag.particleTwo.toString());
	}

}
