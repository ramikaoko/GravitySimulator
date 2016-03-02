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
 * ! fehler, ? mögl. feature, - todo
 * 
 * TODO:
 * ! Problem bei Kollision: Partikel überlappen sich manchmal
 * - Überlappung bei Partikelerzeugung verhindern (Radiusversatz in Vektorrichtung?)
 * - Option: Vektoren für jeden Partikel einzeichnen
 * - Button: Auswertung
 * - Auswertungsfenster mit Zahlenwerten und Diagram
 * - Kollisionen/s, Spaltugen/s
 * - konsistente Namensgebung
 * - Codeleichen löschen, Kommentare prüfen
 * - Kommentare zu allen Methoden, Klassen und Variablen schreiben
 * ? JSpinner in Controller in 1^10er Schritten steigen lassen
 * ? Gravitation implementieren
 */

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	/* an instance of universe */
	Universe universe = new Universe();

	/* particleCounter takes count of the created particles and */
	protected int particleIndex = 0;

	/*
	 * --- Mainframe settings and Mousehandling ---
	 */
	public MainFrame() {

		/* set the basic parameters for the JFrame */
		setTitle("Partikelsimulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 100, 1000, 1000);

		/* create the mainpanel */
		JPanel main = new JPanel(new BorderLayout());

		/* create the controlpanel */
		JPanel control = new Controller(universe);

		/* create the contentpanel */
		DrawPane content = new DrawPane(universe);
		content.setBorder(new EmptyBorder(5, 5, 5, 5));

		/* add the contentpanel to the mainpanel */
		main.add(content, BorderLayout.CENTER);

		/* add the controlpanel to the mainpanel */
		main.add(control, BorderLayout.WEST);
		getContentPane().setLayout(new GridLayout(1, 1));

		/* add main with control- and contentpanel to the contentPane */
		getContentPane().add(main);

		/* add the mouselistener for control purposes */
		content.addMouseListener(new MouseAdapter() {

			/*
			 * the starting point of the mouse coursor, vector calculation
			 * starts here and a created particle will be shown at this position
			 */
			Point start;

			/*
			 * the starting point gets set at the psoition on which the mouse
			 * button was pressed
			 */
			@Override
			public void mousePressed(MouseEvent me) {
				start = me.getPoint();
			}

			/*
			 * the particle vector will be calculated from the startand endpoint
			 * and normalized after that, the velocity is the distance between
			 * those points
			 */
			@Override
			public void mouseReleased(MouseEvent me) {
				super.mouseReleased(me);
				Point end = me.getPoint();
				/*
				 * if the mouse doesn't move we can't create particles because
				 * they need a vector, so we give each particle an initial small
				 * vactor with random values between -1 and 1 for x and y
				 */
				if (end.distance(start) < 0.1) {
					Random rand = new Random();
					double x = end.getX() + (rand.nextInt(1) + 1) * (rand.nextBoolean() ? -1 : 1);
					double y = end.getY() + (rand.nextInt(1) + 1) * (rand.nextBoolean() ? -1 : 1);
					end.setLocation(x, y);
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
