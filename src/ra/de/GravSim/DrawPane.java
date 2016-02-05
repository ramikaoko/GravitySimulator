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

	public DrawPane(Universe uni) {
		this.universe = uni;
		Timer t = new Timer(false);
		t.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				repaint();
			}
		}, 0, 160);

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// zeichne bild mit partikeln
		Particle p = ;

		Shape shape = p.getShape();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.red);

		g2d.fill(shape);

	}

}
