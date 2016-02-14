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
	private Particle particle;

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
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		// get the particle shape and color, then draw it
		Shape shape = particle.getShape();
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.setColor(Color.red);
		g2d.fill(shape);

	}

}
