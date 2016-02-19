package ra.de.GravSim;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/* 
 * TODO:
 * - Masse über die Knöpfe veränderbar machen
 * - Bewegungsgeschwindigkeit über die Knöpfe veränderbar machen
 * - Partikelfarbe entsprechend der Masse ändern
 * - Kommentare zu allen Methoden, Klassen und Variablen schreiben
 * ? (Partikel von den Seiten abprallen lassen)
 * - Gravitation implementieren
 * - Abstandsberechnung implementieren
 */

public class MainFrame extends JFrame {

	/** an instance of universe */
	Universe universe = new Universe();

	/** particleCounter takes count of the created particles and */
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
		JPanel control = new JPanel();
		/* and initialize it */
		initializeControlPanel(control);

		/* create the contentpanel */
		DrawPane pane = new DrawPane(universe);
		pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		/* add the universepanel */
		main.add(pane, BorderLayout.CENTER);
		/* add the controlpanel */
		main.add(control, BorderLayout.WEST);
		getContentPane().setLayout(new GridLayout(1, 1));
		getContentPane().add(main);

		/* add the mouselistener for control purposes */
		pane.addMouseListener(new MouseAdapter() {

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
				Particle p = universe.createParticle(500000, (int) start.getX(), (int) start.getY());
				p.calculateVector(start, end);
			}
		});

	}

	/* controlpanel GUI options will be set here */
	public void initializeControlPanel(JPanel panel) {
		JButton button;
		JLabel label;
		JTextField textField;
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		/* TODO: return the chosen mass and use it for particle creation */

		label = new JLabel("Masse: ", (int) CENTER_ALIGNMENT);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(label, gbc);

		textField = new JTextField(" ");
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(textField, gbc);

		label = new JLabel(" ");
		gbc.gridy++;
		panel.add(label, gbc);

		button = new JButton("winzig");
		gbc.gridy++;
		panel.add(button, gbc);

		button = new JButton("klein");
		gbc.gridy++;
		panel.add(button, gbc);

		button = new JButton("mittel");
		gbc.gridy++;
		panel.add(button, gbc);

		button = new JButton("groß");
		gbc.gridy++;
		panel.add(button, gbc);

		button = new JButton("gewaltig");
		gbc.gridy++;
		panel.add(button, gbc);

		button = new JButton("gigantisch");
		gbc.gridy++;
		panel.add(button, gbc);

		label = new JLabel(" ");
		gbc.gridy++;
		panel.add(label, gbc);

		button = new JButton("Protoscheibe");
		gbc.gridy++;
		panel.add(button, gbc);
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
