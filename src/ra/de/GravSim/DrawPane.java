package ra.de.GravSim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class DrawPane extends JPanel {

	private Universe universe;

	// TODO: Get the particle from the particle list

	// Example particles
	private Particle particleOne = new Particle(10, 50, 50);
	private Particle particleTwo = new Particle(1000000, 500, 50);
	private Particle particleThree = new Particle(10000, 50, 500);
	private Particle particleFour = new Particle(100, 500, 500);

	public DrawPane(Universe universe) {
		this.universe = universe;
		Timer timer = new Timer(false);
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				repaint();
			}
		}, 0, 160);

	}

	@Override
	/** get the shape of a defined particle and add the color, then draw it */
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		Shape shapeOne = particleOne.getShape();
		Shape shapeTwo = particleTwo.getShape();
		Shape shapeThree = particleThree.getShape();
		Shape shapeFour = particleFour.getShape();

		Graphics2D g2d = (Graphics2D) graphics;

		// TODO: change color according to mass
		g2d.setColor(Color.red);

		g2d.fill(shapeOne);
		g2d.fill(shapeTwo);
		g2d.fill(shapeThree);
		g2d.fill(shapeFour);

	}

}
