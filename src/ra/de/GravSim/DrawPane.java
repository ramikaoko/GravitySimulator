package ra.de.GravSim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class DrawPane extends JPanel {

	Universe universe;
	MainFrame mainFrame;

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

		Shape shapeOne = mainFrame.particleList.get(mainFrame.particleIndex).getShape();
		Graphics2D g2d = (Graphics2D) graphics;
		// TODO: change color according to mass
		g2d.setColor(Color.red);
		g2d.fill(shapeOne);
	}
}
