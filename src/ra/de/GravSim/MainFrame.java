package ra.de.GravSim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

	/** an instance of universe */
	Universe universe = new Universe();

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

		DrawPane pane = new DrawPane(universe);
		pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		pane.setBackground(Color.DARK_GRAY.darker());
		main.add(pane, BorderLayout.CENTER);
		getContentPane().setLayout(new GridLayout(1, 1));
		getContentPane().add(main);

		// add mouselistener
		pane.addMouseListener(new MouseAdapter() {

			Point start;

			public void mousePressed(MouseEvent me) {
				start = me.getPoint();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				super.mouseReleased(me);
				Point end = me.getPoint();

				// TODO: set mass value to the choosen value
				Particle p = universe.createParticle(500000, (int) end.getX(), (int) end.getY());
				p.setVector(start, end);
			}
		});

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
