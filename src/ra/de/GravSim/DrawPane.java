package ra.de.GravSim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class DrawPane extends JPanel {

	Universe universe;

	BufferedImage front;
	BufferedImage back;

	public DrawPane(Universe universe) {
		Dimension size = getSize();
		front = new BufferedImage(size.width, size.height, BufferedImage.TYPE_3BYTE_BGR);
		back = new BufferedImage(size.width, size.height, BufferedImage.TYPE_3BYTE_BGR);
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

		Graphics2D g2d = (Graphics2D) graphics;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		List<Particle> particleList = universe.getParticleList();
		for (Particle particle : particleList) {
			Shape shape = particle.getShape();
			// TODO: change color according to mass
			g2d.setColor(Color.red);
			g2d.fill(shape);
		}
	}
}
