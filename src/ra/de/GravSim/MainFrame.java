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
	 * Launch the application.
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
	 * Create the frame.
	 */
	public MainFrame() {
		// TODO Auto-generated constructor stub
		setTitle("Gravity Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		JPanel main = new JPanel(new BorderLayout());
		main.add(new JPanel(), BorderLayout.WEST);// Controls
		DrawPane pane = new DrawPane(new Universe());
		pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		pane.setBackground(Color.DARK_GRAY.darker());
		main.add(pane, BorderLayout.CENTER);
		getContentPane().setLayout(new GridLayout(1, 1));
		getContentPane().add(main);
	}

}
