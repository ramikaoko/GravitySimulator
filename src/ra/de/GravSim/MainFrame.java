package ra.de.GravSim;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/* 
 * TODO:
 * - concurentModificationException
 * - Partikelkollision implementieren
 * - Abstandsberechnung implementieren
 * - Partikelspaltung implementieren
 * - Gravitation implementieren
 * - Pfeil zur Veranschaulichung des Vektors
 * - Kommentare zu allen Methoden, Klassen und Variablen schreiben
 * ? JSpinner in Controller in 1^10er Schritten steigen lassen
 * ? zeitlicher Abfall der Geschwindigkeit
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
		setTitle("Gravity Simulator");
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

			public void mousePressed(MouseEvent me) {
				start = me.getPoint();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				super.mouseReleased(me);
				Point end = me.getPoint();
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
