package de.ra.simulation;

/* TODO */
public class CollisionFlag {

	private final Particle particleOne;

	private final Particle particleTwo;

	public CollisionFlag(Particle particleOne, Particle particleTwo) {
		this.particleOne = particleOne;
		this.particleTwo = particleTwo;
	}

	public boolean stillColliding(Universe universe) {
		return universe.checkForCollision(particleOne, particleTwo);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CollisionFlag) {
			CollisionFlag flag = (CollisionFlag) obj;
			return flag.particleOne == particleOne && flag.particleTwo == particleTwo
					|| flag.particleTwo == particleOne && flag.particleOne == particleTwo;
		}

		return false;
	}

}
