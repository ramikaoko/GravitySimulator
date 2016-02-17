package ra.de.GravSim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

	/** an instance of universe */
	Universe universe = new Universe();

	/** a defined particle */
	// Particle(mass, x, y, id)
	Particle particle;

	/** the list array which stores every particle in the universe */
	protected LinkedList<Particle> particleList = new LinkedList<Particle>();

	/** particleCounter takes count of the created particles and */
	protected int particleIndex = 0;

	/*
	 * --- Framehandling ---
	 */
	public MainFrame() {

		// set the windows basic parameter
		setTitle("Gravity Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 100, 1000, 1000);

		// add mainpanel
		JPanel main = new JPanel(new BorderLayout());

		// add controlpanel
		main.add(new JPanel(), BorderLayout.WEST);

		// add mouselistener
		addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent me) {
				System.out.println("clicked: " + particleIndex + " times!");

				// TODO: set mass value to the choosen value
				// TODO: set x & y at the mouseposition
				particle = new Particle(1000, 500, 500, particleIndex);
				particleList.add(particle);
				particleIndex++;

				/*
				 * - click -> create particle -> save it in the list -> draw it
				 * at the clickposition
				 */

				/*
				 * - drag -> create particle -> get velocity by compute the
				 * distance -> save in the list -> draw at the first click
				 * position
				 */
			}
		});

		DrawPane pane = new DrawPane(new Universe());
		pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		pane.setBackground(Color.DARK_GRAY.darker());
		main.add(pane, BorderLayout.CENTER);
		getContentPane().setLayout(new GridLayout(1, 1));
		getContentPane().add(main);

	}

	/*
	 * --- Launch ---
	 */
	public static void main(String[] args) {
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
