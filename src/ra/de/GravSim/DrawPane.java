package ra.de.GravSim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class DrawPane extends JPanel {

	private Universe universe;

	/* Example particles */
	// private Particle particleOne = new Particle(10, 50, 50);
	// private Particle particleTwo = new Particle(1000000, 500, 50, 1);

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

		// Shape shapeOne = (Shape) universe.particleList.get(0).getShape();
		// Shape shapeTwo = particleTwo.getShape();

		Graphics2D g2d = (Graphics2D) graphics;

		// TODO: change color according to mass
		g2d.setColor(Color.red);

		// g2d.fill(shapeOne);
		// g2d.fill(shapeTwo);
	}

	/*
	 * --- Mousehandling ---
	 */

}
