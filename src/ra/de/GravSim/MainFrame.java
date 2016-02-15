package ra.de.GravSim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

	/**
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

	/**
	 * --- Framehandling ---
	 */
	public MainFrame() {

		setTitle("Gravity Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 100, 1000, 1000);

		JPanel main = new JPanel(new BorderLayout());

		// add controlpanel
		main.add(new JPanel(), BorderLayout.WEST);

		DrawPane pane = new DrawPane(new Universe());
		pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		pane.setBackground(Color.DARK_GRAY.darker());
		main.add(pane, BorderLayout.CENTER);
		getContentPane().setLayout(new GridLayout(1, 1));
		getContentPane().add(main);
	}

}
