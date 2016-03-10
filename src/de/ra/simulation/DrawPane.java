package de.ra.simulation;

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

/*
 * In DrawPane all drawings are processed, we use a doubleBuffer to evade
 * graphical stuttering, therefore a picture is drawn and set to the front
 * while a new picture is drawn in the back and set to the front after a
 * certain time: 
 * The Observer gets notified of every change which happens in the universe and acts accordingly
 */
@SuppressWarnings("serial")
public class DrawPane extends JPanel implements Observer {

	Universe universe;

	BufferedImage front;
	BufferedImage back;

	/* if the front image the currently shown image? -> true */
	boolean drawFront = true;

	/*
	 * If the observer gets a notification the picture is repainted, we set the
	 * period to 16ms to enable the calculation methods in universe to finish
	 * their computations within time
	 */
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

	/*
	 * Create two images which will be switched every few milliseconds to
	 * quicken the drawing process and avoid stuttering
	 */
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
		/* Draw the buffer as a rendered image */
		g2d.drawRenderedImage(buffer, AffineTransform.getTranslateInstance(0, 0));
	}

	/* Create two bufferImages on which the picture is drawn */
	private synchronized void createBuffers() {
		front = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		back = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
	}

	/*
	 * This observer method will listen for changes in Universe() and update the
	 * buffer accordingly
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		updateBuffer();
	}

	/*
	 * Check for the window size and create a synchronized access to create the
	 * buffers. A buffer contains a bufferedImage with the width and size of the
	 * window and the last updatet drawings
	 */
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

		/* Activate antialiasing & rendering for the particles */
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		/* Set the background color to dark grey */
		g2d.setColor(Color.DARK_GRAY);
		g2d.fill(bounds);

		/*
		 * Calculate the shape and color for each particle within the universe
		 */
		List<Particle> particleList = universe.getParticleList();
		for (Particle particle : particleList) {
			Shape coreShape = particle.getCoreShape();
			Shape hullShape = particle.getHullShape();

			g2d.setColor(determineColorMass(particle));
			g2d.fill(hullShape);
			g2d.setColor(determineColorDensity(particle));
			g2d.fill(coreShape);
		}
		/* Toggle the boolean to switch the images */
		drawFront = !drawFront;
	}

	/*
	 * The color of the outer circle changes from white to dark red while the
	 * mass increases, this and the radius of a particle are indicators for
	 * mass. The color changes with a stepsize of 1.6M
	 */
	private Color determineColorMass(Particle particle) {
		Color colorMass = new Color(255, 255, 255);
		double mass = particle.getMass();

		if (mass > 0 && mass <= 1600000)
			colorMass = new Color(255, 145, 0);
		else if (mass > 1600000 && mass <= 3200000)
			colorMass = new Color(255, 115, 0);
		else if (mass > 3200000 && mass <= 4800000)
			colorMass = new Color(255, 85, 0);
		else if (mass > 4800000 && mass <= 6400000)
			colorMass = new Color(255, 40, 0);
		else if (mass > 6400000 && mass <= 8000000)
			colorMass = new Color(250, 0, 0);
		else if (mass > 8000000)
			colorMass = new Color(200, 0, 0);

		return colorMass;
	}

	/*
	 * The color of the inner circle changes from light grey to black while the
	 * density increases, this and the decrease of the radius are indicators for
	 * density
	 */
	private Color determineColorDensity(Particle particle) {
		Color colorDensity = new Color(255, 255, 255);
		double density = particle.getDensity();
		if (density > 0 && density <= 1.8)
			colorDensity = new Color(230, 230, 230);
		else if (density > 1.8 && density <= 3.5)
			colorDensity = new Color(160, 160, 160);
		else if (density > 3.5 && density <= 5.2)
			colorDensity = new Color(128, 128, 128);
		else if (density > 5.2 && density <= 6.9)
			colorDensity = new Color(96, 96, 96);
		else if (density > 6.9 && density <= 8.6)
			colorDensity = new Color(64, 64, 64);
		else if (density > 8.6 && density <= 10.3)
			colorDensity = new Color(35, 35, 35);
		else if (density > 10.3)
			colorDensity = new Color(0, 0, 0);

		return colorDensity;
	}
}
