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

public class DrawPane extends JPanel implements Observer {

	Universe universe;

	BufferedImage front;
	BufferedImage back;

	boolean drawFront = true;

	public DrawPane(Universe universe) {
		int period = 16;

		this.universe = universe;

		universe.addObserver(this);
		setDoubleBuffered(true);
		Dimension size = getSize();

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
			}
		});

	}

	/** get the shape of a defined particle and add the color, then draw it */
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

		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g2d.drawRenderedImage(buffer, AffineTransform.getTranslateInstance(0, 0));

	}

	/** TODO */
	private synchronized void createBuffers() {
		front = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		back = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
	}

	/**
	 * this observer method will listen for changes in Universe() and update
	 * accordingly
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		updateBuffer();
	}

	/** TODO */
	private synchronized void updateBuffer() {
		Graphics2D g2d;
		Rectangle bounds;
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

		g2d.setColor(Color.DARK_GRAY.darker());
		g2d.fill(bounds);
		/* get the to-be-drawn particle out of the particleList */
		List<Particle> particleList = universe.getParticleList();
		for (Particle particle : particleList) {
			Shape shape = particle.getShape();
			// TODO: change color according to mass
			g2d.setColor(Color.red);
			g2d.fill(shape);
		}

		drawFront = !drawFront;
	}
}
