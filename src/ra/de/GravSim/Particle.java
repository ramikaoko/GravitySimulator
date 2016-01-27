package ra.de.GravSim;

public class Particle {

	private int mass;
	private double radius;
	private int x; // x coords
	private int y; // y coords
	private int velX; // x velocity
	private int velY; // y velocity
	private int accX; // x acceleration
	private int accY; // y acceleration
	private int prevX; // previous coord of x
	private int prevY; // previous coord of y
	private int color; // appropriate color for each mass (1, 100, 1000, 10000, ...)
	private boolean collisionFlag; // indicate whether a particle collided with another one
	
	public Particle(int mass, int x, int y, int vx, int vy) {

		this.mass = mass;
		this.x = x;
		this.y = y;
		this.velX = vx;
		this.velY = vy;
		this.accX = 0;
		this.accY = 0;
		this.prevX = x;
		this.prevY = y;
		this.collisionFlag = false;
		this.radius = Math.log(Math.E+mass/1000);
	}
	
}
