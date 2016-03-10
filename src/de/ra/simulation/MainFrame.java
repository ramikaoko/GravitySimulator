package de.ra.simulation;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/* 
 * 
 * TODO, the Todolist for this project:
 * 
 * legend: ! error / ? feature / - todo

 * ! performance bei >150 Partikeln bricht drastisch ein. 
 * - Überlappung bei Partikelerzeugung verhindern (Radiusversatz in Vektorrichtung?)
 * - konsistente, sprechende Namensgebung Namensgebung
 * - Kommentare zu allen Methoden, Klassen und Variablen schreiben
 * - Codeleichen löschen, Kommentare prüfen
 * ? Vektoren für jeden Partikel einzeichnen
 * ? JSpinner in Controller in 1^10er Schritten steigen lassen
 * ? Gravitation implementieren, Partikel ziehen sich entsprechend ihrer Massen an
 *
 */

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	Universe universe = new Universe();

	/*
	 * --- Mainframe settings and Mousehandling ---
	 */
	public MainFrame() {

		/* Set the basic parameters for the JFrame */
		setTitle("Partikelsimulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 100, 1600, 1000);

		/* The mainpanel, everything will be added to this panel */
		JPanel main = new JPanel(new BorderLayout());

		/*
		 * Create the controlpanel. This panel contains the unser controlls such
		 * as buttons, labels and so on
		 */
		JPanel control = new Controller(universe);

		/* Create the contentpanel. The drawing of particles takes place here */
		DrawPane content = new DrawPane(universe);
		content.setBorder(new EmptyBorder(5, 5, 5, 5));

		/* Add the both panels to the mainpanel */
		main.add(content, BorderLayout.CENTER);
		main.add(control, BorderLayout.WEST);

		getContentPane().setLayout(new GridLayout(1, 1));

		/*
		 * Add main, including the control- and contentpanel, to the contentPane
		 * which is the underlying instance of the window
		 */
		getContentPane().add(main);

		/* Add the mouselistener for individual control purposes */
		content.addMouseListener(new MouseAdapter() {

			/*
			 * The starting point of the mouse coursor, vector calculation
			 * starts here and a created particle will be shown at this position
			 */
			Point start;

			/*
			 * The starting point will be set at the position on which the mouse
			 * button was pressed
			 */
			@Override
			public void mousePressed(MouseEvent me) {
				start = me.getPoint();
			}

			/*
			 * The particle vector will be calculated from the startand endpoint
			 * and normalized after that, the velocity is the distance between
			 * those points multiplyed by a random value between 1 and 3 but
			 * always less than 150
			 */
			@Override
			public void mouseReleased(MouseEvent me) {
				super.mouseReleased(me);
				Point end = me.getPoint();
				/*
				 * If the mouse doesn't move we can't create particles. That is0
				 * because a particle always needs a vector, to solve this
				 * problem we calculate an initial small vector (or offset) with
				 * random values between -1 and 1 for x and y
				 */
				if (end.distance(start) < 0.1) {
					Random rand = new Random();
					double offsetX = end.getX() + (rand.nextInt(1) + 1) * (rand.nextBoolean() ? -1 : 1);
					double offsetY = end.getY() + (rand.nextInt(1) + 1) * (rand.nextBoolean() ? -1 : 1);
					end.setLocation(offsetX, offsetY);
				}
				Particle particle = universe.createParticle((int) start.getX(), (int) start.getY());
				particle.calculateVector(start, end);
			}
		});
	}

	/*
	 * --- Launch ---
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					new MainFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
