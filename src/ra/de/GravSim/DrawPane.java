package ra.de.GravSim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

/* TODO */
@SuppressWarnings("serial")
public class DrawPane extends JPanel implements Observer {

	/* TODO */
	Universe universe;

	/* TODO */
	BufferedImage front;
	BufferedImage back;

	/* TODO */
	boolean drawFront = true;

	/* TODO */
	public DrawPane(Universe universe) {
		int period = 16;

		this.universe = universe;

		universe.addObserver(this);
		setDoubleBuffered(true);

		Timer timer = new Timer(false);
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				repaint();
			}
		}, 0, period);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				createBuffers();
				Dimension dimension = getSize();
				universe.setWindowSize(dimension);
			}
		});
	}

	/* TODO */
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		Graphics2D g2d = (Graphics2D) graphics;
		BufferedImage buffer;
		synchronized (this) {
			if (drawFront)
				buffer = front;
			else
				buffer = back;
		}
		/* TODO Is this still necessary? */
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g2d.drawRenderedImage(buffer, AffineTransform.getTranslateInstance(0, 0));
	}

	/* TODO */
	private synchronized void createBuffers() {
		front = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		back = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
	}

	/*
	 * this observer method will listen for changes in Universe() and update
	 * accordingly
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		updateBuffer();
	}

	/* TODO */
	private synchronized void updateBuffer() {
		Graphics2D g2d;
		Rectangle bounds;
		if (getHeight() == 0 || getWidth() == 0) {
			return;
		}
		synchronized (this) {

			if (front == null || back == null)
				createBuffers();
			if (drawFront) {
				g2d = back.createGraphics();
				bounds = new Rectangle(0, 0, back.getWidth(), back.getHeight());
			} else {
				g2d = front.createGraphics();
				bounds = new Rectangle(0, 0, front.getWidth(), front.getHeight());
			}
		}

		/* activate antialiasing & rendering for the particles */
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		g2d.setColor(Color.DARK_GRAY);
		g2d.fill(bounds);
		List<Particle> particleList = universe.getParticleList();
		for (Particle particle : particleList) {
			Shape coreShape = particle.getCoreShape();
			Shape hullShape = particle.getHullShape();

			g2d.setColor(determineColorMass(particle));
			g2d.fill(hullShape);
			g2d.setColor(determineColorDensity(particle));
			g2d.fill(coreShape);
		}
		drawFront = !drawFront;
	}

	/* TODO */
	private Color determineColorMass(Particle particle) {
		Color cMass = new Color(255, 255, 255);
		double mass = particle.getMass();

		if (mass > 0 && mass <= 40)
			cMass = new Color(255, 255, 255);
		else if (mass > 40 && mass <= 400)
			cMass = new Color(255, 145, 0);
		else if (mass > 400 && mass <= 4000)
			cMass = new Color(255, 115, 0);
		else if (mass > 4000 && mass <= 40000)
			cMass = new Color(255, 85, 0);
		else if (mass > 40000 && mass <= 400000)
			cMass = new Color(255, 50, 0);
		else if (mass > 400000 && mass <= 4000000)
			cMass = new Color(230, 15, 0);
		else if (mass > 400000)
			cMass = new Color(210, 0, 0);

		return cMass;
	}

	/* TODO */
	private Color determineColorDensity(Particle particle) {
		Color cDensity = new Color(255, 255, 255);
		double density = particle.getDensity();
		if (density > 0 && density <= 1.8)
			cDensity = new Color(230, 230, 230);
		else if (density > 1.8 && density <= 3.5)
			cDensity = new Color(160, 160, 160);
		else if (density > 3.5 && density <= 5.2)
			cDensity = new Color(128, 128, 128);
		else if (density > 5.2 && density <= 6.9)
			cDensity = new Color(96, 96, 96);
		else if (density > 6.9 && density <= 8.6)
			cDensity = new Color(64, 64, 64);
		else if (density > 8.6 && density <= 10.3)
			cDensity = new Color(35, 35, 35);
		else if (density > 10.3)
			cDensity = new Color(0, 0, 0);

		return cDensity;
	}
}
